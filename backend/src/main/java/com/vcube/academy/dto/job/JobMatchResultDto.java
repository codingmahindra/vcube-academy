package com.vcube.academy.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchResultDto {
    private int matchPercentage;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private boolean rolePreferenceMatched;
    private boolean locationPreferenceMatched;
    private boolean workModeMatched;
    private boolean experienceMatched;
    private String summary;
}
