package com.vcube.academy.repository;

import com.vcube.academy.entity.InterviewDifficulty;
import com.vcube.academy.entity.InterviewQuestion;
import com.vcube.academy.entity.InterviewQuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long>, JpaSpecificationExecutor<InterviewQuestion> {

    List<InterviewQuestion> findByTopicIdAndIsPublishedTrue(Long topicId);

    Page<InterviewQuestion> findByTopicIdAndIsPublishedTrue(Long topicId, Pageable pageable);

    @Query("SELECT q FROM InterviewQuestion q JOIN q.companyMappings cm WHERE cm.company.id = :companyId AND q.isPublished = true")
    List<InterviewQuestion> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT q FROM InterviewQuestion q JOIN q.companyMappings cm WHERE cm.company.id = :companyId AND q.isPublished = true")
    Page<InterviewQuestion> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    long countByTopicId(Long topicId);

    @Query("SELECT COUNT(q) FROM InterviewQuestion q WHERE q.topic.category.id = :categoryId AND q.isPublished = true")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT q FROM InterviewQuestion q WHERE q.isPublished = true ORDER BY FUNCTION('RAND') LIMIT :limit")
    List<InterviewQuestion> findRandomQuestions(@Param("limit") int limit);
}
