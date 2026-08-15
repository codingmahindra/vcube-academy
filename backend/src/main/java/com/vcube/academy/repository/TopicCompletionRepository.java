package com.vcube.academy.repository;

import com.vcube.academy.entity.TopicCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TopicCompletionRepository extends JpaRepository<TopicCompletion, Long> {

    Optional<TopicCompletion> findByStudentIdAndTopicId(Long studentId, Long topicId);

    boolean existsByStudentIdAndTopicId(Long studentId, Long topicId);

    @Query("SELECT tc FROM TopicCompletion tc JOIN FETCH tc.topic t WHERE tc.student.id = :studentId")
    List<TopicCompletion> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT tc.topic.id FROM TopicCompletion tc WHERE tc.student.id = :studentId")
    Set<Long> findCompletedTopicIdsByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(tc) FROM TopicCompletion tc JOIN tc.topic t JOIN t.module m WHERE tc.student.id = :studentId AND m.course.id = :courseId")
    long countByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    long countByStudentId(Long studentId);
}
