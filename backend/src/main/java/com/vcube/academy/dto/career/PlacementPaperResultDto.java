package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementPaperResultDto {
    private Long attemptId;
    private Long paperId;
    private String paperTitle;
    private String companyName;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int unanswered;
    private BigDecimal scoreObtained;
    private BigDecimal percentage;
    private boolean isPassed;
    private int passingMarks;
    private int totalMarks;
    private Instant startTime;
    private Instant endTime;
    private List<SectionScore> sectionScores;
    private List<PlacementPaperQuestionDto> questionsWithAnswers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionScore {
        private String sectionName;
        private int total;
        private int correct;
        private BigDecimal accuracy;
    }
}
