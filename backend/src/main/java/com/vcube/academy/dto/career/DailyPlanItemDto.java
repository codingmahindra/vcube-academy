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
    private int targetCount;
    private int completedCount;
    private String actionLink;
    private boolean isCompleted;
    private int displayOrder;
}
