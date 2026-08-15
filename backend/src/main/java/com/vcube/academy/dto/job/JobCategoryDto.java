package com.vcube.academy.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private long totalJobs;
}
