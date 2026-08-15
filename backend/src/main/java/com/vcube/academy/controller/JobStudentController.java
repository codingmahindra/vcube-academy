package com.vcube.academy.controller;

import com.vcube.academy.dto.interview.CompanySummaryDto;
import com.vcube.academy.dto.job.*;
import com.vcube.academy.entity.EmploymentType;
import com.vcube.academy.entity.ExperienceLevel;
import com.vcube.academy.entity.WorkMode;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.JobApplicationService;
import com.vcube.academy.service.JobPortalService;
import com.vcube.academy.service.SavedJobService;
import com.vcube.academy.service.StudentPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JobStudentController {

    private final JobPortalService jobPortalService;
    private final SavedJobService savedJobService;
    private final JobApplicationService applicationService;
    private final StudentPreferenceService preferenceService;

    // --- Job Browsing & Search ---

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobSummaryDto>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) WorkMode workMode,
            @RequestParam(required = false) Long skillId,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        JobFilterRequest filter = JobFilterRequest.builder()
                .keyword(keyword)
                .companyId(companyId)
                .categoryId(categoryId)
                .location(location)
                .employmentType(employmentType)
                .experienceLevel(experienceLevel)
                .workMode(workMode)
                .skillId(skillId)
                .minSalary(minSalary)
                .sortBy(sortBy)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(jobPortalService.searchJobs(filter, userId));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobDetailDto> getJobDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        return ResponseEntity.ok(jobPortalService.getJobDetail(id, userId));
    }

    @GetMapping("/jobs/categories")
    public ResponseEntity<List<JobCategoryDto>> getCategories() {
        return ResponseEntity.ok(jobPortalService.getCategories());
    }

    @GetMapping("/jobs/locations")
    public ResponseEntity<List<String>> getLocations() {
        return ResponseEntity.ok(jobPortalService.getLocations());
    }

    @GetMapping("/jobs/skills")
    public ResponseEntity<List<JobSkillDto>> getSkills() {
        return ResponseEntity.ok(jobPortalService.getSkills());
    }

    // --- Saved Jobs ---

    @PostMapping("/student/jobs/{jobId}/save")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SavedJobDto> saveJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedJobService.saveJob(jobId, currentUser.getId()));
    }

    @DeleteMapping("/student/jobs/{jobId}/save")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> unsaveJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        savedJobService.unsaveJob(jobId, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Job removed from saved list"));
    }

    @GetMapping("/student/saved-jobs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SavedJobDto>> getSavedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(savedJobService.getSavedJobs(currentUser.getId(), page, size));
    }

    // --- Applications Tracker ---

    @PostMapping("/student/applications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JobApplicationDto> createApplication(
            @Valid @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.createApplication(request, currentUser.getId()));
    }

    @GetMapping("/student/applications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<JobApplicationDto>> getApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(applicationService.getApplications(currentUser.getId(), page, size));
    }

    @GetMapping("/student/applications/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JobApplicationDto> getApplicationDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(applicationService.getApplicationDetail(id, currentUser.getId()));
    }

    @PutMapping("/student/applications/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JobApplicationDto> updateApplication(
            @PathVariable Long id,
            @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(applicationService.updateApplication(id, request, currentUser.getId()));
    }

    @DeleteMapping("/student/applications/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> deleteApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        applicationService.deleteApplication(id, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Application removed"));
    }

    @GetMapping("/student/applications/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplicationDashboardDto> getApplicationDashboard(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(applicationService.getApplicationDashboard(currentUser.getId()));
    }

    // --- Student Career Preferences ---

    @GetMapping("/student/job-preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudentJobPreferenceDto> getPreferences(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(preferenceService.getPreferences(currentUser.getId()));
    }

    @PutMapping("/student/job-preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudentJobPreferenceDto> savePreferences(
            @RequestBody StudentJobPreferenceDto dto,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(preferenceService.savePreferences(dto, currentUser.getId()));
    }

    @GetMapping("/jobs/companies")
    public ResponseEntity<List<CompanySummaryDto>> getCompanies() {
        return ResponseEntity.ok(jobPortalService.getCompanies());
    }

    // --- Student Job Recommendations ---

    @GetMapping("/student/job-recommendations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getJobRecommendations(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(jobPortalService.getStudentJobRecommendations(currentUser.getId()));
    }
}
