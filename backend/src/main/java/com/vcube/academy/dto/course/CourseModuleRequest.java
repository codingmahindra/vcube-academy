package com.vcube.academy.dto.course;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CourseModuleRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    private String description;

    private Integer displayOrder = 0;
}
