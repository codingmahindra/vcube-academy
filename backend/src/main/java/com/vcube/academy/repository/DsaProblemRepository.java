package com.vcube.academy.repository;

import com.vcube.academy.entity.DsaDifficulty;
import com.vcube.academy.entity.DsaProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DsaProblemRepository extends JpaRepository<DsaProblem, Long>, JpaSpecificationExecutor<DsaProblem> {

    Optional<DsaProblem> findBySlug(String slug);

    long countByIsPublishedTrue();

    long countByIsPublishedTrueAndDifficulty(DsaDifficulty difficulty);

    @Query("SELECT COUNT(p) FROM DsaProblem p WHERE p.category.id = :categoryId AND p.isPublished = true")
    long countPublishedByCategoryId(@Param("categoryId") Long categoryId);
}
