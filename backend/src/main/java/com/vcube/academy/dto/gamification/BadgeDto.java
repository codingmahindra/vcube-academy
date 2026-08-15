package com.vcube.academy.dto.gamification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDto {
    private Long id;
    private String badgeCode;
    private String badgeName;
    private String description;
    private String iconName;
    private String category;
    private LocalDateTime earnedAt;
    private boolean isUnlocked;
}
