package com.vcube.academy.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchResultDto {
    private String id;
    private String title;
    private String description;
    private String category; // e.g. "COURSE", "TOPIC", "MCQ", "DSA", "INTERVIEW", "JOB", "PLACEMENT_PAPER"
    private String categoryLabel;
    private String route;
    private String badge;
}
