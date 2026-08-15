package com.vcube.academy.repository;

import com.vcube.academy.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c JOIN FETCH c.category WHERE c.isPublished = true ORDER BY c.displayOrder ASC")
    List<Course> findAllPublished();

    @Query("SELECT c FROM Course c JOIN FETCH c.category cat WHERE cat.slug = :categorySlug AND c.isPublished = true ORDER BY c.displayOrder ASC")
    List<Course> findPublishedByCategorySlug(@Param("categorySlug") String categorySlug);

    @Query("SELECT c FROM Course c JOIN FETCH c.category WHERE c.id = :id")
    Optional<Course> findByIdWithCategory(@Param("id") Long id);

    Optional<Course> findBySlug(String slug);

    long countByIsPublishedTrue();
}
