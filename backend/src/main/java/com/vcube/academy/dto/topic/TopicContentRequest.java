package com.vcube.academy.dto.topic;

import lombok.Data;

@Data
public class TopicContentRequest {
    private String explanation;
    private String simpleExplanation;
    private String realWorldExample;
    private String syntaxExample;
    private String codeExample;
    private String codeLanguage = "java";
    private String interviewPoints;
    private String commonMistakes;
    private String practiceQuestions;
}
