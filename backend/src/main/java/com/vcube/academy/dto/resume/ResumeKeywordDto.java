package com.vcube.academy.dto.resume;

import com.vcube.academy.entity.KeywordCategory;
import com.vcube.academy.entity.SkillMatchStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeKeywordDto {
    private Long id;
    private String keywordName;
    private KeywordCategory category;
    private SkillMatchStatus matchStatus;
    private String importance;
    private Integer occurrenceCount;
}
