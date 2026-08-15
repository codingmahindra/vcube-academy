package com.vcube.academy.dto.resume;

import com.vcube.academy.entity.KeywordCategory;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeMissingSkillDto {
    private Long id;
    private String skillName;
    private KeywordCategory category;
    private String importance;
    private String whyItMatters;
    private Map<String, Object> recommendedCourse;
    private Map<String, Object> recommendedDsaProblem;
    private Map<String, Object> recommendedInterviewQuestion;
    private String recommendedMockRole;
}
