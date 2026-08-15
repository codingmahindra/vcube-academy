package com.vcube.academy.controller;

import com.vcube.academy.service.DsaTrainerAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/dsa")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin DSA Management", description = "Platform-wide DSA statistics")
public class DsaAdminController {

    private final DsaTrainerAdminService trainerAdminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get platform-wide DSA statistics")
    public ResponseEntity<Map<String, Object>> getAdminDsaStats() {
        return ResponseEntity.ok(trainerAdminService.getAdminDsaStats());
    }
}
