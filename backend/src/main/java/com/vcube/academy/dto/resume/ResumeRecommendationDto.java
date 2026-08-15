package com.vcube.academy.dto.resume;

import com.vcube.academy.entity.ResumeSectionType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeRecommendationDto {
    private Long id;
    private ResumeSectionType sectionType;
    private String severity; // CRITICAL, WARNING, SUGGESTION
    private String title;
    private String message;
    private String actionableFix;
}
