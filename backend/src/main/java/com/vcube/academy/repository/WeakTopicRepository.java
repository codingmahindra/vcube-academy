package com.vcube.academy.repository;

import com.vcube.academy.entity.WeakTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeakTopicRepository extends JpaRepository<WeakTopic, Long> {

    Optional<WeakTopic> findByStudentIdAndTopicId(Long studentId, Long topicId);

    @Query("SELECT wt FROM WeakTopic wt WHERE wt.student.id = :studentId ORDER BY wt.accuracyPct ASC")
    List<WeakTopic> findByStudentIdOrderByAccuracyAsc(@Param("studentId") Long studentId);

    @Query("SELECT wt FROM WeakTopic wt WHERE wt.student.id = :studentId AND wt.accuracyPct < :threshold ORDER BY wt.accuracyPct ASC")
    List<WeakTopic> findWeakByStudentId(@Param("studentId") Long studentId, @Param("threshold") java.math.BigDecimal threshold);
}
