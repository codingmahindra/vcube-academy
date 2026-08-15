package com.vcube.academy.controller;

import com.vcube.academy.dto.job.JobAdminRequest;
import com.vcube.academy.dto.job.JobDetailDto;
import com.vcube.academy.service.JobAdminTrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainer/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
public class JobTrainerController {

    private final JobAdminTrainerService jobAdminTrainerService;

    @PostMapping
    public ResponseEntity<JobDetailDto> createJob(@Valid @RequestBody JobAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobAdminTrainerService.createJob(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDetailDto> updateJob(
            @PathVariable Long id,
            @RequestBody JobAdminRequest request
    ) {
        return ResponseEntity.ok(jobAdminTrainerService.updateJob(id, request));
    }
}
