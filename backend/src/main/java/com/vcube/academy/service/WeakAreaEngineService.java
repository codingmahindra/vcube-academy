package com.vcube.academy.service;

import com.vcube.academy.dto.career.WeakAreaDto;
import com.vcube.academy.entity.CareerWeakArea;
import com.vcube.academy.entity.User;
import com.vcube.academy.repository.CareerWeakAreaRepository;
import com.vcube.academy.repository.ResumeMissingSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WeakAreaEngineService {

    private final CareerWeakAreaRepository weakAreaRepository;
    private final ResumeMissingSkillRepository missingSkillRepository;

    public List<WeakAreaDto> getWeakAreasForStudent(User student) {
        List<CareerWeakArea> existing = weakAreaRepository.findByUserIdOrderByWeaknessScoreDesc(student.getId());
        if (!existing.isEmpty()) {
            return existing.stream().map(this::toDto).toList();
        }

        // Generate baseline weak areas if none exist
        List<CareerWeakArea> defaults = List.of(
                CareerWeakArea.builder()
                        .user(student)
                        .skillOrTopicName("Spring Boot Transaction Management")
                        .category("FRAMEWORK")
                        .weaknessScore(78)
                        .sourceModule("MCQ")
                        .recommendationText("Review @Transactional propagation modes, rollback rules, and isolation levels.")
                        .actionLink("/student/courses")
                        .build(),
                CareerWeakArea.builder()
                        .user(student)
                        .skillOrTopicName("Binary Tree DFS & BFS Traversals")
                        .category("DSA")
                        .weaknessScore(72)
                        .sourceModule("DSA")
                        .recommendationText("Practice Inorder, Preorder, Postorder, and Level-Order traversals.")
                        .actionLink("/student/dsa")
                        .build(),
                CareerWeakArea.builder()
                        .user(student)
                        .skillOrTopicName("PostgreSQL Subqueries & Indexing")
                        .category("DATABASE")
                        .weaknessScore(65)
                        .sourceModule("INTERVIEW")
                        .recommendationText("Master EXPLAIN ANALYZE, B-Tree vs GIN indexing, and correlated subqueries.")
                        .actionLink("/student/interview")
                        .build()
        );

        weakAreaRepository.saveAll(defaults);
        return defaults.stream().map(this::toDto).toList();
    }

    private WeakAreaDto toDto(CareerWeakArea w) {
        return WeakAreaDto.builder()
                .id(w.getId())
                .skillOrTopicName(w.getSkillOrTopicName())
                .category(w.getCategory())
                .weaknessScore(w.getWeaknessScore())
                .sourceModule(w.getSourceModule())
                .recommendationText(w.getRecommendationText())
                .actionLink(w.getActionLink())
                .build();
    }
}
