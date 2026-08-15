package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_skill_mappings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"job_id", "skill_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkillMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private JobSkill skill;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = true;
}
