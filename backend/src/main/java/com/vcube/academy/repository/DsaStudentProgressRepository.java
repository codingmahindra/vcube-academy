package com.vcube.academy.repository;

import com.vcube.academy.entity.DsaDifficulty;
import com.vcube.academy.entity.DsaStudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DsaStudentProgressRepository extends JpaRepository<DsaStudentProgress, Long> {

    Optional<DsaStudentProgress> findByUserIdAndProblemId(Long userId, Long problemId);

    List<DsaStudentProgress> findByUserId(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndIsSolvedTrue(Long userId);

    @Query("SELECT COUNT(p) FROM DsaStudentProgress p WHERE p.user.id = :userId AND p.isSolved = true AND p.problem.difficulty = :difficulty")
    long countSolvedByUserIdAndDifficulty(@Param("userId") Long userId, @Param("difficulty") DsaDifficulty difficulty);

    @Query("SELECT COUNT(p) FROM DsaStudentProgress p WHERE p.user.id = :userId AND p.isSolved = true AND p.problem.category.id = :categoryId")
    long countSolvedByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
}
