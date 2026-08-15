package com.vcube.academy.dto.course;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CourseDetailDto {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String title;
    private String slug;
    private String description;
    private String difficulty;
    private Integer estimatedHours;
    private Boolean isPublished;
    private Integer displayOrder;
    private List<CourseModuleDto> modules;
    private Instant createdAt;
    private Instant updatedAt;
}
