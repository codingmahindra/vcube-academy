package com.vcube.academy.repository;

import com.vcube.academy.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByIsActiveTrueOrderByNameAsc();
    Optional<Company> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
