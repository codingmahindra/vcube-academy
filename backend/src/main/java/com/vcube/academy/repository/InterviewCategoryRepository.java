package com.vcube.academy.repository;

import com.vcube.academy.entity.InterviewCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewCategoryRepository extends JpaRepository<InterviewCategory, Long> {
    List<InterviewCategory> findByIsActiveTrueOrderByDisplayOrderAsc();
    Optional<InterviewCategory> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
