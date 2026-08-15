package com.vcube.academy.repository;

import com.vcube.academy.entity.StudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {

    Optional<StudentProgress> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT sp FROM StudentProgress sp JOIN FETCH sp.course WHERE sp.student.id = :studentId ORDER BY sp.lastActivityAt DESC NULLS LAST")
    List<StudentProgress> findByStudentIdWithCourse(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(DISTINCT sp.course) FROM StudentProgress sp WHERE sp.student.id = :studentId")
    long countDistinctCoursesByStudentId(@Param("studentId") Long studentId);
}
