package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeKeywordRepository extends JpaRepository<ResumeKeyword, Long> {
    List<ResumeKeyword> findByAnalysisId(Long analysisId);
}
