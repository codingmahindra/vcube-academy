package com.vcube.academy.entity;

import com.vcube.academy.enums.BadgeCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "badge_code", nullable = false, length = 100)
    private String badgeCode;

    @Column(name = "badge_name", nullable = false)
    private String badgeName;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_name", nullable = false, length = 100)
    private String iconName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private BadgeCategory category;

    @Column(name = "earned_at", nullable = false)
    @Builder.Default
    private LocalDateTime earnedAt = LocalDateTime.now();
}
