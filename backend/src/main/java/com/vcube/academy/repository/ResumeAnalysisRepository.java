package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    List<ResumeAnalysis> findByVersionIdOrderByCreatedAtDesc(Long versionId);
    Optional<ResumeAnalysis> findFirstByVersionIdOrderByCreatedAtDesc(Long versionId);
    Optional<ResumeAnalysis> findByIdAndVersionProfileUserId(Long id, Long userId);

    @Query("SELECT AVG(ra.overallAtsScore) FROM ResumeAnalysis ra")
    Double getGlobalAverageAtsScore();

    @Query("SELECT COUNT(ra) FROM ResumeAnalysis ra")
    Long getTotalAnalysesCount();
}
