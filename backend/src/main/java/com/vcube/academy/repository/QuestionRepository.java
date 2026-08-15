package com.vcube.academy.repository;

import com.vcube.academy.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.topic.id = :topicId AND q.isActive = true ORDER BY q.id ASC")
    List<Question> findByTopicId(@Param("topicId") Long topicId);

    @Query("SELECT q FROM Question q WHERE q.topic.id = :topicId AND q.difficulty = :difficulty AND q.isActive = true ORDER BY FUNCTION('RANDOM')")
    List<Question> findByTopicIdAndDifficulty(@Param("topicId") Long topicId, @Param("difficulty") String difficulty);

    @Query(value = "SELECT * FROM questions WHERE topic_id = :topicId AND is_active = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByTopicId(@Param("topicId") Long topicId, @Param("limit") int limit);

    @Query(value = "SELECT * FROM questions WHERE course_id = :courseId AND is_active = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByCourseId(@Param("courseId") Long courseId, @Param("limit") int limit);

    @Query(value = "SELECT * FROM questions WHERE course_id = :courseId AND difficulty = :difficulty AND is_active = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByCourseIdAndDifficulty(@Param("courseId") Long courseId, @Param("difficulty") String difficulty, @Param("limit") int limit);

    @Query(value = "SELECT * FROM questions WHERE is_active = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandom(@Param("limit") int limit);

    List<Question> findByTopicIdOrderByIdAsc(Long topicId);

    List<Question> findByCourseIdOrderByIdAsc(Long courseId);

    List<Question> findAllByOrderByIdDesc();

    long countByTopicIdAndIsActiveTrue(Long topicId);

    long countByIsActiveTrue();
}
