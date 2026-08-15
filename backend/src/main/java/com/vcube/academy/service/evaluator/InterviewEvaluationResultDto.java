package com.vcube.academy.service.evaluator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewEvaluationResultDto {
    private Double score;
    private Double technicalAccuracyScore;
    private Double completenessScore;
    private Double clarityScore;
    private String feedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingPoints;
    private String improvedAnswer;
}
