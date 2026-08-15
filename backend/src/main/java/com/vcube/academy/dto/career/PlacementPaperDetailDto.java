package com.vcube.academy.dto.career;

import com.vcube.academy.entity.InterviewDifficulty;
import com.vcube.academy.entity.PlacementPaperSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementPaperDetailDto {
    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String slug;
    private String year;
    private String targetRole;
    private String roundName;
    private int durationMinutes;
    private int totalMarks;
    private int passingMarks;
    private InterviewDifficulty difficulty;
    private PlacementPaperSource paperSource;
    private String instructions;
    private List<PlacementPaperQuestionDto> questions;
}
