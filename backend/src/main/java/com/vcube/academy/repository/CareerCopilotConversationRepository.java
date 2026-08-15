package com.vcube.academy.repository;

import com.vcube.academy.entity.CareerCopilotConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerCopilotConversationRepository extends JpaRepository<CareerCopilotConversation, Long> {
    List<CareerCopilotConversation> findByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<CareerCopilotConversation> findByIdAndUserId(Long id, Long userId);
}
