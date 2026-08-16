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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyPreparationService {

    private final DailyPreparationPlanRepository planRepository;
    private final DailyPlanItemRepository itemRepository;


    // =========================================================
    // GET TODAY'S DAILY PLAN
    // =========================================================
    public DailyPlanDto getOrCreateTodayPlan(User student) {

        LocalDate today = LocalDate.now();

        DailyPreparationPlan plan =
                planRepository.findByUserIdAndPlanDate(
                        student.getId(),
                        today
                ).orElseGet(() ->
                        createDefaultDailyPlan(student, today)
                );


        List<DailyPlanItemDto> itemDtos =
                plan.getItems()
                        .stream()
                        .map(i ->
                                DailyPlanItemDto.builder()

                                        .id(i.getId())

                                        .category(i.getCategory())

                                        .title(i.getTitle())

                                        .targetCount(
                                                i.getTargetCount()
                                        )

                                        .completedCount(
                                                i.getCompletedCount()
                                        )

                                        // IMPORTANT
                                        // Backend entity uses actionLink
                                        // Frontend DTO uses actionRoute
                                        .actionRoute(
                                                i.getActionLink()
                                        )

                                        .isCompleted(
                                                i.getIsCompleted()
                                        )

                                        .displayOrder(
                                                i.getDisplayOrder()
                                        )

                                        .build()
                        )
                        .toList();


        return DailyPlanDto.builder()

                .id(plan.getId())

                .planDate(plan.getPlanDate())

                .totalTasks(plan.getTotalTasks())

                .completedTasks(plan.getCompletedTasks())

                .completionPercentage(
                        plan.getCompletionPercentage()
                )

                .status(plan.getStatus())

                .items(itemDtos)

                .build();
    }


    // =========================================================
    // TOGGLE TASK COMPLETION
    // =========================================================
    public DailyPlanDto toggleTaskCompletion(
            Long itemId,
            User student
    ) {

        DailyPlanItem item =
                itemRepository.findById(itemId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Daily plan task not found: " + itemId
                                )
                        );


        // Make sure the task belongs to this student
        if (!item.getPlan()
                .getUser()
                .getId()
                .equals(student.getId())) {

            throw new AccessDeniedException(
                    "Access denied to task"
            );
        }


        // Toggle completed status
        item.setIsCompleted(
                !item.getIsCompleted()
        );


        // Update completed count
        if (item.getIsCompleted()) {

            item.setCompletedCount(
                    item.getTargetCount()
            );

        } else {

            item.setCompletedCount(0);

        }


        itemRepository.save(item);


        // =====================================================
        // UPDATE PLAN PROGRESS
        // =====================================================

        DailyPreparationPlan plan =
                item.getPlan();


        int completed =
                (int) plan.getItems()
                        .stream()
                        .filter(DailyPlanItem::getIsCompleted)
                        .count();


        int total =
                plan.getItems().size();


        int percentage =
                total > 0
                        ? (completed * 100) / total
                        : 0;


        plan.setCompletedTasks(completed);

        plan.setCompletionPercentage(
                percentage
        );


        if (percentage == 100) {

            plan.setStatus("COMPLETED");

        } else {

            plan.setStatus("IN_PROGRESS");

        }


        planRepository.save(plan);


        // Return updated plan
        return getOrCreateTodayPlan(student);
    }


    // =========================================================
    // CREATE DEFAULT DAILY PLAN
    // =========================================================
    private DailyPreparationPlan createDefaultDailyPlan(
            User student,
            LocalDate date
    ) {

        DailyPreparationPlan plan =
                DailyPreparationPlan.builder()

                        .user(student)

                        .planDate(date)

                        .totalTasks(7)

                        .completedTasks(0)

                        .completionPercentage(0)

                        .status("IN_PROGRESS")

                        .items(new ArrayList<>())

                        .build();


        plan =
                planRepository.save(plan);


        // =====================================================
        // CREATE 7 DAILY TASKS
        // =====================================================

        List<DailyPlanItem> items = List.of(

                // 1. Java
                DailyPlanItem.builder()

                        .plan(plan)

                        .category(
                                DailyPlanCategory.JAVA_TOPIC
                        )

                        .title(
                                "Core Java & JVM: Complete 2 Topics"
                        )

                        .targetCount(2)

                        .completedCount(0)

                        .isCompleted(false)

                        .actionLink(
                                "/student/courses"
                        )

                        .displayOrder(1)

                        .build(),


                // 2. MCQ
                DailyPlanItem.builder()

                        .plan(plan)

                        .category(
                                DailyPlanCategory.MCQ_PRACTICE
                        )

                        .title(
                                "Technical Quiz: Solve 20 Java MCQs"
                        )

                        .targetCount(20)

                        .completedCount(0)

                        .isCompleted(false)

                        .actionLink(
                                "/student/courses"
                        )

                        .displayOrder(2)

                        .build(),


                // 3. DSA
                DailyPlanItem.builder()

                        .plan(plan)

                        .category(
                                DailyPlanCategory.DSA_PROBLEM
                        )

                        .title(
                                "DSA Problem Solving: Solve 2 LeetCode-style Problems"
                        )

                        .targetCount(2)

                        .completedCount(0)

                        .isCompleted(false)

                        .actionLink(
                                "/student/dsa"
                        )

                        .displayOrder(3)

                        .build(),


                // 4. SQL
                DailyPlanItem.builder()

                        .plan(plan)

                        .category(
                                DailyPlanCategory.SQL_PRACTICE
                        )

                        .title(
                                "SQL & Database: Practice 10 Complex JOIN & Subqueries"
                        )

                        .targetCount(10)

                        .completedCount(0)

                        .isCompleted(false)

                        .actionLink(
                                "/student/courses"
                        )

                        .displayOrder(4)

                        .build(),


                // 5. Interview
                DailyPlanItem.builder()

                        .plan(plan)

                        .category(
                                DailyPlanCategory.INTERVIEW_QA
                        )

                        .title(
                                "Interview Prep: Review 5 Company Interview Questions"
                        )

                        .targetCount(5)

                        .completedCount(0)

                        .isCompleted(false)

                        .actionLink(
                                "/student/interview"
                        )

                        .displayOrder(5)

                        .build(),


                // 6. Mock Interview
                DailyPlanItem.builder()

                        .plan(plan)

                        .category(
                                DailyPlanCategory.MOCK_INTERVIEW
                        )

                        .title(
                                "Live Mock Interview: Take 1 Interactive Evaluation"
                        )

                        .targetCount(1)

                        .completedCount(0)

                        .isCompleted(false)

                        .actionLink(
                                "/student/interview/mock"
                        )

                        .displayOrder(6)

                        .build(),


                // 7. Jobs
                DailyPlanItem.builder()

                        .plan(plan)

                        .category(
                                DailyPlanCategory.JOB_APPLY
                        )

                        .title(
                                "Job Applications: Apply to 3 Matching Openings"
                        )

                        .targetCount(3)

                        .completedCount(0)

                        .isCompleted(false)

                        .actionLink(
                                "/student/jobs"
                        )

                        .displayOrder(7)

                        .build()
        );


        itemRepository.saveAll(items);


        plan.setItems(items);


        return plan;
    }
}