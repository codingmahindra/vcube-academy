package com.vcube.academy.dto.course;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @Size(max = 200, message = "Slug must be at most 200 characters")
    private String slug; // auto-generated if blank

    private String description;

    @Pattern(regexp = "BEGINNER|INTERMEDIATE|ADVANCED",
             message = "Difficulty must be BEGINNER, INTERMEDIATE, or ADVANCED")
    private String difficulty = "BEGINNER";

    @Min(value = 1, message = "Estimated hours must be at least 1")
    @Max(value = 1000, message = "Estimated hours must be at most 1000")
    private Integer estimatedHours;

    private Boolean isPublished = false;

    private Integer displayOrder = 0;
}
