package com.vcube.academy.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInterviewStartRequest {
    private String roleTitle;
    private Long targetCompanyId;
    private String interviewType;
    private String difficulty;
    private Integer totalQuestions;
}
