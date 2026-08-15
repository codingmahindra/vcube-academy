package com.vcube.academy.service;

import com.vcube.academy.dto.dsa.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import com.vcube.academy.service.execution.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DsaSubmissionService {

    private final DsaProblemRepository problemRepository;
    private final DsaTestCaseRepository testCaseRepository;
    private final DsaSubmissionRepository submissionRepository;
    private final DsaStudentProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final CodeExecutionService codeExecutionService;

    @Transactional
    public CodeExecutionResult runCode(Long problemId, DsaSubmissionRequest request) {
        DsaProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("DSA Problem", "id", problemId));

        List<DsaTestCase> sampleTestCases = testCaseRepository.findByProblemIdAndIsSampleTrueOrderByDisplayOrderAsc(problem.getId());
        if (sampleTestCases.isEmpty()) {
            sampleTestCases = testCaseRepository.findByProblemIdOrderByDisplayOrderAsc(problem.getId());
        }

        return codeExecutionService.execute(request.getSourceCode(), sampleTestCases);
    }

    @Transactional
    public DsaSubmissionResponse submitCode(Long userId, Long problemId, DsaSubmissionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        DsaProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("DSA Problem", "id", problemId));

        List<DsaTestCase> allTestCases = testCaseRepository.findByProblemIdOrderByDisplayOrderAsc(problem.getId());

        CodeExecutionResult execResult = codeExecutionService.execute(request.getSourceCode(), allTestCases);

        DsaSubmission submission = DsaSubmission.builder()
                .user(user)
                .problem(problem)
                .language(request.getLanguage() != null ? request.getLanguage() : "JAVA")
                .sourceCode(request.getSourceCode())
                .status(execResult.getStatus())
                .executionTimeMs(execResult.getExecutionTimeMs())
                .memoryUsedKb(execResult.getMemoryUsedKb())
                .passedTestCases(execResult.getPassedTestCases())
                .totalTestCases(execResult.getTotalTestCases())
                .errorOutput(execResult.getErrorOutput())
                .build();

        submission = submissionRepository.save(submission);

        // Update student DSA progress
        DsaStudentProgress progress = progressRepository.findByUserIdAndProblemId(user.getId(), problem.getId())
                .orElseGet(() -> DsaStudentProgress.builder()
                        .user(user)
                        .problem(problem)
                        .attemptCount(0)
                        .isSolved(false)
                        .build());

        progress.setAttemptCount(progress.getAttemptCount() + 1);
        progress.setLastAttemptAt(Instant.now());

        if (execResult.getStatus() == SubmissionStatus.ACCEPTED) {
            if (!Boolean.TRUE.equals(progress.getIsSolved())) {
                progress.setIsSolved(true);
                progress.setSolvedAt(Instant.now());
            }
            if (progress.getBestExecutionTimeMs() == null || execResult.getExecutionTimeMs() < progress.getBestExecutionTimeMs()) {
                progress.setBestExecutionTimeMs(execResult.getExecutionTimeMs());
            }
        }
        progressRepository.save(progress);

        return DsaSubmissionResponse.builder()
                .id(submission.getId())
                .problemId(problem.getId())
                .problemTitle(problem.getTitle())
                .language(submission.getLanguage())
                .sourceCode(submission.getSourceCode())
                .status(submission.getStatus())
                .executionTimeMs(submission.getExecutionTimeMs())
                .memoryUsedKb(submission.getMemoryUsedKb())
                .passedTestCases(submission.getPassedTestCases())
                .totalTestCases(submission.getTotalTestCases())
                .errorOutput(submission.getErrorOutput())
                .submittedAt(submission.getSubmittedAt())
                .testCaseResults(execResult.getTestCaseResults())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<DsaSubmissionResponse> getUserSubmissions(Long userId, Pageable pageable) {
        Page<DsaSubmission> page = submissionRepository.findByUserIdOrderByIdDesc(userId, pageable);
        List<DsaSubmissionResponse> dtos = page.getContent().stream().map(this::toResponseDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<DsaSubmissionResponse> getProblemSubmissions(Long userId, Long problemId, Pageable pageable) {
        Page<DsaSubmission> page = submissionRepository.findByUserIdAndProblemIdOrderByIdDesc(userId, problemId, pageable);
        List<DsaSubmissionResponse> dtos = page.getContent().stream().map(this::toResponseDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private DsaSubmissionResponse toResponseDto(DsaSubmission s) {
        return DsaSubmissionResponse.builder()
                .id(s.getId())
                .problemId(s.getProblem().getId())
                .problemTitle(s.getProblem().getTitle())
                .language(s.getLanguage())
                .sourceCode(s.getSourceCode())
                .status(s.getStatus())
                .executionTimeMs(s.getExecutionTimeMs())
                .memoryUsedKb(s.getMemoryUsedKb())
                .passedTestCases(s.getPassedTestCases())
                .totalTestCases(s.getTotalTestCases())
                .errorOutput(s.getErrorOutput())
                .submittedAt(s.getSubmittedAt())
                .build();
    }
}
