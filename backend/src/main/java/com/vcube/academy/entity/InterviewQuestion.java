package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private InterviewTopic topic;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 50)
    @Builder.Default
    private InterviewQuestionType questionType = InterviewQuestionType.CONCEPTUAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private InterviewDifficulty difficulty = InterviewDifficulty.INTERMEDIATE;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_round", nullable = false, length = 50)
    @Builder.Default
    private InterviewRoundType interviewRound = InterviewRoundType.ROUND_3_TECHNICAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_source", nullable = false, length = 50)
    @Builder.Default
    private QuestionSource questionSource = QuestionSource.PRACTICE_QUESTION;

    @Column(name = "source_reference")
    private String sourceReference;

    @Column(name = "expected_answer", nullable = false, columnDefinition = "TEXT")
    private String expectedAnswer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "interview_points", columnDefinition = "TEXT")
    private String interviewPoints;

    @Column(name = "common_mistakes", columnDefinition = "TEXT")
    private String commonMistakes;

    @Column(name = "follow_up_questions", columnDefinition = "TEXT")
    private String followUpQuestions;

    @Column(name = "real_world_example", columnDefinition = "TEXT")
    private String realWorldExample;

    @Column(name = "evaluation_keywords", columnDefinition = "TEXT")
    private String evaluationKeywords;

    @Column(name = "is_published", nullable = false)
    @Builder.Default
    private Boolean isPublished = true;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompanyInterviewQuestion> companyMappings = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
