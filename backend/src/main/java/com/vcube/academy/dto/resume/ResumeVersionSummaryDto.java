package com.vcube.academy.dto.resume;

import com.vcube.academy.entity.ResumeTemplate;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeVersionSummaryDto {
    private Long id;
    private Long profileId;
    private Long jobId;
    private String versionTitle;
    private String targetRole;
    private String targetCompany;
    private ResumeTemplate template;
    private Integer latestAtsScore;
    private Boolean isPrimary;
    private Instant createdAt;
    private Instant updatedAt;
}
