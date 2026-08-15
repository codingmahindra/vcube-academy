package com.vcube.academy.repository;

import com.vcube.academy.entity.DailyPreparationPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyPreparationPlanRepository extends JpaRepository<DailyPreparationPlan, Long> {
    Optional<DailyPreparationPlan> findByUserIdAndPlanDate(Long userId, LocalDate planDate);
}
