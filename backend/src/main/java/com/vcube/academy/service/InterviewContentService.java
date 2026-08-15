package com.vcube.academy.service;

import com.vcube.academy.dto.interview.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewContentService {

    private final InterviewCategoryRepository categoryRepository;
    private final InterviewTopicRepository topicRepository;
    private final CompanyRepository companyRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewStudentProgressRepository progressRepository;
    private final CompanyInterviewQuestionRepository companyQuestionRepository;

    @Transactional(readOnly = true)
    public List<InterviewCategoryDto> getCategories(Long userId) {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(c -> mapCategoryToDto(c, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterviewTopicDto> getTopicsByCategory(Long categoryId, Long userId) {
        return topicRepository.findByCategoryIdAndIsActiveTrueOrderByDisplayOrderAsc(categoryId).stream()
                .map(t -> mapTopicToDto(t, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompanySummaryDto> getCompanies() {
        return companyRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(comp -> CompanySummaryDto.builder()
                        .id(comp.getId())
                        .name(comp.getName())
                        .slug(comp.getSlug())
                        .logoUrl(comp.getLogoUrl())
                        .description(comp.getDescription())
                        .industry(comp.getIndustry())
                        .tier(comp.getTier())
                        .totalQuestions(companyQuestionRepository.findByCompanyId(comp.getId()).size())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyDetailDto getCompanyDetail(Long companyId, Long userId) {
        Company comp = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        List<InterviewQuestionSummaryDto> questions = questionRepository.findByCompanyId(companyId).stream()
                .map(q -> mapQuestionToSummaryDto(q, userId))
                .collect(Collectors.toList());

        return CompanyDetailDto.builder()
                .id(comp.getId())
                .name(comp.getName())
                .slug(comp.getSlug())
                .logoUrl(comp.getLogoUrl())
                .description(comp.getDescription())
                .industry(comp.getIndustry())
                .tier(comp.getTier())
                .hiringRoundsInfo(comp.getHiringRoundsInfo())
                .totalQuestions(questions.size())
                .questions(questions)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<InterviewQuestionSummaryDto> searchQuestions(
            Long topicId,
            Long categoryId,
            InterviewDifficulty difficulty,
            InterviewQuestionType type,
            String search,
            int page,
            int size,
            Long userId
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<InterviewQuestion> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates.getExpressions().add(cb.isTrue(root.get("isPublished")));

            if (topicId != null) {
                predicates.getExpressions().add(cb.equal(root.get("topic").get("id"), topicId));
            }
            if (categoryId != null) {
                predicates.getExpressions().add(cb.equal(root.get("topic").get("category").get("id"), categoryId));
            }
            if (difficulty != null) {
                predicates.getExpressions().add(cb.equal(root.get("difficulty"), difficulty));
            }
            if (type != null) {
                predicates.getExpressions().add(cb.equal(root.get("questionType"), type));
            }
            if (search != null && !search.trim().isEmpty()) {
                String like = "%" + search.toLowerCase().trim() + "%";
                predicates.getExpressions().add(cb.or(
                        cb.like(cb.lower(root.get("questionText")), like),
                        cb.like(cb.lower(root.get("expectedAnswer")), like)
                ));
            }
            return predicates;
        };

        return questionRepository.findAll(spec, pageable).map(q -> mapQuestionToSummaryDto(q, userId));
    }

    @Transactional(readOnly = true)
    public InterviewQuestionDetailDto getQuestionDetail(Long questionId, Long userId) {
        InterviewQuestion q = questionRepository.findById(questionId)
                .filter(InterviewQuestion::getIsPublished)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        boolean isCompleted = false;
        Double lastScore = 0.0;
        int practiceCount = 0;

        if (userId != null) {
            var prog = progressRepository.findByUserIdAndQuestionId(userId, questionId);
            if (prog.isPresent()) {
                isCompleted = prog.get().getIsCompleted();
                lastScore = prog.get().getLastScore();
                practiceCount = prog.get().getPracticeCount();
            }
        }

        List<String> companyNames = q.getCompanyMappings().stream()
                .map(cm -> cm.getCompany().getName())
                .collect(Collectors.toList());

        return InterviewQuestionDetailDto.builder()
                .id(q.getId())
                .topicId(q.getTopic().getId())
                .topicName(q.getTopic().getName())
                .categoryId(q.getTopic().getCategory().getId())
                .categoryName(q.getTopic().getCategory().getName())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .difficulty(q.getDifficulty())
                .interviewRound(q.getInterviewRound())
                .questionSource(q.getQuestionSource())
                .sourceReference(q.getSourceReference())
                .expectedAnswer(q.getExpectedAnswer())
                .explanation(q.getExplanation())
                .interviewPoints(q.getInterviewPoints())
                .commonMistakes(q.getCommonMistakes())
                .followUpQuestions(q.getFollowUpQuestions())
                .realWorldExample(q.getRealWorldExample())
                .isCompleted(isCompleted)
                .lastScore(lastScore)
                .practiceCount(practiceCount)
                .companies(companyNames)
                .build();
    }

    private InterviewCategoryDto mapCategoryToDto(InterviewCategory cat, Long userId) {
        long totalQuestions = questionRepository.countByCategoryId(cat.getId());
        long completed = (userId != null) ? progressRepository.countCompletedByUserIdAndCategoryId(userId, cat.getId()) : 0;

        List<InterviewTopicDto> topicDtos = cat.getTopics().stream()
                .filter(InterviewTopic::getIsActive)
                .map(t -> mapTopicToDto(t, userId))
                .collect(Collectors.toList());

        return InterviewCategoryDto.builder()
                .id(cat.getId())
                .name(cat.getName())
                .slug(cat.getSlug())
                .description(cat.getDescription())
                .icon(cat.getIcon())
                .displayOrder(cat.getDisplayOrder())
                .totalTopics(topicDtos.size())
                .totalQuestions(totalQuestions)
                .completedQuestions(completed)
                .topics(topicDtos)
                .build();
    }

    private InterviewTopicDto mapTopicToDto(InterviewTopic topic, Long userId) {
        long totalQ = questionRepository.countByTopicId(topic.getId());
        long completed = (userId != null) ? progressRepository.countCompletedByUserIdAndTopicId(userId, topic.getId()) : 0;

        return InterviewTopicDto.builder()
                .id(topic.getId())
                .categoryId(topic.getCategory().getId())
                .categoryName(topic.getCategory().getName())
                .name(topic.getName())
                .slug(topic.getSlug())
                .description(topic.getDescription())
                .displayOrder(topic.getDisplayOrder())
                .totalQuestions(totalQ)
                .completedQuestions(completed)
                .build();
    }

    private InterviewQuestionSummaryDto mapQuestionToSummaryDto(InterviewQuestion q, Long userId) {
        boolean completed = false;
        Double lastScore = null;

        if (userId != null) {
            var prog = progressRepository.findByUserIdAndQuestionId(userId, q.getId());
            if (prog.isPresent()) {
                completed = prog.get().getIsCompleted();
                lastScore = prog.get().getLastScore();
            }
        }

        List<String> companyNames = q.getCompanyMappings().stream()
                .map(cm -> cm.getCompany().getName())
                .collect(Collectors.toList());

        return InterviewQuestionSummaryDto.builder()
                .id(q.getId())
                .topicId(q.getTopic().getId())
                .topicName(q.getTopic().getName())
                .categoryId(q.getTopic().getCategory().getId())
                .categoryName(q.getTopic().getCategory().getName())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .difficulty(q.getDifficulty())
                .interviewRound(q.getInterviewRound())
                .questionSource(q.getQuestionSource())
                .sourceReference(q.getSourceReference())
                .isCompleted(completed)
                .lastScore(lastScore)
                .companies(companyNames)
                .build();
    }
}
