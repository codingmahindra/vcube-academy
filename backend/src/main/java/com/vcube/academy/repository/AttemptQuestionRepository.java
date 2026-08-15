package com.vcube.academy.repository;

import com.vcube.academy.entity.AttemptQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptQuestionRepository extends JpaRepository<AttemptQuestion, Long> {

    @Query("SELECT aq FROM AttemptQuestion aq JOIN FETCH aq.question q WHERE aq.attempt.id = :attemptId ORDER BY aq.displayOrder ASC")
    List<AttemptQuestion> findByAttemptIdOrdered(@Param("attemptId") Long attemptId);

    @Query("SELECT aq FROM AttemptQuestion aq JOIN FETCH aq.question q WHERE aq.attempt.id = :attemptId AND aq.displayOrder = :order")
    Optional<AttemptQuestion> findByAttemptIdAndOrder(@Param("attemptId") Long attemptId, @Param("order") int order);
}
