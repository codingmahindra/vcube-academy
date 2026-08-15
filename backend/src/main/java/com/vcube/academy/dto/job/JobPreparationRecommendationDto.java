package com.vcube.academy.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPreparationRecommendationDto {
    private List<Map<String, Object>> recommendedCourses;
    private List<Map<String, Object>> recommendedDsaProblems;
    private List<Map<String, Object>> recommendedInterviewQuestions;
    private List<String> technicalChecklist;
    private String recommendedMockInterviewRole;
}
