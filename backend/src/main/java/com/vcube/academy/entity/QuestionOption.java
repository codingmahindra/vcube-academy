package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "question_options")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "option_label", nullable = false, length = 5)
    private String optionLabel;   // 'A', 'B', 'C', 'D'

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    @Builder.Default
    private Boolean isCorrect = false;

    // Shown only after submission when student picks this wrong option
    @Column(name = "why_wrong", columnDefinition = "TEXT")
    private String whyWrong;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
