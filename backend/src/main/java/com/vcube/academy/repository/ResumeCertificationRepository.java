package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeCertificationRepository extends JpaRepository<ResumeCertification, Long> {
    List<ResumeCertification> findByVersionIdOrderByDisplayOrderAsc(Long versionId);
    void deleteByVersionId(Long versionId);
}
