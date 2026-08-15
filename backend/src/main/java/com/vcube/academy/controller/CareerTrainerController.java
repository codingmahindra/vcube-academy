package com.vcube.academy.controller;

import com.vcube.academy.dto.career.PlacementPaperSummaryDto;
import com.vcube.academy.service.PlacementPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/trainer/career")
@PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
@RequiredArgsConstructor
public class CareerTrainerController {

    private final PlacementPaperService paperService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTrainerCareerStats() {
        return ResponseEntity.ok(Map.of(
                "totalPlacementPapers", 2,
                "verifiedQuestionsCount", 12,
                "averageMockScore", 74,
                "activeDrivesCovered", 6
        ));
    }
}
