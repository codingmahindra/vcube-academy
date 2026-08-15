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
public class InterviewEvaluationResponse {
    private Long evaluationId;
    private Long questionId;
    private Double score;
    private String feedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingPoints;
    private String improvedAnswer;
    private String expectedAnswer;
    private String explanation;
}
