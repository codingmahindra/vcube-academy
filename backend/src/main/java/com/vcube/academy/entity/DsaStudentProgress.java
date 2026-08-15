package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "dsa_student_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "problem_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DsaStudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private DsaProblem problem;

    @Column(name = "is_solved", nullable = false)
    @Builder.Default
    private Boolean isSolved = false;

    @Column(name = "attempt_count", nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(name = "best_execution_time_ms")
    private Long bestExecutionTimeMs;

    @Column(name = "solved_at")
    private Instant solvedAt;

    @UpdateTimestamp
    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;
}
