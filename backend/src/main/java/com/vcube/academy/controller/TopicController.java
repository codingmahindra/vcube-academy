package com.vcube.academy.controller;

import com.vcube.academy.dto.topic.*;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.TopicService;
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
@RequestMapping("/topics")
@RequiredArgsConstructor
@Tag(name = "Topics", description = "Topic content and completion APIs")
public class TopicController {

    private final TopicService topicService;

    // ─── Student reading ──────────────────────────────────────────────────────

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "List topics in a module")
    public ResponseEntity<List<TopicDto>> getTopicsByModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(topicService.getTopicsByModule(moduleId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get topic detail including content")
    public ResponseEntity<TopicDetailDto> getTopicDetail(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getTopicDetail(id));
    }

    @GetMapping("/{id}/completion")
    @Operation(summary = "Check if the current student has completed a topic")
    public ResponseEntity<Map<String, Boolean>> checkCompletion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean completed = topicService.isTopicCompleted(principal.getId(), id);
        return ResponseEntity.ok(Map.of("completed", completed));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mark a topic as completed for the authenticated student")
    public ResponseEntity<Map<String, String>> markComplete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        topicService.markTopicComplete(principal.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Topic marked as completed."));
    }

    // ─── Trainer CRUD ─────────────────────────────────────────────────────────

    @PostMapping("/module/{moduleId}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Create a topic in a module")
    public ResponseEntity<TopicDto> createTopic(
            @PathVariable Long moduleId,
            @Valid @RequestBody TopicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.createTopic(moduleId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Update a topic")
    public ResponseEntity<TopicDto> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest request) {
        return ResponseEntity.ok(topicService.updateTopic(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Delete a topic")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}
