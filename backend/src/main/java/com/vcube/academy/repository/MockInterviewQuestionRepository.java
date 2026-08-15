package com.vcube.academy.repository;

import com.vcube.academy.entity.MockInterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockInterviewQuestionRepository extends JpaRepository<MockInterviewQuestion, Long> {
    List<MockInterviewQuestion> findByMockInterviewIdOrderByQuestionOrderAsc(Long mockInterviewId);
    Optional<MockInterviewQuestion> findByMockInterviewIdAndQuestionOrder(Long mockInterviewId, Integer questionOrder);
}
