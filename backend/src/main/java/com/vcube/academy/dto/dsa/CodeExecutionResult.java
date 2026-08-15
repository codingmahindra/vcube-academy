package com.vcube.academy.dto.dsa;

import com.vcube.academy.entity.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResult {

    private SubmissionStatus status;
    private long executionTimeMs;
    private long memoryUsedKb;
    private int passedTestCases;
    private int totalTestCases;
    private String errorOutput;
    private List<TestCaseResultDto> testCaseResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseResultDto {
        private Long testCaseId;
        private boolean isSample;
        private boolean isHidden;
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private boolean passed;
        private String error;
    }
}
