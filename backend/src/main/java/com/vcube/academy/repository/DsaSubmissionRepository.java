package com.vcube.academy.repository;

import com.vcube.academy.entity.DsaSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DsaSubmissionRepository extends JpaRepository<DsaSubmission, Long> {

    Page<DsaSubmission> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    Page<DsaSubmission> findByUserIdAndProblemIdOrderByIdDesc(Long userId, Long problemId, Pageable pageable);

    List<DsaSubmission> findTop10ByUserIdOrderByIdDesc(Long userId);

    long countByUserId(Long userId);
}
