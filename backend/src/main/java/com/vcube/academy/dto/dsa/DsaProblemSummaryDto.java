package com.vcube.academy.dto.dsa;

import com.vcube.academy.entity.DsaDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsaProblemSummaryDto {
    private Long id;
    private String title;
    private String slug;
    private DsaDifficulty difficulty;
    private Long categoryId;
    private String categoryName;
    private String subtopic;
    private String companyTags;
    private Boolean isSolved;
    private Boolean isAttempted;
}
