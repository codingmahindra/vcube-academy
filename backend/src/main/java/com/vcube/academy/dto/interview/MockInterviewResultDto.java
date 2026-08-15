package com.vcube.academy.dto.interview;

import com.vcube.academy.entity.InterviewReadiness;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInterviewResultDto {
    private Long id;
    private String title;
    private String roleTitle;
    private String targetCompanyName;
    private String interviewType;
    private String difficulty;
    private Integer totalQuestions;
    private Double overallScore;
    private Double technicalScore;
    private Double javaScore;
    private Double sqlScore;
    private Double springScore;
    private Double dsaScore;
    private Double hrScore;
    private Double communicationScore;
    private Integer interviewReadinessPercentage;
    private InterviewReadiness recommendationStatus;
    private String feedbackSummary;
    private List<String> strongAreas;
    private List<String> weakAreas;
    private List<String> recommendedRevisionTopics;
    private List<MockInterviewQuestionDto> questionEvaluations;
    private Instant createdAt;
    private Instant completedAt;
}
