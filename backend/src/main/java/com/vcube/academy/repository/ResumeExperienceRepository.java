package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeExperienceRepository extends JpaRepository<ResumeExperience, Long> {
    List<ResumeExperience> findByVersionIdOrderByDisplayOrderAsc(Long versionId);
    void deleteByVersionId(Long versionId);
}
