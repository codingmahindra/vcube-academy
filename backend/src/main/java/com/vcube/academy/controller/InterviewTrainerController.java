package com.vcube.academy.controller;

import com.vcube.academy.dto.interview.InterviewQuestionAdminRequest;
import com.vcube.academy.dto.interview.InterviewQuestionDetailDto;
import com.vcube.academy.service.InterviewTrainerAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trainer/interview")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
public class InterviewTrainerController {

    private final InterviewTrainerAdminService trainerAdminService;

    @PostMapping("/questions")
    public ResponseEntity<InterviewQuestionDetailDto> createQuestion(@Valid @RequestBody InterviewQuestionAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainerAdminService.createQuestion(request));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<InterviewQuestionDetailDto> updateQuestion(
            @PathVariable Long id,
            @RequestBody InterviewQuestionAdminRequest request
    ) {
        return ResponseEntity.ok(trainerAdminService.updateQuestion(id, request));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Map<String, String>> deleteQuestion(@PathVariable Long id) {
        trainerAdminService.deleteQuestion(id);
        return ResponseEntity.ok(Map.of("message", "Question deleted successfully"));
    }
}
