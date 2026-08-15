package com.vcube.academy.repository;

import com.vcube.academy.entity.CareerCopilotMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerCopilotMessageRepository extends JpaRepository<CareerCopilotMessage, Long> {
    List<CareerCopilotMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
