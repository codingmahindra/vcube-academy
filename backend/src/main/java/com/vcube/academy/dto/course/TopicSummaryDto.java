package com.vcube.academy.dto.course;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicSummaryDto {
    private Long id;
    private Long moduleId;
    private String title;
    private String slug;
    private String difficulty;
    private Integer estimatedMinutes;
    private Integer displayOrder;
    private Boolean isPublished;
    private Integer questionCount;
}
