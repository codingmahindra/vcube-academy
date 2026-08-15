package com.vcube.academy.repository;

import com.vcube.academy.entity.TopicContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicContentRepository extends JpaRepository<TopicContent, Long> {
    Optional<TopicContent> findByTopicId(Long topicId);
}
