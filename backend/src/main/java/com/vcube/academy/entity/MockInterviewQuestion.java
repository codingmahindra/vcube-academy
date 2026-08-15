package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "mock_interview_questions", uniqueConstraints = {
        @UniqueConstraint(name = "uq_mock_question_order", columnNames = {"mock_interview_id", "question_order"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockInterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mock_interview_id", nullable = false)
    private MockInterview mockInterview;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private InterviewQuestion question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = "time_taken_seconds")
    @Builder.Default
    private Integer timeTakenSeconds = 0;

    private Double score;

    @Column(name = "technical_accuracy_score")
    private Double technicalAccuracyScore;

    @Column(name = "completeness_score")
    private Double completenessScore;

    @Column(name = "clarity_score")
    private Double clarityScore;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "missing_points", columnDefinition = "TEXT")
    private String missingPoints;

    @Column(name = "improved_answer", columnDefinition = "TEXT")
    private String improvedAnswer;

    @Column(name = "evaluated_at")
    private Instant evaluatedAt;
}
