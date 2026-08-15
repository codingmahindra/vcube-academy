package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementPaperAnswerRequest {
    private Long attemptId;
    private Long questionId;
    private String selectedOption;
    private int timeTakenSeconds;
}
