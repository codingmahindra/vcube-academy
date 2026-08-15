package com.vcube.academy.dto.job;

import com.vcube.academy.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDto {
    private Long id;
    private Long jobId;
    private JobSummaryDto job;
    private ApplicationStatus status;
    private Instant appliedDate;
    private String notes;
    private String nextAction;
    private Instant interviewDate;
    private List<ApplicationStatusHistoryDto> statusHistories;
    private Instant createdAt;
    private Instant updatedAt;
}
