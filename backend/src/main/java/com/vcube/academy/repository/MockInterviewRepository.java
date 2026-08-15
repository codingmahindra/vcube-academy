package com.vcube.academy.repository;

import com.vcube.academy.entity.MockInterview;
import com.vcube.academy.entity.MockInterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {
    Page<MockInterview> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<MockInterview> findByUserIdAndStatus(Long userId, MockInterviewStatus status);
    Optional<MockInterview> findByIdAndUserId(Long id, Long userId);
    long countByUserIdAndStatus(Long userId, MockInterviewStatus status);

    @Query("SELECT AVG(m.overallScore) FROM MockInterview m WHERE m.user.id = :userId AND m.status = 'COMPLETED'")
    Double getAverageScoreByUserId(@Param("userId") Long userId);
}
