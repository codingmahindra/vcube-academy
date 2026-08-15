package com.vcube.academy.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobDto {
    private Long id;
    private Long jobId;
    private JobSummaryDto job;
    private Instant savedAt;
}
