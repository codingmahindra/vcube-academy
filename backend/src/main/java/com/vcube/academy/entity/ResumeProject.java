package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resume_projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private ResumeVersion version;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "tech_stack", length = 255)
    private String techStack;

    @Column(name = "live_url")
    private String liveUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "bullet_points", columnDefinition = "TEXT")
    private String bulletPoints;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}
