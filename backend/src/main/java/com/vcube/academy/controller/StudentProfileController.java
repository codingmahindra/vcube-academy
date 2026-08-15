package com.vcube.academy.controller;

import com.vcube.academy.dto.user.StudentProfileDto;
import com.vcube.academy.dto.user.StudentProfileUpdateRequest;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.EnhancedProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentProfileController {

    private final EnhancedProfileService profileService;

    @GetMapping
    public ResponseEntity<StudentProfileDto> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        StudentProfileDto profile = profileService.getStudentProfile(principal.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<StudentProfileDto> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentProfileUpdateRequest req) {
        StudentProfileDto updated = profileService.updateStudentProfile(principal.getId(), req);
        return ResponseEntity.ok(updated);
    }
}
