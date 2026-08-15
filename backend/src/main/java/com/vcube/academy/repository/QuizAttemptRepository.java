package com.vcube.academy.repository;

import com.vcube.academy.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    @Query("SELECT a FROM QuizAttempt a WHERE a.student.id = :studentId ORDER BY a.startedAt DESC")
    List<QuizAttempt> findByStudentIdOrderByStartedAtDesc(@Param("studentId") Long studentId);

    @Query("SELECT a FROM QuizAttempt a WHERE a.student.id = :studentId AND a.status = 'IN_PROGRESS' ORDER BY a.startedAt DESC")
    List<QuizAttempt> findInProgressByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT a FROM QuizAttempt a JOIN FETCH a.student WHERE a.id = :id AND a.student.id = :studentId")
    Optional<QuizAttempt> findByIdAndStudentId(@Param("id") Long id, @Param("studentId") Long studentId);

    long countByStudentIdAndStatus(Long studentId, String status);

    long countByStudentId(Long studentId);
}
