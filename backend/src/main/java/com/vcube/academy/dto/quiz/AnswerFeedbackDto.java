package com.vcube.academy.dto.quiz;

import lombok.Builder;
import lombok.Data;

/**
 * Response after a student submits an answer.
 * Reveals correct answer + explanation AFTER submission only.
 */
@Data
@Builder
public class AnswerFeedbackDto {
    private Long questionId;
    private Long selectedOptionId;
    private String selectedOptionLabel;
    private Long correctOptionId;
    private String correctOptionLabel;
    @com.fasterxml.jackson.annotation.JsonProperty("isCorrect")
    private boolean isCorrect;
    private String explanation;
    private String interviewPoint;
    private int currentIndex;       // 0-based index of this question
    private int totalQuestions;
    private boolean isLastQuestion;
    private Long attemptId;
}
