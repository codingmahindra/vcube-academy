package com.vcube.academy.dto.interview;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewEvaluationRequest {
    @NotBlank(message = "User answer cannot be empty")
    private String userAnswer;
}
