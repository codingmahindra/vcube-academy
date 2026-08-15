package com.vcube.academy.repository;

import com.vcube.academy.entity.CareerWeakArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerWeakAreaRepository extends JpaRepository<CareerWeakArea, Long> {
    List<CareerWeakArea> findByUserIdOrderByWeaknessScoreDesc(Long userId);
    void deleteByUserId(Long userId);
}
