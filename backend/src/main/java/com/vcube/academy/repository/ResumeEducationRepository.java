package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeEducationRepository extends JpaRepository<ResumeEducation, Long> {
    List<ResumeEducation> findByVersionIdOrderByDisplayOrderAsc(Long versionId);
    void deleteByVersionId(Long versionId);
}
