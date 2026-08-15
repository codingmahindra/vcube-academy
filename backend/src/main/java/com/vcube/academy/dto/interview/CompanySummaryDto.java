package com.vcube.academy.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySummaryDto {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
    private String industry;
    private String tier;
    private long totalQuestions;
}
