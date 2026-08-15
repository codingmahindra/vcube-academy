package com.vcube.academy.service;

import com.vcube.academy.dto.career.DailyPlanDto;
import com.vcube.academy.dto.career.DailyPlanItemDto;
import com.vcube.academy.entity.DailyPlanCategory;
import com.vcube.academy.entity.DailyPlanItem;
import com.vcube.academy.entity.DailyPreparationPlan;
import com.vcube.academy.entity.User;
import com.vcube.academy.repository.DailyPlanItemRepository;
import com.vcube.academy.repository.DailyPreparationPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyPreparationService {

    private final DailyPreparationPlanRepository planRepository;
    private final DailyPlanItemRepository itemRepository;

    public DailyPlanDto getOrCreateTodayPlan(User student) {
        LocalDate today = LocalDate.now();
        DailyPreparationPlan plan = planRepository.findByUserIdAndPlanDate(student.getId(), today)
                .orElseGet(() -> createDefaultDailyPlan(student, today));

        List<DailyPlanItemDto> itemDtos = plan.getItems().stream().map(i ->
                DailyPlanItemDto.builder()
                        .id(i.getId())
                        .category(i.getCategory())
                        .title(i.getTitle())
                        .targetCount(i.getTargetCount())
                        .completedCount(i.getCompletedCount())
                        .actionLink(i.getActionLink())
                        .isCompleted(i.getIsCompleted())
                        .displayOrder(i.getDisplayOrder())
                        .build()
        ).toList();

        return DailyPlanDto.builder()
                .id(plan.getId())
                .planDate(plan.getPlanDate())
                .totalTasks(plan.getTotalTasks())
                .completedTasks(plan.getCompletedTasks())
                .completionPercentage(plan.getCompletionPercentage())
                .status(plan.getStatus())
                .items(itemDtos)
                .build();
    }

    public DailyPlanDto toggleTaskCompletion(Long itemId, User student) {
        DailyPlanItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Daily plan task not found: " + itemId));

        if (!item.getPlan().getUser().getId().equals(student.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied to task");
        }

        item.setIsCompleted(!item.getIsCompleted());
        item.setCompletedCount(item.getIsCompleted() ? item.getTargetCount() : 0);
        itemRepository.save(item);

        DailyPreparationPlan plan = item.getPlan();
        int completed = (int) plan.getItems().stream().filter(DailyPlanItem::getIsCompleted).count();
        int total = plan.getItems().size();
        int pct = total > 0 ? (completed * 100) / total : 0;

        plan.setCompletedTasks(completed);
        plan.setCompletionPercentage(pct);
        plan.setStatus(pct == 100 ? "COMPLETED" : "IN_PROGRESS");
        planRepository.save(plan);

        return getOrCreateTodayPlan(student);
    }

    private DailyPreparationPlan createDefaultDailyPlan(User student, LocalDate date) {
        DailyPreparationPlan plan = DailyPreparationPlan.builder()
                .user(student)
                .planDate(date)
                .totalTasks(7)
                .completedTasks(0)
                .completionPercentage(0)
                .status("IN_PROGRESS")
                .items(new ArrayList<>())
                .build();

        plan = planRepository.save(plan);

        List<DailyPlanItem> items = List.of(
                DailyPlanItem.builder().plan(plan).category(DailyPlanCategory.JAVA_TOPIC).title("Core Java & JVM: Complete 2 Topics").targetCount(2).actionLink("/student/courses").displayOrder(1).build(),
                DailyPlanItem.builder().plan(plan).category(DailyPlanCategory.MCQ_PRACTICE).title("Technical Quiz: Solve 20 Java MCQs").targetCount(20).actionLink("/student/courses").displayOrder(2).build(),
                DailyPlanItem.builder().plan(plan).category(DailyPlanCategory.DSA_PROBLEM).title("DSA Problem Solving: Solve 2 LeetCode-style Problems").targetCount(2).actionLink("/student/dsa").displayOrder(3).build(),
                DailyPlanItem.builder().plan(plan).category(DailyPlanCategory.SQL_PRACTICE).title("SQL & Database: Practice 10 Complex JOIN & Subqueries").targetCount(10).actionLink("/student/courses").displayOrder(4).build(),
                DailyPlanItem.builder().plan(plan).category(DailyPlanCategory.INTERVIEW_QA).title("Interview Prep: Review 5 Company Interview Questions").targetCount(5).actionLink("/student/interview").displayOrder(5).build(),
                DailyPlanItem.builder().plan(plan).category(DailyPlanCategory.MOCK_INTERVIEW).title("Live Mock Interview: Take 1 Interactive Evaluation").targetCount(1).actionLink("/student/interview/mock").displayOrder(6).build(),
                DailyPlanItem.builder().plan(plan).category(DailyPlanCategory.JOB_APPLY).title("Job Applications: Apply to 3 Matching Openings").targetCount(3).actionLink("/student/jobs").displayOrder(7).build()
        );

        itemRepository.saveAll(items);
        plan.setItems(items);
        return plan;
    }
}
