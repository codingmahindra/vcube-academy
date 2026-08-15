package com.vcube.academy.repository;

import com.vcube.academy.entity.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {
    List<JobSkill> findAllByOrderByNameAsc();
    Optional<JobSkill> findBySlug(String slug);
    Optional<JobSkill> findByNameIgnoreCase(String name);
}
