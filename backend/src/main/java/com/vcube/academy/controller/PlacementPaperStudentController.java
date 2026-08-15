package com.vcube.academy.controller;

import com.vcube.academy.dto.career.*;
import com.vcube.academy.entity.User;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.PlacementPaperService;
import com.vcube.academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/placement-papers")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class PlacementPaperStudentController {

    private final PlacementPaperService paperService;
    private final UserRepository userRepository;

    private User getStudentUser(UserPrincipal principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated student not found: " + principal.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<PlacementPaperSummaryDto>> listPapers(@AuthenticationPrincipal UserPrincipal principal) {
        User student = getStudentUser(principal);
        return ResponseEntity.ok(paperService.listPapers(student));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlacementPaperDetailDto> getPaperDetail(@PathVariable Long id) {
        return ResponseEntity.ok(paperService.getPaperDetail(id));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<PlacementPaperQuestionDto>> getQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(paperService.getPaperDetail(id).getQuestions());
    }

    @PostMapping("/{id}/attempt")
    public ResponseEntity<PlacementPaperAttemptDto> startAttempt(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User student = getStudentUser(principal);
        return ResponseEntity.ok(paperService.startAttempt(id, student));
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<Void> submitAnswer(
            @PathVariable Long id,
            @RequestBody PlacementPaperAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User student = getStudentUser(principal);
        paperService.submitAnswer(request, student);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<PlacementPaperResultDto> completeAttempt(
            @PathVariable Long id,
            @RequestParam(required = false) Long attemptId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User student = getStudentUser(principal);
        Long targetAttemptId = attemptId != null ? attemptId : id;
        return ResponseEntity.ok(paperService.completeAttempt(targetAttemptId, student));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<PlacementPaperResultDto> getResult(
            @PathVariable Long id,
            @RequestParam(required = false) Long attemptId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User student = getStudentUser(principal);
        Long targetAttemptId = attemptId != null ? attemptId : id;
        return ResponseEntity.ok(paperService.getAttemptResult(targetAttemptId, student));
    }
}
