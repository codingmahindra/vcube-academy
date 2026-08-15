package com.vcube.academy.dto.dsa;

import com.vcube.academy.entity.DsaDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsaProblemRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    private String title;

    private String slug;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Difficulty is required")
    private DsaDifficulty difficulty;

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

    @NotBlank(message = "Java starter code is required")
    private String javaStarterCode;

    private String solutionExplanation;
    private String solutionJavaCode;
    private Boolean isPublished;

    private List<DsaTestCaseRequest> testCases;
}
