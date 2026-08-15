package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resume_educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private ResumeVersion version;

    @Column(nullable = false, length = 200)
    private String institution;

    @Column(nullable = false, length = 150)
    private String degree;

    @Column(name = "field_of_study", length = 150)
    private String fieldOfStudy;

    @Column(name = "start_year", length = 20)
    private String startYear;

    @Column(name = "end_year", length = 20)
    private String endYear;

    @Column(name = "score_or_cgpa", length = 50)
    private String scoreOrCgpa;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}
