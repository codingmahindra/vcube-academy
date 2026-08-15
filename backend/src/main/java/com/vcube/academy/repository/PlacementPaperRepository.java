package com.vcube.academy.repository;

import com.vcube.academy.entity.PlacementPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementPaperRepository extends JpaRepository<PlacementPaper, Long> {
    List<PlacementPaper> findByIsActiveTrueOrderByCreatedAtDesc();
    List<PlacementPaper> findByCompanyIdAndIsActiveTrue(Long companyId);
    Optional<PlacementPaper> findBySlug(String slug);
}
