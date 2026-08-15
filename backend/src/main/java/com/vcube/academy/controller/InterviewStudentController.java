package com.vcube.academy.controller;

import com.vcube.academy.dto.interview.*;
import com.vcube.academy.entity.InterviewDifficulty;
import com.vcube.academy.entity.InterviewQuestionType;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.InterviewContentService;
import com.vcube.academy.service.InterviewPracticeService;
import com.vcube.academy.service.InterviewProgressService;
import com.vcube.academy.service.MockInterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewStudentController {

    private final InterviewContentService contentService;
    private final InterviewPracticeService practiceService;
    private final MockInterviewService mockInterviewService;
    private final InterviewProgressService progressService;

    // --- Content Browsing ---

    @GetMapping("/categories")
    public ResponseEntity<List<InterviewCategoryDto>> getCategories(@AuthenticationPrincipal UserPrincipal currentUser) {
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        return ResponseEntity.ok(contentService.getCategories(userId));
    }

    @GetMapping("/topics")
    public ResponseEntity<List<InterviewTopicDto>> getTopics(
            @RequestParam(required = false) Long categoryId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        if (categoryId != null) {
            return ResponseEntity.ok(contentService.getTopicsByCategory(categoryId, userId));
        }
        return ResponseEntity.ok(contentService.getTopicsByCategory(1L, userId));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanySummaryDto>> getCompanies() {
        return ResponseEntity.ok(contentService.getCompanies());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyDetailDto> getCompanyDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        return ResponseEntity.ok(contentService.getCompanyDetail(id, userId));
    }

    @GetMapping("/questions")
    public ResponseEntity<Page<InterviewQuestionSummaryDto>> searchQuestions(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) InterviewDifficulty difficulty,
            @RequestParam(required = false) InterviewQuestionType type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        return ResponseEntity.ok(contentService.searchQuestions(topicId, categoryId, difficulty, type, search, page, size, userId));
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<InterviewQuestionDetailDto> getQuestionDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        return ResponseEntity.ok(contentService.getQuestionDetail(id, userId));
    }

    // --- Question Practice & Evaluation ---

    @PostMapping("/questions/{id}/evaluate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewEvaluationResponse> evaluatePracticeAnswer(
            @PathVariable Long id,
            @Valid @RequestBody InterviewEvaluationRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(practiceService.evaluateAndSavePracticeAnswer(id, request, currentUser.getId()));
    }

    // --- Mock Interview Endpoints ---

    @PostMapping("/mock/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MockInterviewResponse> startMockInterview(
            @RequestBody MockInterviewStartRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mockInterviewService.startMockInterview(request, currentUser.getId()));
    }

    @PostMapping("/mock/{id}/answer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MockInterviewQuestionDto> answerMockQuestion(
            @PathVariable Long id,
            @Valid @RequestBody MockInterviewAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(mockInterviewService.answerQuestion(id, request, currentUser.getId()));
    }

    @PostMapping("/mock/{id}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MockInterviewResultDto> completeMockInterview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(mockInterviewService.completeMockInterview(id, currentUser.getId()));
    }

    @GetMapping("/mock/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MockInterviewResponse> getMockInterview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(mockInterviewService.getMockInterview(id, currentUser.getId()));
    }

    @GetMapping("/mock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<MockInterviewResponse>> getUserMockInterviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(mockInterviewService.getUserMockInterviews(currentUser.getId(), page, size));
    }

    // --- Student Progress & Recommendations ---

    @GetMapping("/progress")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewProgressSummaryDto> getProgress(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(progressService.getProgress(currentUser.getId()));
    }

    @GetMapping("/recommendations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getRecommendations(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(progressService.getRecommendations(currentUser.getId()));
    }
}
