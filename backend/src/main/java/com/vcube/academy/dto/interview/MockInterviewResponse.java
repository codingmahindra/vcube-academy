package com.vcube.academy.dto.interview;

import com.vcube.academy.entity.MockInterviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInterviewResponse {
    private Long id;
    private String title;
    private String roleTitle;
    private Long targetCompanyId;
    private String targetCompanyName;
    private String interviewType;
    private String difficulty;
    private Integer totalQuestions;
    private Integer currentQuestionIndex;
    private MockInterviewStatus status;
    private Double overallScore;
    private Integer interviewReadinessPercentage;
    private String recommendationStatus;
    private List<MockInterviewQuestionDto> questions;
}
