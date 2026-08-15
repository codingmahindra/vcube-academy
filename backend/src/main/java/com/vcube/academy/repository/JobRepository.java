package com.vcube.academy.repository;

import com.vcube.academy.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    Optional<Job> findByIdAndIsActiveTrue(Long id);

    Optional<Job> findBySlug(String slug);

    List<Job> findByCompanyIdAndIsActiveTrue(Long companyId);

    @Query("SELECT DISTINCT j.location FROM Job j WHERE j.isActive = true ORDER BY j.location ASC")
    List<String> findDistinctLocations();

    long countByIsActiveTrue();

    long countByCompanyId(Long companyId);

    @Query("SELECT j FROM Job j JOIN j.skillMappings sm WHERE sm.skill.id = :skillId AND j.isActive = true")
    List<Job> findBySkillId(@Param("skillId") Long skillId);
}
