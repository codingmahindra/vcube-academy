package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resume_missing_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeMissingSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private ResumeAnalysis analysis;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private KeywordCategory category;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String importance = "HIGH";

    @Column(name = "why_it_matters", columnDefinition = "TEXT")
    private String whyItMatters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course recommendedCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dsa_problem_id")
    private DsaProblem recommendedDsaProblem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_question_id")
    private InterviewQuestion recommendedInterviewQuestion;
}
