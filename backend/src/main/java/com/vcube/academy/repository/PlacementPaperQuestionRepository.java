package com.vcube.academy.repository;

import com.vcube.academy.entity.PlacementPaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementPaperQuestionRepository extends JpaRepository<PlacementPaperQuestion, Long> {
    List<PlacementPaperQuestion> findByPaperIdOrderByDisplayOrderAsc(Long paperId);
}
