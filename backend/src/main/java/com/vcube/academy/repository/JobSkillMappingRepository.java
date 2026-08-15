package com.vcube.academy.repository;

import com.vcube.academy.entity.JobSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSkillMappingRepository extends JpaRepository<JobSkillMapping, Long> {
    List<JobSkillMapping> findByJobId(Long jobId);
    void deleteByJobId(Long jobId);
}
