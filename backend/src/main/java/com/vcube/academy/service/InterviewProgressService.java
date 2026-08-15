package com.vcube.academy.service;

import com.vcube.academy.dto.interview.InterviewCategoryDto;
import com.vcube.academy.dto.interview.InterviewProgressSummaryDto;
import com.vcube.academy.dto.interview.InterviewQuestionSummaryDto;
import com.vcube.academy.entity.InterviewCategory;
import com.vcube.academy.entity.InterviewStudentProgress;
import com.vcube.academy.entity.MockInterviewStatus;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewProgressService {

    private final InterviewQuestionRepository questionRepository;
    private final InterviewCategoryRepository categoryRepository;
    private final InterviewStudentProgressRepository progressRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final InterviewContentService contentService;

    @Transactional(readOnly = true)
    public InterviewProgressSummaryDto getProgress(Long userId) {
        long totalQuestions = questionRepository.count();
        long completedQuestions = progressRepository.countByUserIdAndIsCompletedTrue(userId);

        long totalMocks = mockInterviewRepository.findByUserIdOrderByCreatedAtDesc(userId, org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        long completedMocks = mockInterviewRepository.countByUserIdAndStatus(userId, MockInterviewStatus.COMPLETED);
        Double avgMockScore = mockInterviewRepository.getAverageScoreByUserId(userId);
        if (avgMockScore == null) avgMockScore = 0.0;

        int readinessPct = totalQuestions > 0 ? (int) Math.round(((double) completedQuestions / totalQuestions) * 100.0) : 0;
        if (completedMocks > 0) {
            readinessPct = (int) Math.round((readinessPct * 0.5) + (avgMockScore * 0.5));
        }

        List<InterviewCategoryDto> categoryProgress = contentService.getCategories(userId);

        List<InterviewStudentProgress> studentProgressList = progressRepository.findByUserId(userId);
        List<String> strongTopics = new ArrayList<>();
        List<String> weakTopics = new ArrayList<>();

        for (InterviewStudentProgress p : studentProgressList) {
            String tName = p.getQuestion().getTopic().getName();
            if (p.getIsCompleted() && (p.getLastScore() != null && p.getLastScore() >= 70.0)) {
                strongTopics.add(tName);
            } else {
                weakTopics.add(tName);
            }
        }

        return InterviewProgressSummaryDto.builder()
                .totalQuestions(totalQuestions)
                .completedQuestions(completedQuestions)
                .readinessPercentage(Math.min(100, readinessPct))
                .totalMockInterviews(totalMocks)
                .completedMockInterviews(completedMocks)
                .averageMockScore(Math.round(avgMockScore * 10.0) / 10.0)
                .categoryProgress(categoryProgress)
                .strongTopics(strongTopics.stream().distinct().collect(Collectors.toList()))
                .weakTopics(weakTopics.stream().distinct().collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRecommendations(Long userId) {
        List<InterviewStudentProgress> progList = progressRepository.findByUserId(userId);
        Set<Long> completedQIds = progList.stream()
                .filter(InterviewStudentProgress::getIsCompleted)
                .map(p -> p.getQuestion().getId())
                .collect(Collectors.toSet());

        // Find uncompleted questions
        List<InterviewQuestionSummaryDto> recommendedQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getIsPublished() && !completedQIds.contains(q.getId()))
                .limit(5)
                .map(q -> InterviewQuestionSummaryDto.builder()
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
                        .isCompleted(false)
                        .companies(q.getCompanyMappings().stream().map(cm -> cm.getCompany().getName()).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("recommendedQuestions", recommendedQuestions);
        response.put("recommendedRevisionTopics", List.of("Multithreading & Concurrency", "SQL Joins & Indexing", "Spring Boot Internals", "STAR Behavioral Method"));
        response.put("targetCompanies", List.of("TCS", "Infosys", "Amazon", "JPMorgan Chase", "Accenture"));

        return response;
    }
}
