package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "career_weak_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerWeakArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "skill_or_topic_name", nullable = false, length = 150)
    private String skillOrTopicName;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "weakness_score", nullable = false)
    @Builder.Default
    private Integer weaknessScore = 70; // 0-100 (higher = weaker)

    @Column(name = "source_module", nullable = false, length = 50)
    private String sourceModule; // MCQ, DSA, INTERVIEW, RESUME, PLACEMENT_PAPER

    @Column(name = "recommendation_text", columnDefinition = "TEXT")
    private String recommendationText;

    @Column(name = "action_link")
    private String actionLink;

    @CreationTimestamp
    @Column(name = "detected_at", updatable = false)
    private Instant detectedAt;
}
