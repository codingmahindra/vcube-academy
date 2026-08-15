package com.vcube.academy.repository;

import com.vcube.academy.entity.DsaTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DsaTestCaseRepository extends JpaRepository<DsaTestCase, Long> {

    List<DsaTestCase> findByProblemIdOrderByDisplayOrderAsc(Long problemId);

    List<DsaTestCase> findByProblemIdAndIsSampleTrueOrderByDisplayOrderAsc(Long problemId);
}
