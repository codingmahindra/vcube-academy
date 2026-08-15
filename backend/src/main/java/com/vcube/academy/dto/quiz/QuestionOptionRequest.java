package com.vcube.academy.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionRequest {

    private Long id;

    @NotBlank(message = "Option label is required (e.g. A, B, C, D)")
    private String optionLabel;

    @NotBlank(message = "Option text is required")
    private String optionText;

    private Boolean isCorrect;

    private String whyWrong;
}
