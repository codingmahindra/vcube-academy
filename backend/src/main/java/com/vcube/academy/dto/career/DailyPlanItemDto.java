package com.vcube.academy.dto.career;

import com.vcube.academy.entity.DailyPlanCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPlanItemDto {

    private Long id;

    private DailyPlanCategory category;

    private String title;

    private String description;

    private int targetCount;

    private int completedCount;

    private boolean isCompleted;

    private String actionRoute;

    private String actionLabel;

    private int displayOrder;
}