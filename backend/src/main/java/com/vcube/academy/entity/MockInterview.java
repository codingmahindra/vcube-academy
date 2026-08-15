package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mock_interviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "role_title", nullable = false, length = 100)
    @Builder.Default
    private String roleTitle = "Java Full Stack Developer";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_company_id")
    private Company targetCompany;

    @Column(name = "interview_type", nullable = false, length = 50)
    @Builder.Default
    private String interviewType = "TECHNICAL";

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String difficulty = "INTERMEDIATE";

    @Column(name = "total_questions", nullable = false)
    @Builder.Default
    private Integer totalQuestions = 5;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private MockInterviewStatus status = MockInterviewStatus.IN_PROGRESS;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "technical_score")
    private Double technicalScore;

    @Column(name = "java_score")
    private Double javaScore;

    @Column(name = "sql_score")
    private Double sqlScore;

    @Column(name = "spring_score")
    private Double springScore;

    @Column(name = "dsa_score")
    private Double dsaScore;

    @Column(name = "hr_score")
    private Double hrScore;

    @Column(name = "communication_score")
    private Double communicationScore;

    @Column(name = "interview_readiness_percentage")
    private Integer interviewReadinessPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_status", length = 50)
    private InterviewReadiness recommendationStatus;

    @Column(name = "feedback_summary", columnDefinition = "TEXT")
    private String feedbackSummary;

    @OneToMany(mappedBy = "mockInterview", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    @Builder.Default
    private List<MockInterviewQuestion> questions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
