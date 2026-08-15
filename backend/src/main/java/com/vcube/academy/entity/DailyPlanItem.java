package com.vcube.academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "daily_plan_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private DailyPreparationPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DailyPlanCategory category;

    @Column(nullable = false)
    private String title;

    @Column(name = "target_count", nullable = false)
    @Builder.Default
    private Integer targetCount = 1;

    @Column(name = "completed_count", nullable = false)
    @Builder.Default
    private Integer completedCount = 0;

    @Column(name = "action_link")
    private String actionLink;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}
