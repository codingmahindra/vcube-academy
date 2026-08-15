package com.vcube.academy.controller;

import com.vcube.academy.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRAINER')")
@Tag(name = "Trainer Operations", description = "Trainer dashboard and analytics APIs")
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get trainer dashboard data including course and student progress analytics")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(trainerService.getDashboardData());
    }
}
