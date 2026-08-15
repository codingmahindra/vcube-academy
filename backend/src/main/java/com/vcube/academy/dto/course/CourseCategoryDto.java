package com.vcube.academy.dto.course;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CourseCategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private Integer displayOrder;
    private Boolean isActive;
    private Long courseCount;
    private Instant createdAt;
}
