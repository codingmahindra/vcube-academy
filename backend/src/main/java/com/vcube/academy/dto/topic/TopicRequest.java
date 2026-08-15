package com.vcube.academy.dto.topic;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TopicRequest {

    @NotNull(message = "Module ID is required")
    private Long moduleId;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @Size(max = 200, message = "Slug must be at most 200 characters")
    private String slug; // auto-generated if blank

    @Pattern(regexp = "EASY|MEDIUM|HARD", message = "Difficulty must be EASY, MEDIUM, or HARD")
    private String difficulty = "EASY";

    @Min(value = 1, message = "Estimated minutes must be at least 1")
    @Max(value = 600, message = "Estimated minutes must be at most 600")
    private Integer estimatedMinutes = 30;

    private Integer displayOrder = 0;

    private Boolean isPublished = true;

    // Optional inline content creation/update
    private TopicContentRequest content;
}
