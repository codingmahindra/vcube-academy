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

@RestController
@RequestMapping("/admin/resume")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Resume Oversight", description = "Endpoints for platform-wide resume and ATS performance oversight")
public class ResumeAdminController {

    private final ResumeAdminTrainerService adminTrainerService;

    @GetMapping("/analytics")
    @Operation(summary = "Get platform-wide aggregated resume metrics and skill gap trends")
    public ResponseEntity<ResumeAdminStatsDto> getAnalytics() {
        return ResponseEntity.ok(adminTrainerService.getAdminStatistics());
    }
}
