package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "topic_contents")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TopicContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false, unique = true)
    private Topic topic;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "simple_explanation", columnDefinition = "TEXT")
    private String simpleExplanation;

    @Column(name = "real_world_example", columnDefinition = "TEXT")
    private String realWorldExample;

    @Column(name = "syntax_example", columnDefinition = "TEXT")
    private String syntaxExample;

    @Column(name = "code_example", columnDefinition = "TEXT")
    private String codeExample;

    @Column(name = "code_language", nullable = false, length = 30)
    @Builder.Default
    private String codeLanguage = "java";

    @Column(name = "interview_points", columnDefinition = "TEXT")
    private String interviewPoints;

    @Column(name = "common_mistakes", columnDefinition = "TEXT")
    private String commonMistakes;

    @Column(name = "practice_questions", columnDefinition = "TEXT")
    private String practiceQuestions;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
