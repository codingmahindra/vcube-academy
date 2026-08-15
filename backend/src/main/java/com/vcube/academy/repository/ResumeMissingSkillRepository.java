package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeMissingSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ResumeMissingSkillRepository extends JpaRepository<ResumeMissingSkill, Long> {
    List<ResumeMissingSkill> findByAnalysisId(Long analysisId);

    @Query("SELECT rms.skillName as skill, COUNT(rms) as count FROM ResumeMissingSkill rms GROUP BY rms.skillName ORDER BY COUNT(rms) DESC")
    List<Map<String, Object>> findTopMissingSkills();
}
