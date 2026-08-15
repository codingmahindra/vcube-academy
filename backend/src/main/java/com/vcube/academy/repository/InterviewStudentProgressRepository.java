package com.vcube.academy.repository;

import com.vcube.academy.entity.InterviewStudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewStudentProgressRepository extends JpaRepository<InterviewStudentProgress, Long> {
    Optional<InterviewStudentProgress> findByUserIdAndQuestionId(Long userId, Long questionId);
    List<InterviewStudentProgress> findByUserId(Long userId);
    long countByUserIdAndIsCompletedTrue(Long userId);

    @Query("SELECT COUNT(isp) FROM InterviewStudentProgress isp WHERE isp.user.id = :userId AND isp.question.topic.category.id = :categoryId AND isp.isCompleted = true")
    long countCompletedByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(isp) FROM InterviewStudentProgress isp WHERE isp.user.id = :userId AND isp.question.topic.id = :topicId AND isp.isCompleted = true")
    long countCompletedByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);
}
