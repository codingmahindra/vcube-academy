package com.vcube.academy.repository;

import com.vcube.academy.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("SELECT t FROM Topic t WHERE t.module.id = :moduleId AND t.isPublished = true ORDER BY t.displayOrder ASC")
    List<Topic> findByModuleIdOrderByDisplayOrder(@Param("moduleId") Long moduleId);

    @Query("SELECT t FROM Topic t JOIN FETCH t.module m JOIN FETCH m.course WHERE t.id = :id AND t.isPublished = true")
    Optional<Topic> findByIdWithModule(@Param("id") Long id);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.module.course.id = :courseId AND t.isPublished = true")
    long countPublishedByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT t FROM Topic t WHERE t.module.course.id = :courseId AND t.isPublished = true ORDER BY t.module.displayOrder ASC, t.displayOrder ASC")
    List<Topic> findAllByCourseIdOrdered(@Param("courseId") Long courseId);
}
