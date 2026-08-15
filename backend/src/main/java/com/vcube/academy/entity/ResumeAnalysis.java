package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resume_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private ResumeVersion version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job targetJob;

    @Column(name = "target_job_title", length = 150)
    private String targetJobTitle;

    @Column(name = "target_company_name", length = 150)
    private String targetCompanyName;

    @Column(name = "job_description_text", columnDefinition = "TEXT")
    private String jobDescriptionText;

    @Column(name = "overall_ats_score", nullable = false)
    private Integer overallAtsScore;

    @Column(name = "keyword_match_score", nullable = false)
    private Integer keywordMatchScore;

    @Column(name = "skills_match_score", nullable = false)
    private Integer skillsMatchScore;

    @Column(name = "experience_match_score", nullable = false)
    private Integer experienceMatchScore;

    @Column(name = "project_match_score", nullable = false)
    private Integer projectMatchScore;

    @Column(name = "education_match_score", nullable = false)
    private Integer educationMatchScore;

    @Column(name = "structure_score", nullable = false)
    private Integer structureScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_provider", length = 50)
    @Builder.Default
    private AIProvider aiProvider = AIProvider.RULE_BASED;

    @Column(name = "summary_feedback", columnDefinition = "TEXT")
    private String summaryFeedback;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResumeKeyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResumeMissingSkill> missingSkills = new ArrayList<>();

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResumeRecommendation> recommendations = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
