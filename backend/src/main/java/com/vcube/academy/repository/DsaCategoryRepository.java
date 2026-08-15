package com.vcube.academy.repository;

import com.vcube.academy.entity.DsaCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DsaCategoryRepository extends JpaRepository<DsaCategory, Long> {

    List<DsaCategory> findAllByIsActiveTrueOrderByDisplayOrderAsc();

    Optional<DsaCategory> findBySlug(String slug);
}
