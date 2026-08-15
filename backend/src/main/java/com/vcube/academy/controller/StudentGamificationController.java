package com.vcube.academy.controller;

import com.vcube.academy.dto.gamification.GamificationSummaryDto;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student/gamification")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentGamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/summary")
    public ResponseEntity<GamificationSummaryDto> getGamificationSummary(
            @AuthenticationPrincipal UserPrincipal principal) {
        GamificationSummaryDto summary = gamificationService.getStudentGamificationSummary(principal.getId());
        return ResponseEntity.ok(summary);
    }
}
