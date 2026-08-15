package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeProjectRepository extends JpaRepository<ResumeProject, Long> {
    List<ResumeProject> findByVersionIdOrderByDisplayOrderAsc(Long versionId);
    void deleteByVersionId(Long versionId);
}
