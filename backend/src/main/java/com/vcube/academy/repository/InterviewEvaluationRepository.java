package com.vcube.academy.repository;

import com.vcube.academy.entity.InterviewEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewEvaluationRepository extends JpaRepository<InterviewEvaluation, Long> {
    Page<InterviewEvaluation> findByUserIdOrderByEvaluatedAtDesc(Long userId, Pageable pageable);
    List<InterviewEvaluation> findByUserIdAndQuestionIdOrderByEvaluatedAtDesc(Long userId, Long questionId);
}
