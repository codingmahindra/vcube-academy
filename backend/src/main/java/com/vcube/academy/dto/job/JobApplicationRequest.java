package com.vcube.academy.dto.job;

import com.vcube.academy.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {
    @NotNull(message = "Job ID is required")
    private Long jobId;

    private ApplicationStatus status;
    private String notes;
    private String nextAction;
    private Instant interviewDate;
}
