package com.vcube.academy.controller;

import com.vcube.academy.dto.quiz.*;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "MCQ quiz attempt, answer submission, and result APIs")
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/start")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Start a new quiz attempt")
    public ResponseEntity<QuizAttemptDto> startQuiz(
            @Valid @RequestBody StartQuizRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.startQuiz(principal.getId(), request));
    }

    @GetMapping("/{attemptId}/question")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get the current question for an attempt")
    public ResponseEntity<QuestionDto> getCurrentQuestion(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(quizService.getCurrentQuestion(attemptId, principal.getId()));
    }

    @PostMapping("/{attemptId}/answer")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit an answer for the current question")
    public ResponseEntity<AnswerFeedbackDto> submitAnswer(
            @PathVariable Long attemptId,
            @Valid @RequestBody SubmitAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(quizService.submitAnswer(attemptId, principal.getId(), request));
    }

    @PostMapping("/{attemptId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Complete a quiz attempt and get the result")
    public ResponseEntity<QuizResultDto> completeQuiz(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(quizService.completeQuiz(attemptId, principal.getId()));
    }

    @GetMapping("/{attemptId}/result")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get the result of a completed quiz attempt")
    public ResponseEntity<QuizResultDto> getResult(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(quizService.getQuizResult(attemptId, principal.getId()));
    }
}
