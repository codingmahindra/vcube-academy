package com.vcube.academy.dto.dsa;

import com.vcube.academy.entity.DsaDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsaProblemDetailDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private DsaDifficulty difficulty;
    private Long categoryId;
    private String categoryName;
    private String subtopic;
    private String constraints;
    private String inputFormat;
    private String outputFormat;
    private String expectedApproach;
    private String timeComplexity;
    private String spaceComplexity;
    private String hints;
    private String interviewPoints;
    private String companyTags;
    private String javaStarterCode;
    private String solutionExplanation;
    private String solutionJavaCode;
    private List<DsaTestCaseDto> sampleTestCases;
    private Boolean isSolved;
}
