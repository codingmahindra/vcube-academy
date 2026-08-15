package com.vcube.academy.dto.topic;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TopicContentDto {
    private Long id;
    private Long topicId;
    private String explanation;
    private String simpleExplanation;
    private String realWorldExample;
    private String syntaxExample;
    private String codeExample;
    private String codeLanguage;
    private String interviewPoints;
    private String commonMistakes;
    private String practiceQuestions;
    private Instant createdAt;
    private Instant updatedAt;
}
