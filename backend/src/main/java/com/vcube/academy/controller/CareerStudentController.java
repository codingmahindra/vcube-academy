package com.vcube.academy.controller;

import com.vcube.academy.dto.career.*;
import com.vcube.academy.entity.User;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.*;
import com.vcube.academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/career")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class CareerStudentController {

    private final CareerService careerService;
    private final CareerRoadmapService roadmapService;
    private final CareerAIService careerAIService;
    private final DailyPreparationService dailyPlanService;
    private final WeakAreaEngineService weakAreaService;
    private final CompanyPrepService companyPrepService;
    private final UserRepository userRepository;

    private User getStudentUser(UserPrincipal principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated student not found: " + principal.getUsername()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<CareerDashboardDto> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        User student = getStudentUser(principal);
        return ResponseEntity.ok(careerService.getStudentCareerDashboard(student));
    }

    @GetMapping("/roadmap")
    public ResponseEntity<CareerRoadmapDto> getRoadmap(@AuthenticationPrincipal UserPrincipal principal) {
        User student = getStudentUser(principal);
        return ResponseEntity.ok(roadmapService.generateRoadmap(student));
    }

    @PostMapping("/copilot/chat")
    public ResponseEntity<CopilotChatResponse> chatWithCopilot(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CopilotChatRequest request
    ) {
        User student = getStudentUser(principal);
        return ResponseEntity.ok(careerAIService.generateCopilotResponse(student, request.getMessage(), request.getConversationId()));
    }

    @GetMapping("/daily-plan")
    public ResponseEntity<DailyPlanDto> getDailyPlan(@AuthenticationPrincipal UserPrincipal principal) {
        User student = getStudentUser(principal);
        return ResponseEntity.ok(dailyPlanService.getOrCreateTodayPlan(student));
    }

    @PostMapping("/daily-plan/toggle/{itemId}")
    public ResponseEntity<DailyPlanDto> toggleTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long itemId
    ) {
        User student = getStudentUser(principal);
        return ResponseEntity.ok(dailyPlanService.toggleTaskCompletion(itemId, student));
    }

    @GetMapping("/weak-areas")
    public ResponseEntity<List<WeakAreaDto>> getWeakAreas(@AuthenticationPrincipal UserPrincipal principal) {
        User student = getStudentUser(principal);
        return ResponseEntity.ok(weakAreaService.getWeakAreasForStudent(student));
    }

    @GetMapping("/company-prep/{companyId}")
    public ResponseEntity<CompanyPrepHubDto> getCompanyPrep(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyPrepService.getCompanyPrep(companyId));
    }
}
