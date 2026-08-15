package com.vcube.academy.service;

import com.vcube.academy.dto.dsa.DsaProblemDetailDto;
import com.vcube.academy.dto.dsa.DsaProblemRequest;
import com.vcube.academy.dto.dsa.DsaTestCaseRequest;
import com.vcube.academy.entity.DsaCategory;
import com.vcube.academy.entity.DsaProblem;
import com.vcube.academy.entity.DsaTestCase;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DsaTrainerAdminService {

    private final DsaCategoryRepository categoryRepository;
    private final DsaProblemRepository problemRepository;
    private final DsaTestCaseRepository testCaseRepository;
    private final DsaSubmissionRepository submissionRepository;
    private final DsaStudentProgressRepository progressRepository;
    private final DsaProblemService problemService;

    @Transactional
    public DsaProblemDetailDto createProblem(DsaProblemRequest request) {
        DsaCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("DSA Category", "id", request.getCategoryId()));

        String slug = request.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = request.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }

        DsaProblem problem = DsaProblem.builder()
                .category(category)
                .title(request.getTitle())
                .slug(slug)
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .subtopic(request.getSubtopic())
                .constraints(request.getConstraints())
                .inputFormat(request.getInputFormat())
                .outputFormat(request.getOutputFormat())
                .expectedApproach(request.getExpectedApproach())
                .timeComplexity(request.getTimeComplexity())
                .spaceComplexity(request.getSpaceComplexity())
                .hints(request.getHints())
                .interviewPoints(request.getInterviewPoints())
                .companyTags(request.getCompanyTags())
                .javaStarterCode(request.getJavaStarterCode())
                .solutionExplanation(request.getSolutionExplanation())
                .solutionJavaCode(request.getSolutionJavaCode())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : true)
                .build();

        problem = problemRepository.save(problem);

        if (request.getTestCases() != null && !request.getTestCases().isEmpty()) {
            for (DsaTestCaseRequest tcReq : request.getTestCases()) {
                DsaTestCase tc = DsaTestCase.builder()
                        .problem(problem)
                        .input(tcReq.getInput())
                        .expectedOutput(tcReq.getExpectedOutput())
                        .isSample(tcReq.getIsSample() != null ? tcReq.getIsSample() : false)
                        .isHidden(tcReq.getIsHidden() != null ? tcReq.getIsHidden() : true)
                        .explanation(tcReq.getExplanation())
                        .displayOrder(tcReq.getDisplayOrder() != null ? tcReq.getDisplayOrder() : 0)
                        .build();
                testCaseRepository.save(tc);
            }
        }

        return problemService.getProblemDetail(problem.getId(), null);
    }

    @Transactional
    public DsaProblemDetailDto updateProblem(Long problemId, DsaProblemRequest request) {
        DsaProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("DSA Problem", "id", problemId));

        if (request.getCategoryId() != null) {
            DsaCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("DSA Category", "id", request.getCategoryId()));
            problem.setCategory(category);
        }

        if (request.getTitle() != null) problem.setTitle(request.getTitle());
        if (request.getDescription() != null) problem.setDescription(request.getDescription());
        if (request.getDifficulty() != null) problem.setDifficulty(request.getDifficulty());
        if (request.getSubtopic() != null) problem.setSubtopic(request.getSubtopic());
        if (request.getConstraints() != null) problem.setConstraints(request.getConstraints());
        if (request.getInputFormat() != null) problem.setInputFormat(request.getInputFormat());
        if (request.getOutputFormat() != null) problem.setOutputFormat(request.getOutputFormat());
        if (request.getExpectedApproach() != null) problem.setExpectedApproach(request.getExpectedApproach());
        if (request.getTimeComplexity() != null) problem.setTimeComplexity(request.getTimeComplexity());
        if (request.getSpaceComplexity() != null) problem.setSpaceComplexity(request.getSpaceComplexity());
        if (request.getHints() != null) problem.setHints(request.getHints());
        if (request.getInterviewPoints() != null) problem.setInterviewPoints(request.getInterviewPoints());
        if (request.getCompanyTags() != null) problem.setCompanyTags(request.getCompanyTags());
        if (request.getJavaStarterCode() != null) problem.setJavaStarterCode(request.getJavaStarterCode());
        if (request.getSolutionExplanation() != null) problem.setSolutionExplanation(request.getSolutionExplanation());
        if (request.getSolutionJavaCode() != null) problem.setSolutionJavaCode(request.getSolutionJavaCode());
        if (request.getIsPublished() != null) problem.setIsPublished(request.getIsPublished());

        problemRepository.save(problem);

        return problemService.getProblemDetail(problem.getId(), null);
    }

    @Transactional
    public void deleteProblem(Long problemId) {
        DsaProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("DSA Problem", "id", problemId));
        problemRepository.delete(problem);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAdminDsaStats() {
        long totalProblems = problemRepository.count();
        long totalCategories = categoryRepository.count();
        long totalSubmissions = submissionRepository.count();
        long totalTestCases = testCaseRepository.count();

        return Map.of(
                "totalProblems", totalProblems,
                "totalCategories", totalCategories,
                "totalSubmissions", totalSubmissions,
                "totalTestCases", totalTestCases
        );
    }
}
