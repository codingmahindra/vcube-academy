package com.vcube.academy.dto.resume;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeOptimizationDto {
    private String optimizedSummary;
    private List<Map<String, String>> optimizedBulletPoints; // original vs improvedActionOriented
    private List<String> recommendedActionVerbs;
    private List<String> detectedWeaknesses;
    private List<String> suggestedCertifications;
    private String rationale;
}
