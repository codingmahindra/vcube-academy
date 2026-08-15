package com.vcube.academy.dto.topic;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TopicDetailDto {
    private Long id;
    private Long moduleId;
    private String moduleTitle;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String slug;
    private String difficulty;
    private Integer estimatedMinutes;
    private Integer displayOrder;
    private Boolean isPublished;
    private Integer questionCount;
    private TopicContentDto content;
    private Instant createdAt;
    private Instant updatedAt;
}
