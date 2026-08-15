package com.vcube.academy.controller;

import com.vcube.academy.dto.job.PlacementDriveDto;
import com.vcube.academy.service.PlacementDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/placements")
@RequiredArgsConstructor
public class PlacementStudentController {

    private final PlacementDriveService driveService;

    @GetMapping
    public ResponseEntity<List<PlacementDriveDto>> getActivePlacementDrives() {
        return ResponseEntity.ok(driveService.getActivePlacementDrives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlacementDriveDto> getPlacementDriveDetail(@PathVariable Long id) {
        return ResponseEntity.ok(driveService.getPlacementDriveDetail(id));
    }
}
