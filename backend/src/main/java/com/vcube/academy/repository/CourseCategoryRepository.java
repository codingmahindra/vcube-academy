package com.vcube.academy.repository;

import com.vcube.academy.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    List<CourseCategory> findByIsActiveTrueOrderByDisplayOrderAsc();
    Optional<CourseCategory> findBySlug(String slug);
}
