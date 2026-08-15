package com.vcube.academy.dto.dsa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsaProgressSummaryDto {
    private long totalProblems;
    private long solvedProblems;
    private long attemptedProblems;
    private long easySolved;
    private long easyTotal;
    private long mediumSolved;
    private long mediumTotal;
    private long hardSolved;
    private long hardTotal;
    private BigDecimal successRate;
    private long totalSubmissions;
    private List<DsaCategoryDto> categoryProgress;
}
