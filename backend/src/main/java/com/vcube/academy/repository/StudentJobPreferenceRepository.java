package com.vcube.academy.repository;

import com.vcube.academy.entity.StudentJobPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentJobPreferenceRepository extends JpaRepository<StudentJobPreference, Long> {
    Optional<StudentJobPreference> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
