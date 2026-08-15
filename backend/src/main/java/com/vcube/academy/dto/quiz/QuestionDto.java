package com.vcube.academy.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Question DTO sent to the student BEFORE they answer.
 * CRITICAL: Does NOT include isCorrect on options. Never expose correct answer before submission.
 */
@Data
@Builder
public class QuestionDto {
    private Long id;
    private String questionText;
    private String difficulty;
    private List<OptionDto> options;

    @Data
    @Builder
    public static class OptionDto {
        private Long id;
        private String optionLabel; // A, B, C, D
        private String optionText;
        // isCorrect intentionally omitted — never sent before submission
    }
}
