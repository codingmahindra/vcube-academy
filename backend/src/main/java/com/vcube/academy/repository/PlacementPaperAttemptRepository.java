package com.vcube.academy.repository;

import com.vcube.academy.entity.PlacementPaperAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementPaperAttemptRepository extends JpaRepository<PlacementPaperAttempt, Long> {
    List<PlacementPaperAttempt> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<PlacementPaperAttempt> findByPaperIdAndUserId(Long paperId, Long userId);
    Optional<PlacementPaperAttempt> findByIdAndUserId(Long id, Long userId);
}
