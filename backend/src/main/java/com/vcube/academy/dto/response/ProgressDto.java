package com.vcube.academy.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ProgressDto {

    private Long courseId;
    private String courseTitle;
    private String courseSlug;
    private int topicsCompleted;
    private int totalTopics;
    private int quizAttempts;
    private int totalCorrect;
    private int totalAttemptedQuestions;
    private BigDecimal overallAccuracy;
    private Instant lastActivityAt;
    private List<WeakTopicDto> weakTopics;

    @Data
    @Builder
    public static class WeakTopicDto {
        private Long topicId;
        private String topicTitle;
        private int totalQuestions;
        private int correctCount;
        private BigDecimal accuracyPct;
        private Instant lastAttemptedAt;
    }
}
