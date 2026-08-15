package com.vcube.academy.repository;

import com.vcube.academy.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findByAttemptId(Long attemptId);

    Optional<QuizAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);

    @Query("SELECT COUNT(a) FROM QuizAnswer a WHERE a.attempt.id = :attemptId AND a.isCorrect = true")
    long countCorrectByAttemptId(@Param("attemptId") Long attemptId);

    @Query("SELECT SUM(a.isCorrect = true) FROM QuizAnswer a WHERE a.attempt.student.id = :studentId")
    Long sumCorrectByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM QuizAnswer a WHERE a.attempt.student.id = :studentId")
    long countTotalByStudentId(@Param("studentId") Long studentId);
}
