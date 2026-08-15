package com.vcube.academy.controller;

import com.vcube.academy.dto.user.UserDto;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Tag(name = "Student Dashboard", description = "Student-facing APIs")
public class StudentController {

    private final AuthService authService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Student dashboard data")
    public ResponseEntity<Map<String, Object>> dashboard(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserDto user = authService.getCurrentUser(principal);
        return ResponseEntity.ok(Map.of(
            "message",  "Welcome to VCUBE Academy, " + user.getFullName() + "!",
            "user",     user,
            "trainers", "SriKanth & Viswanath"
        ));
    }
}
