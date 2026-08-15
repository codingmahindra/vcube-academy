package com.vcube.academy.dto.dsa;

import com.vcube.academy.entity.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsaSubmissionResponse {
    private Long id;
    private Long problemId;
    private String problemTitle;
    private String language;
    private String sourceCode;
    private SubmissionStatus status;
    private Long executionTimeMs;
    private Long memoryUsedKb;
    private Integer passedTestCases;
    private Integer totalTestCases;
    private String errorOutput;
    private Instant submittedAt;
    private List<CodeExecutionResult.TestCaseResultDto> testCaseResults;
}
