package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "topic_completions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "topic_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TopicCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "completed_at", nullable = false)
    @Builder.Default
    private Instant completedAt = Instant.now();
}
