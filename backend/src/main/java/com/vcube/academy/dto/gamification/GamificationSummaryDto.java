package com.vcube.academy.dto.gamification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamificationSummaryDto {
    private Long studentId;
    private int currentStreakDays;
    private int longestStreakDays;
    private int totalXpPoints;
    private int unlockedBadgesCount;
    private int totalBadgesCount;
    private List<BadgeDto> badges;
    private String nextMilestoneGoal;
}
