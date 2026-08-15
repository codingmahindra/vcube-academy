package com.vcube.academy.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StudentStatsDto {
    private int totalCoursesEnrolled;
    private int totalTopicsCompleted;
    private int totalQuizAttempts;
    private int totalCorrectAnswers;
    private int totalAttemptedQuestions;
    private BigDecimal overallAccuracy;
    private List<ProgressDto> courseProgress;
}
