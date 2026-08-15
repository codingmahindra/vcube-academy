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
public class CompanyDetailDto {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
    private String industry;
    private String tier;
    private String hiringRoundsInfo;
    private long totalQuestions;
    private List<InterviewQuestionSummaryDto> questions;
}
