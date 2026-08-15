package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeAnalysisHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysisHistoryRepository extends JpaRepository<ResumeAnalysisHistory, Long> {
    List<ResumeAnalysisHistory> findByVersionIdOrderByAnalyzedAtDesc(Long versionId);
}
