package com.vcube.academy.controller;

import com.vcube.academy.dto.dsa.DsaProblemDetailDto;
import com.vcube.academy.dto.dsa.DsaProblemRequest;
import com.vcube.academy.service.DsaTrainerAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trainer/dsa")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
@Tag(name = "Trainer DSA Management", description = "Trainer DSA problem and test case CRUD operations")
public class DsaTrainerController {

    private final DsaTrainerAdminService trainerAdminService;

    @PostMapping("/problems")
    @Operation(summary = "Create a new DSA problem with test cases")
    public ResponseEntity<DsaProblemDetailDto> createProblem(@Valid @RequestBody DsaProblemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerAdminService.createProblem(request));
    }

    @PutMapping("/problems/{id}")
    @Operation(summary = "Update an existing DSA problem")
    public ResponseEntity<DsaProblemDetailDto> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody DsaProblemRequest request) {
        return ResponseEntity.ok(trainerAdminService.updateProblem(id, request));
    }

    @DeleteMapping("/problems/{id}")
    @Operation(summary = "Delete a DSA problem")
    public ResponseEntity<Map<String, String>> deleteProblem(@PathVariable Long id) {
        trainerAdminService.deleteProblem(id);
        return ResponseEntity.ok(Map.of("message", "DSA Problem deleted successfully."));
    }
}
