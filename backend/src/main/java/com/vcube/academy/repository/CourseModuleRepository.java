package com.vcube.academy.repository;

import com.vcube.academy.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {

    @Query("SELECT m FROM CourseModule m WHERE m.course.id = :courseId ORDER BY m.displayOrder ASC")
    List<CourseModule> findByCourseIdOrderByDisplayOrder(@Param("courseId") Long courseId);
}
