package com.vcube.academy.repository;

import com.vcube.academy.entity.DailyPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyPlanItemRepository extends JpaRepository<DailyPlanItem, Long> {
    List<DailyPlanItem> findByPlanIdOrderByDisplayOrderAsc(Long planId);
}
