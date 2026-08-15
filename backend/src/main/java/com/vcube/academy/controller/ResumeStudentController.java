package com.vcube.academy.controller;

import com.vcube.academy.dto.resume.*;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.ResumeService;
import com.vcube.academy.service.ResumeTextExtractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student/resume")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT') or hasRole('TRAINER') or hasRole('ADMIN')")
@Tag(name = "Student Resume Intelligence", description = "Endpoints for ATS resume creation, parsing, analysis, and PDF download")
public class ResumeStudentController {

    private final ResumeService resumeService;
    private final ResumeTextExtractorService textExtractorService;

    @GetMapping("/profile")
    @Operation(summary = "Get or create student resume profile")
    public ResponseEntity<ResumeProfileDto> getProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(resumeService.getProfile(currentUser.getId()));
    }

    @GetMapping("/versions")
    @Operation(summary = "List all resume versions belonging to current student")
    public ResponseEntity<List<ResumeVersionSummaryDto>> listVersions(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(resumeService.listVersions(currentUser.getId()));
    }

    @GetMapping("/versions/{id}")
    @Operation(summary = "Get resume version detail by ID")
    public ResponseEntity<ResumeVersionDetailDto> getVersionDetail(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(resumeService.getVersionDetail(id, currentUser.getId()));
    }

    @PostMapping("/versions")
    @Operation(summary = "Create a new resume version")
    public ResponseEntity<ResumeVersionDetailDto> createVersion(@Valid @RequestBody ResumeDataRequest request, @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(resumeService.saveResume(request, currentUser.getId()));
    }

    @PutMapping("/versions/{id}")
    @Operation(summary = "Update an existing resume version")
    public ResponseEntity<ResumeVersionDetailDto> updateVersion(@PathVariable Long id, @Valid @RequestBody ResumeDataRequest request, @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(resumeService.updateResume(id, request, currentUser.getId()));
    }

    @DeleteMapping("/versions/{id}")
    @Operation(summary = "Delete resume version")
    public ResponseEntity<Void> deleteVersion(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        resumeService.deleteVersion(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Extract text from uploaded PDF/DOCX/TXT resume file")
    public ResponseEntity<Map<String, String>> uploadAndExtract(@RequestParam("file") MultipartFile file) {
        String extractedText = textExtractorService.extractText(file);
        return ResponseEntity.ok(Map.of("extractedText", extractedText));
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analyze resume against JD and calculate ATS score")
    public ResponseEntity<ResumeAnalysisDto> analyzeResume(@RequestBody ResumeAnalyzeRequest request, @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(resumeService.analyzeResume(request, currentUser.getId()));
    }

    @GetMapping("/versions/{id}/optimize")
    @Operation(summary = "Get ATS optimization recommendations")
    public ResponseEntity<ResumeOptimizationDto> optimizeResume(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(resumeService.optimizeResume(id, currentUser.getId()));
    }

    @GetMapping(value = "/versions/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Generate and download professional ATS-compliant PDF resume")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        byte[] pdfBytes = resumeService.generatePdf(id, currentUser.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"VCUBE_ATS_Resume_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}

