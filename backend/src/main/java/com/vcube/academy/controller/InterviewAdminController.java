package com.vcube.academy.controller;

import com.vcube.academy.dto.interview.CompanyAdminRequest;
import com.vcube.academy.entity.Company;
import com.vcube.academy.service.InterviewTrainerAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/interview")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InterviewAdminController {

    private final InterviewTrainerAdminService trainerAdminService;

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody CompanyAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainerAdminService.createCompany(request));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Map<String, String>> deleteCompany(@PathVariable Long id) {
        trainerAdminService.deleteCompany(id);
        return ResponseEntity.ok(Map.of("message", "Company deleted successfully"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(trainerAdminService.getAdminDashboardStats());
    }
}
