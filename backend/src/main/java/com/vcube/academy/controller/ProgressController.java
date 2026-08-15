package com.vcube.academy.controller;

import com.vcube.academy.dto.response.ProgressDto;
import com.vcube.academy.dto.response.StudentStatsDto;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Progress", description = "Student learning progress and statistics APIs")
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/stats")
    @Operation(summary = "Get overall student statistics")
    public ResponseEntity<StudentStatsDto> getStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(progressService.getStudentStats(principal.getId()));
    }

    @GetMapping("/courses")
    @Operation(summary = "Get per-course learning progress")
    public ResponseEntity<List<ProgressDto>> getCourseProgress(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(progressService.getCourseProgress(principal.getId()));
    }

    @GetMapping("/weak-topics")
    @Operation(summary = "Get topics where accuracy is below 60%")
    public ResponseEntity<List<ProgressDto.WeakTopicDto>> getWeakTopics(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(progressService.getWeakTopics(principal.getId()));
    }
}
