package com.vcube.academy.dto.dsa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsaCategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private Integer displayOrder;
    private long totalProblems;
    private long solvedProblems;
}
