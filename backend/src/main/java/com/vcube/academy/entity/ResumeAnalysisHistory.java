package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "resume_analysis_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysisHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private ResumeVersion version;

    @Column(name = "score_before")
    private Integer scoreBefore;

    @Column(name = "score_after", nullable = false)
    private Integer scoreAfter;

    @Column(name = "change_summary", columnDefinition = "TEXT")
    private String changeSummary;

    @CreationTimestamp
    @Column(name = "analyzed_at", updatable = false)
    private Instant analyzedAt;
}
