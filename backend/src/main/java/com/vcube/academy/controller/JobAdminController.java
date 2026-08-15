package com.vcube.academy.controller;

import com.vcube.academy.dto.job.PlacementDriveAdminRequest;
import com.vcube.academy.dto.job.PlacementDriveDto;
import com.vcube.academy.service.JobAdminTrainerService;
import com.vcube.academy.service.PlacementDriveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class JobAdminController {

    private final JobAdminTrainerService jobAdminTrainerService;
    private final PlacementDriveService placementDriveService;

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Map<String, String>> deleteJob(@PathVariable Long id) {
        jobAdminTrainerService.deleteJob(id);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
    }

    @PostMapping("/placements")
    public ResponseEntity<PlacementDriveDto> createPlacementDrive(@Valid @RequestBody PlacementDriveAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(placementDriveService.createPlacementDrive(request));
    }

    @PutMapping("/placements/{id}")
    public ResponseEntity<PlacementDriveDto> updatePlacementDrive(
            @PathVariable Long id,
            @RequestBody PlacementDriveAdminRequest request
    ) {
        return ResponseEntity.ok(placementDriveService.updatePlacementDrive(id, request));
    }

    @DeleteMapping("/placements/{id}")
    public ResponseEntity<Map<String, String>> deletePlacementDrive(@PathVariable Long id) {
        placementDriveService.deletePlacementDrive(id);
        return ResponseEntity.ok(Map.of("message", "Placement drive deleted successfully"));
    }

    @GetMapping("/jobs/analytics")
    public ResponseEntity<Map<String, Object>> getJobAnalytics() {
        return ResponseEntity.ok(jobAdminTrainerService.getJobAnalytics());
    }
}
