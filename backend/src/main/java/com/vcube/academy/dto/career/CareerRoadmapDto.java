package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerRoadmapDto {
    private String targetRole;
    private int overallProgressPercentage;
    private String currentStageName;
    private List<CareerRoadmapStageDto> stages;
    private List<String> currentSkillGaps;
    private String primaryNextAction;
    private String primaryNextActionLink;
}
