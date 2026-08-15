package com.vcube.academy.dto.career;

import com.vcube.academy.entity.CareerRoadmapStage;
import com.vcube.academy.entity.CareerRoadmapStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerRoadmapStageDto {
    private CareerRoadmapStage stage;
    private String title;
    private String description;
    private CareerRoadmapStatus status;
    private int completionPercentage;
    private List<String> focusAreas;
    private String recommendedActionTitle;
    private String recommendedActionLink;
}
