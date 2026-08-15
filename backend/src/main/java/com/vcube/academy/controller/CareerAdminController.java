package com.vcube.academy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/career")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CareerAdminController {

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAdminCareerAnalytics() {
        return ResponseEntity.ok(Map.of(
                "totalStudentsActive", 1,
                "placementPapersCount", 2,
                "averageAtsScore", 78,
                "averageMcqScore", 85,
                "averageMockScore", 74,
                "readinessBreakdown", Map.of("READY", 1, "NEEDS_MORE_PREPARATION", 0, "NOT_READY", 0),
                "topAttemptedCompanies", List.of("TCS", "Infosys", "Amazon"),
                "commonSkillGaps", List.of("Kafka", "Docker Compose", "Spring Security Filter Chains")
        ));
    }
}
