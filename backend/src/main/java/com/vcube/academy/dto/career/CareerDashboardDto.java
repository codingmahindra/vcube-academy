package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerDashboardDto {
    private int profileCompletionPercentage;
    private int latestAtsScore;
    private int averageJobMatchPercentage;
    private int coursesEnrolled;
    private int coursesCompleted;
    private int totalTopicsCompleted;
    private int mcqTotalAttempts;
    private double mcqAccuracyPercentage;
    private int dsaProblemsSolved;
    private int mockInterviewsCompleted;
    private int averageMockScore;
    private String interviewReadinessStatus; // READY, NEEDS_MORE_PREPARATION, NOT_READY
    private int totalApplicationsCount;
    private int shortlistedApplicationsCount;
    private int interviewScheduledCount;
    private int offersCount;
    private List<String> topWeakSkills;
    private String currentRoadmapStage;
    private int dailyGoalCompletionPercentage;
}
