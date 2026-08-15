package com.vcube.academy.dto.resume;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAdminStatsDto {
    private Long totalResumesCreated;
    private Long totalAnalysesPerformed;
    private Double averageAtsScore;
    private List<Map<String, Object>> topMissingSkills;
    private List<Map<String, Object>> topTargetRoles;
}
