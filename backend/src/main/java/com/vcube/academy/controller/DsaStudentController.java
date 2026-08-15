package com.vcube.academy.controller;

import com.vcube.academy.dto.dsa.*;
import com.vcube.academy.entity.DsaDifficulty;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.DsaProblemService;
import com.vcube.academy.service.DsaProgressService;
import com.vcube.academy.service.DsaSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dsa")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "DSA Practice Engine", description = "Student DSA problem, execution, submission, and progress APIs")
public class DsaStudentController {

    private final DsaProblemService problemService;
    private final DsaSubmissionService submissionService;
    private final DsaProgressService progressService;

    @GetMapping("/categories")
    @Operation(summary = "Get all active DSA categories with solved counts")
    public ResponseEntity<List<DsaCategoryDto>> getCategories(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(problemService.getCategories(principal.getId()));
    }

    @GetMapping("/problems")
    @Operation(summary = "Get paginated list of DSA problems with filters")
    public ResponseEntity<Page<DsaProblemSummaryDto>> getProblems(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) DsaDifficulty difficulty,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return ResponseEntity.ok(problemService.getProblems(categoryId, difficulty, search, statusFilter, pageable, principal.getId()));
    }

    @GetMapping("/problems/{id}")
    @Operation(summary = "Get detailed DSA problem view")
    public ResponseEntity<DsaProblemDetailDto> getProblemDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(problemService.getProblemDetail(id, principal.getId()));
    }

    @PostMapping("/problems/{id}/run")
    @Operation(summary = "Run code against sample test cases")
    public ResponseEntity<CodeExecutionResult> runCode(
            @PathVariable Long id,
            @Valid @RequestBody DsaSubmissionRequest request) {
        return ResponseEntity.ok(submissionService.runCode(id, request));
    }

    @PostMapping("/problems/{id}/submit")
    @Operation(summary = "Submit code against all test cases and record attempt")
    public ResponseEntity<DsaSubmissionResponse> submitCode(
            @PathVariable Long id,
            @Valid @RequestBody DsaSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(submissionService.submitCode(principal.getId(), id, request));
    }

    @GetMapping("/submissions")
    @Operation(summary = "Get current student submission history")
    public ResponseEntity<Page<DsaSubmissionResponse>> getUserSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(submissionService.getUserSubmissions(principal.getId(), PageRequest.of(page, size)));
    }

    @GetMapping("/problems/{id}/submissions")
    @Operation(summary = "Get student submissions for a specific DSA problem")
    public ResponseEntity<Page<DsaSubmissionResponse>> getProblemSubmissions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(submissionService.getProblemSubmissions(principal.getId(), id, PageRequest.of(page, size)));
    }

    @GetMapping("/progress")
    @Operation(summary = "Get student DSA progress analytics")
    public ResponseEntity<DsaProgressSummaryDto> getProgress(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(progressService.getStudentDsaProgress(principal.getId()));
    }

    @GetMapping("/problems/{id}/hints")
    @Operation(summary = "Get hints for a DSA problem")
    public ResponseEntity<Map<String, Object>> getHints(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getHints(id));
    }

    @GetMapping("/problems/{id}/solution")
    @Operation(summary = "Get solution explanation and Java code for a DSA problem")
    public ResponseEntity<Map<String, Object>> getSolution(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(problemService.getSolution(id, principal.getId()));
    }
}
