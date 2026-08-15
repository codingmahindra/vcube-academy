package com.vcube.academy.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDashboardDto {
    private long totalApplications;
    private long appliedCount;
    private long assessmentCount;
    private long interviewCount;
    private long offerCount;
    private long rejectedCount;
    private List<JobApplicationDto> upcomingInterviews;
    private List<JobApplicationDto> recentApplications;
}
