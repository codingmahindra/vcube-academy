package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dsa_problems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DsaProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private DsaCategory category;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(nullable = false, unique = true, length = 250)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DsaDifficulty difficulty = DsaDifficulty.EASY;

    @Column(length = 100)
    private String subtopic;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "input_format", columnDefinition = "TEXT")
    private String inputFormat;

    @Column(name = "output_format", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(name = "expected_approach", columnDefinition = "TEXT")
    private String expectedApproach;

    @Column(name = "time_complexity", length = 50)
    private String timeComplexity;

    @Column(name = "space_complexity", length = 50)
    private String spaceComplexity;

    @Column(columnDefinition = "TEXT")
    private String hints;

    @Column(name = "interview_points", columnDefinition = "TEXT")
    private String interviewPoints;

    @Column(name = "company_tags", columnDefinition = "TEXT")
    private String companyTags;

    @Column(name = "java_starter_code", columnDefinition = "TEXT", nullable = false)
    private String javaStarterCode;

    @Column(name = "solution_explanation", columnDefinition = "TEXT")
    private String solutionExplanation;

    @Column(name = "solution_java_code", columnDefinition = "TEXT")
    private String solutionJavaCode;

    @Column(name = "is_published", nullable = false)
    @Builder.Default
    private Boolean isPublished = true;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<DsaTestCase> testCases = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
