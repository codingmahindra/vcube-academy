package com.vcube.academy.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    private Long topicId;

    private Long courseId;

    @NotBlank(message = "Question text cannot be blank")
    private String questionText;

    @NotBlank(message = "Difficulty cannot be blank")
    private String difficulty;

    private String explanation;

    private String interviewPoint;

    private String companyTags;

    private Boolean isActive;

    @NotEmpty(message = "Question must have options")
    @Valid
    private List<QuestionOptionRequest> options;
}
