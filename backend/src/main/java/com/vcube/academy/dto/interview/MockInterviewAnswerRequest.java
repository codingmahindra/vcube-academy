package com.vcube.academy.dto.interview;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInterviewAnswerRequest {
    @NotNull(message = "Question order is required")
    private Integer questionOrder;

    @NotBlank(message = "Answer is required")
    private String userAnswer;

    private Integer timeTakenSeconds;
}
