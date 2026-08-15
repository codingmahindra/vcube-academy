package com.vcube.academy.repository;

import com.vcube.academy.entity.ApplicationStatus;
import com.vcube.academy.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Page<JobApplication> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);
    List<JobApplication> findByUserId(Long userId);
    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
    Optional<JobApplication> findByUserIdAndJobId(Long userId, Long jobId);
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, ApplicationStatus status);

    @Query("SELECT j.status, COUNT(j) FROM JobApplication j WHERE j.user.id = :userId GROUP BY j.status")
    List<Object[]> countGroupByStatusByUserId(@Param("userId") Long userId);
}
