package com.vcube.academy.repository;

import com.vcube.academy.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    Optional<QuizResult> findByAttemptId(Long attemptId);

    @Query("SELECT r FROM QuizResult r WHERE r.student.id = :studentId ORDER BY r.createdAt DESC")
    List<QuizResult> findByStudentIdOrderByCreatedAtDesc(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(r) FROM QuizResult r WHERE r.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(r.scorePercentage) FROM QuizResult r WHERE r.student.id = :studentId")
    Double avgScoreByStudentId(@Param("studentId") Long studentId);
}
