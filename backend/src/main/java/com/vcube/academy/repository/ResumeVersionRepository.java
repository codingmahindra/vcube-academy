package com.vcube.academy.repository;

import com.vcube.academy.entity.ResumeVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, Long> {
    List<ResumeVersion> findByProfileIdOrderByUpdatedAtDesc(Long profileId);
    Page<ResumeVersion> findByProfileIdOrderByUpdatedAtDesc(Long profileId, Pageable pageable);
    Optional<ResumeVersion> findByIdAndProfileUserId(Long id, Long userId);
    List<ResumeVersion> findByProfileUserIdOrderByUpdatedAtDesc(Long userId);

    @Query("SELECT AVG(rv.latestAtsScore) FROM ResumeVersion rv WHERE rv.latestAtsScore > 0")
    Double getAverageAtsScore();
}
