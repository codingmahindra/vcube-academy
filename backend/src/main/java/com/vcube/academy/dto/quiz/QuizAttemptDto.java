package com.vcube.academy.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Represents an active quiz attempt returned to the student.
 */
@Data
@Builder
public class QuizAttemptDto {
    private Long attemptId;
    private String quizType;
    private Long topicId;
    private String topicTitle;
    private Long courseId;
    private String courseTitle;
    private String difficulty;
    private String status;
    private int totalQuestions;
    private int currentIndex;
    private Instant startedAt;
    private QuestionDto currentQuestion; // the first/current question to answer
}
