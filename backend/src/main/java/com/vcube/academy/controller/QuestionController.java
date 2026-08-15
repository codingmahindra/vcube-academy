package com.vcube.academy.controller;

import com.vcube.academy.dto.quiz.QuestionAdminDto;
import com.vcube.academy.dto.quiz.QuestionRequest;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
@Tag(name = "Questions Management", description = "Trainer & Admin MCQ management APIs")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "List questions with optional topic or course filter")
    public ResponseEntity<List<QuestionAdminDto>> getQuestions(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long courseId) {
        return ResponseEntity.ok(questionService.getQuestions(topicId, courseId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question details by ID")
    public ResponseEntity<QuestionAdminDto> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new question with options")
    public ResponseEntity<QuestionAdminDto> createQuestion(
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createQuestion(request, principal.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing question and its options")
    public ResponseEntity<QuestionAdminDto> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(questionService.updateQuestion(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a question")
    public ResponseEntity<Map<String, String>> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(Map.of("message", "Question deleted successfully."));
    }
}
