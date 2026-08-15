package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "student_job_preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentJobPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "preferred_roles", columnDefinition = "TEXT")
    private String preferredRoles;

    @Column(name = "preferred_locations", columnDefinition = "TEXT")
    private String preferredLocations;

    @Column(name = "preferred_technologies", columnDefinition = "TEXT")
    private String preferredTechnologies;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 50)
    @Builder.Default
    private ExperienceLevel experienceLevel = ExperienceLevel.FRESHER;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode", length = 50)
    @Builder.Default
    private WorkMode workMode = WorkMode.HYBRID;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 50)
    @Builder.Default
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    @Column(name = "expected_salary_min", precision = 12, scale = 2)
    private BigDecimal expectedSalaryMin;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
