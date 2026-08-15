package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "placement_papers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "paper_year", nullable = false, length = 20)
    @Builder.Default
    private String year = "2024";

    @Column(name = "target_role", nullable = false, length = 150)
    @Builder.Default
    private String targetRole = "Software Engineer";

    @Column(name = "round_name", nullable = false, length = 100)
    @Builder.Default
    private String roundName = "Online Assessment";

    @Column(name = "duration_minutes", nullable = false)
    @Builder.Default
    private Integer durationMinutes = 60;

    @Column(name = "total_marks", nullable = false)
    @Builder.Default
    private Integer totalMarks = 100;

    @Column(name = "passing_marks", nullable = false)
    @Builder.Default
    private Integer passingMarks = 60;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private InterviewDifficulty difficulty = InterviewDifficulty.INTERMEDIATE;

    @Enumerated(EnumType.STRING)
    @Column(name = "paper_source", nullable = false, length = 50)
    @Builder.Default
    private PlacementPaperSource paperSource = PlacementPaperSource.VERIFIED;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<PlacementPaperQuestion> questions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
