package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "student_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "topics_completed", nullable = false)
    @Builder.Default
    private Integer topicsCompleted = 0;

    @Column(name = "total_topics", nullable = false)
    @Builder.Default
    private Integer totalTopics = 0;

    @Column(name = "quiz_attempts", nullable = false)
    @Builder.Default
    private Integer quizAttempts = 0;

    @Column(name = "total_correct", nullable = false)
    @Builder.Default
    private Integer totalCorrect = 0;

    @Column(name = "total_attempted_questions", nullable = false)
    @Builder.Default
    private Integer totalAttemptedQuestions = 0;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
