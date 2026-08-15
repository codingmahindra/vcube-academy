package com.vcube.academy.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewProgressSummaryDto {
    private long totalQuestions;
    private long completedQuestions;
    private int readinessPercentage;
    private long totalMockInterviews;
    private long completedMockInterviews;
    private Double averageMockScore;
    private List<InterviewCategoryDto> categoryProgress;
    private List<String> strongTopics;
    private List<String> weakTopics;
}
