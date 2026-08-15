package com.vcube.academy.dto.topic;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TopicDto {
    private Long id;
    private Long moduleId;
    private String moduleTitle;
    private String courseTitle;
    private Long courseId;
    private String title;
    private String slug;
    private String difficulty;
    private Integer estimatedMinutes;
    private Integer displayOrder;
    private Boolean isPublished;
    private Boolean hasContent;
    private Integer questionCount;
    private Instant createdAt;
    private Instant updatedAt;
}
