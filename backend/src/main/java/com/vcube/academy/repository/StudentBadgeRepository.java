package com.vcube.academy.repository;

import com.vcube.academy.entity.StudentBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentBadgeRepository extends JpaRepository<StudentBadge, Long> {
    List<StudentBadge> findByStudentIdOrderByEarnedAtDesc(Long studentId);
    Optional<StudentBadge> findByStudentIdAndBadgeCode(Long studentId, String badgeCode);
    boolean existsByStudentIdAndBadgeCode(Long studentId, String badgeCode);
    long countByStudentId(Long studentId);
}
