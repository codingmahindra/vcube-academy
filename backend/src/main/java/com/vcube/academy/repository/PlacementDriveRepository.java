package com.vcube.academy.repository;

import com.vcube.academy.entity.PlacementDrive;
import com.vcube.academy.entity.PlacementDriveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {
    List<PlacementDrive> findByStatusOrderByDriveDateAsc(PlacementDriveStatus status);
    Page<PlacementDrive> findAllByOrderByDriveDateAsc(Pageable pageable);
    List<PlacementDrive> findByCompanyId(Long companyId);
}
