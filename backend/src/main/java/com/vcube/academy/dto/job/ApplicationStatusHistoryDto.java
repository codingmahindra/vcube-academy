package com.vcube.academy.dto.job;

import com.vcube.academy.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusHistoryDto {
    private Long id;
    private ApplicationStatus previousStatus;
    private ApplicationStatus newStatus;
    private Instant changedAt;
    private String notes;
}
