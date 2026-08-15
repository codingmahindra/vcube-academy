package com.vcube.academy.service;

import com.vcube.academy.dto.dsa.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DsaProblemService {

    private final DsaCategoryRepository categoryRepository;
    private final DsaProblemRepository problemRepository;
    private final DsaTestCaseRepository testCaseRepository;
    private final DsaStudentProgressRepository progressRepository;

    @Transactional(readOnly = true)
    public List<DsaCategoryDto> getCategories(Long userId) {
        List<DsaCategory> categories = categoryRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc();
        return categories.stream().map(c -> {
            long total = problemRepository.countPublishedByCategoryId(c.getId());
            long solved = (userId != null) ? progressRepository.countSolvedByUserIdAndCategoryId(userId, c.getId()) : 0;
            return DsaCategoryDto.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .slug(c.getSlug())
                    .description(c.getDescription())
                    .icon(c.getIcon())
                    .displayOrder(c.getDisplayOrder())
                    .totalProblems(total)
                    .solvedProblems(solved)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<DsaProblemSummaryDto> getProblems(Long categoryId, DsaDifficulty difficulty, String search,
                                                 String statusFilter, Pageable pageable, Long userId) {
        Specification<DsaProblem> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isPublished"), true));

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (difficulty != null) {
                predicates.add(cb.equal(root.get("difficulty"), difficulty));
            }
            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), term),
                        cb.like(cb.lower(root.get("description")), term),
                        cb.like(cb.lower(root.get("subtopic")), term)
                ));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<DsaProblem> pageResult = problemRepository.findAll(spec, pageable);

        Set<Long> solvedProblemIds = new HashSet<>();
        Set<Long> attemptedProblemIds = new HashSet<>();

        if (userId != null) {
            List<DsaStudentProgress> userProgress = progressRepository.findByUserId(userId);
            for (DsaStudentProgress p : userProgress) {
                attemptedProblemIds.add(p.getProblem().getId());
                if (Boolean.TRUE.equals(p.getIsSolved())) {
                    solvedProblemIds.add(p.getProblem().getId());
                }
            }
        }

        List<DsaProblemSummaryDto> dtos = pageResult.getContent().stream()
                .filter(p -> {
                    if ("SOLVED".equalsIgnoreCase(statusFilter)) {
                        return solvedProblemIds.contains(p.getId());
                    } else if ("UNSOLVED".equalsIgnoreCase(statusFilter)) {
                        return !solvedProblemIds.contains(p.getId());
                    }
                    return true;
                })
                .map(p -> DsaProblemSummaryDto.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .slug(p.getSlug())
                        .difficulty(p.getDifficulty())
                        .categoryId(p.getCategory().getId())
                        .categoryName(p.getCategory().getName())
                        .subtopic(p.getSubtopic())
                        .companyTags(p.getCompanyTags())
                        .isSolved(solvedProblemIds.contains(p.getId()))
                        .isAttempted(attemptedProblemIds.contains(p.getId()))
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
    }

    @Transactional(readOnly = true)
    public DsaProblemDetailDto getProblemDetail(Long problemId, Long userId) {
        DsaProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("DSA Problem", "id", problemId));
        return buildProblemDetailDto(problem, userId);
    }

    @Transactional(readOnly = true)
    public DsaProblemDetailDto getProblemDetailBySlug(String slug, Long userId) {
        DsaProblem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("DSA Problem", "slug", slug));
        return buildProblemDetailDto(problem, userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getHints(Long problemId) {
        DsaProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("DSA Problem", "id", problemId));
        return Map.of("problemId", problemId, "hints", problem.getHints() != null ? problem.getHints() : "[]");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSolution(Long problemId, Long userId) {
        DsaProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("DSA Problem", "id", problemId));

        return Map.of(
                "problemId", problemId,
                "title", problem.getTitle(),
                "expectedApproach", problem.getExpectedApproach() != null ? problem.getExpectedApproach() : "",
                "timeComplexity", problem.getTimeComplexity() != null ? problem.getTimeComplexity() : "",
                "spaceComplexity", problem.getSpaceComplexity() != null ? problem.getSpaceComplexity() : "",
                "explanation", problem.getSolutionExplanation() != null ? problem.getSolutionExplanation() : "",
                "solutionJavaCode", problem.getSolutionJavaCode() != null ? problem.getSolutionJavaCode() : ""
        );
    }

    private DsaProblemDetailDto buildProblemDetailDto(DsaProblem problem, Long userId) {
        boolean isSolved = false;
        if (userId != null) {
            isSolved = progressRepository.findByUserIdAndProblemId(userId, problem.getId())
                    .map(DsaStudentProgress::getIsSolved)
                    .orElse(false);
        }

        List<DsaTestCaseDto> sampleTestCases = testCaseRepository.findByProblemIdAndIsSampleTrueOrderByDisplayOrderAsc(problem.getId())
                .stream().map(tc -> DsaTestCaseDto.builder()
                        .id(tc.getId())
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .isSample(true)
                        .isHidden(false)
                        .explanation(tc.getExplanation())
                        .build())
                .collect(Collectors.toList());

        return DsaProblemDetailDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .slug(problem.getSlug())
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty())
                .categoryId(problem.getCategory().getId())
                .categoryName(problem.getCategory().getName())
                .subtopic(problem.getSubtopic())
                .constraints(problem.getConstraints())
                .inputFormat(problem.getInputFormat())
                .outputFormat(problem.getOutputFormat())
                .expectedApproach(problem.getExpectedApproach())
                .timeComplexity(problem.getTimeComplexity())
                .spaceComplexity(problem.getSpaceComplexity())
                .hints(problem.getHints())
                .interviewPoints(problem.getInterviewPoints())
                .companyTags(problem.getCompanyTags())
                .javaStarterCode(problem.getJavaStarterCode())
                .sampleTestCases(sampleTestCases)
                .isSolved(isSolved)
                .build();
    }
}
