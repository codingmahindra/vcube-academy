package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "company_interview_questions", uniqueConstraints = {
        @UniqueConstraint(name = "uq_company_question", columnNames = {"company_id", "question_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private InterviewQuestion question;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String frequency = "MEDIUM";

    @Column(name = "last_seen_year")
    private Integer lastSeenYear;

    @Column(name = "role_title", length = 100)
    @Builder.Default
    private String roleTitle = "Java Full Stack Developer";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
