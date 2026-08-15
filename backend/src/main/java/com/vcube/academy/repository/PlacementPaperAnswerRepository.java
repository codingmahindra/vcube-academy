package com.vcube.academy.repository;

import com.vcube.academy.entity.PlacementPaperAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementPaperAnswerRepository extends JpaRepository<PlacementPaperAnswer, Long> {
    List<PlacementPaperAnswer> findByAttemptId(Long attemptId);
    Optional<PlacementPaperAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
}
