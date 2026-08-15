package com.vcube.academy.controller;

import com.vcube.academy.dto.resume.ResumeAdminStatsDto;
import com.vcube.academy.service.ResumeAdminTrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trainer/resume")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
@Tag(name = "Trainer Resume Management", description = "Endpoints for trainer resume guidance and statistics")
public class ResumeTrainerController {

    private final ResumeAdminTrainerService adminTrainerService;

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated resume and ATS performance statistics")
    public ResponseEntity<ResumeAdminStatsDto> getStats() {
        return ResponseEntity.ok(adminTrainerService.getAdminStatistics());
    }

    @GetMapping("/guidance")
    @Operation(summary = "Get resume guidance resources for student mentoring")
    public ResponseEntity<List<Map<String, String>>> getGuidance() {
        return ResponseEntity.ok(adminTrainerService.getTrainerGuidanceResources());
    }
}
