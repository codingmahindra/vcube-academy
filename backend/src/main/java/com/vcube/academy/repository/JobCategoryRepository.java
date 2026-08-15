package com.vcube.academy.repository;

import com.vcube.academy.entity.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {
    List<JobCategory> findByIsActiveTrueOrderByNameAsc();
    Optional<JobCategory> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
