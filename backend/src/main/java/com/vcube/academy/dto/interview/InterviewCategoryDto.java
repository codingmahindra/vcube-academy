package com.vcube.academy.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewCategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private Integer displayOrder;
    private long totalTopics;
    private long totalQuestions;
    private long completedQuestions;
    private List<InterviewTopicDto> topics;
}
