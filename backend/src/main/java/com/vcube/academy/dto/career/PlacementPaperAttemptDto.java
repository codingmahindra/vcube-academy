package com.vcube.academy.dto.career;

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
public class PlacementPaperAttemptDto {
    private Long id;
    private Long paperId;
    private String paperTitle;
    private String companyName;
    private int durationMinutes;
    private Instant startTime;
    private Instant endTime;
    private String status;
    private List<PlacementPaperQuestionDto> questions;
}
