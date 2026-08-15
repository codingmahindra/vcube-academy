package com.vcube.academy.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionAdminDto {
    private Long id;
    private String optionLabel;
    private String optionText;
    private Boolean isCorrect;
    private String whyWrong;
}
