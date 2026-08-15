package com.vcube.academy.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class QuizResultDto {
    private Long resultId;
    private Long attemptId;
    private String quizType;
    private Long topicId;
    private String topicTitle;
    private Long courseId;
    private String courseTitle;
    private int totalQuestions;
    private int attemptedCount;
    private int correctCount;
    private int wrongCount;
    private int skippedCount;
    private BigDecimal scorePercentage;
    private int timeTakenSeconds;
    private String grade;         // A, B, C, D, F based on percentage
    private Instant completedAt;
    private List<AnswerReviewDto> answers;

    @Data
    @Builder
    public static class AnswerReviewDto {
        private Long questionId;
        private String questionText;
        private String difficulty;
        private Long selectedOptionId;
        private String selectedOptionLabel;
        private String selectedOptionText;
        private Long correctOptionId;
        private String correctOptionLabel;
        private String correctOptionText;
        private boolean isCorrect;
        private String explanation;
    }
}
