package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPlanDto {
    private Long id;
    private LocalDate planDate;
    private int totalTasks;
    private int completedTasks;
    private int completionPercentage;
    private String status;
    private List<DailyPlanItemDto> items;
}
