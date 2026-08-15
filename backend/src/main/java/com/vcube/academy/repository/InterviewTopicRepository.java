package com.vcube.academy.repository;

import com.vcube.academy.entity.InterviewTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewTopicRepository extends JpaRepository<InterviewTopic, Long> {
    List<InterviewTopic> findByCategoryIdAndIsActiveTrueOrderByDisplayOrderAsc(Long categoryId);
    List<InterviewTopic> findByIsActiveTrueOrderByDisplayOrderAsc();
    Optional<InterviewTopic> findByCategoryIdAndSlug(Long categoryId, String slug);
    Optional<InterviewTopic> findBySlug(String slug);
}
