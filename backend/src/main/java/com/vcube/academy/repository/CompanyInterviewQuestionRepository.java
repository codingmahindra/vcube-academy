package com.vcube.academy.repository;

import com.vcube.academy.entity.CompanyInterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyInterviewQuestionRepository extends JpaRepository<CompanyInterviewQuestion, Long> {
    List<CompanyInterviewQuestion> findByCompanyId(Long companyId);
    List<CompanyInterviewQuestion> findByQuestionId(Long questionId);
    Optional<CompanyInterviewQuestion> findByCompanyIdAndQuestionId(Long companyId, Long questionId);
    boolean existsByCompanyIdAndQuestionId(Long companyId, Long questionId);
}
