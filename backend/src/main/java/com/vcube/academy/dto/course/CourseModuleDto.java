package com.vcube.academy.dto.course;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CourseModuleDto {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer displayOrder;
    private List<TopicSummaryDto> topics;
    private Instant createdAt;
    private Instant updatedAt;
}
