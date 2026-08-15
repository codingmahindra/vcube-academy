package com.vcube.academy.dto.job;

import com.vcube.academy.entity.PlacementDriveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementDriveDto {
    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
    private String companyTier;
    private String title;
    private String description;
    private String location;
    private Instant driveDate;
    private Instant registrationDeadline;
    private String packageDetails;
    private String eligibilityCriteria;
    private String selectionProcess;
    private String applicationLink;
    private PlacementDriveStatus status;
}
