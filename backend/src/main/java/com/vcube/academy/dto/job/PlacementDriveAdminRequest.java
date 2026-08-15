package com.vcube.academy.dto.job;

import com.vcube.academy.entity.PlacementDriveStatus;
import jakarta.validation.constraints.NotBlank;
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
public class PlacementDriveAdminRequest {
    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Drive date is required")
    private Instant driveDate;

    @NotNull(message = "Registration deadline is required")
    private Instant registrationDeadline;

    private String packageDetails;
    private String eligibilityCriteria;
    private String selectionProcess;
    private String applicationLink;
    private PlacementDriveStatus status;
}
