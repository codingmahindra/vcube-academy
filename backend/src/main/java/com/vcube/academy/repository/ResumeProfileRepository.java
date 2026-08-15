package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeProfileRepository extends JpaRepository<ResumeProfile, Long> {
    Optional<ResumeProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
