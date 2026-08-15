package com.vcube.academy.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartQuizRequest {

    @NotBlank(message = "Quiz type is required")
    private String quizType; // TOPIC_QUIZ or COURSE_QUIZ

    private Long topicId;   // required when quizType = TOPIC_QUIZ
    private Long courseId;  // required when quizType = COURSE_QUIZ

    private String difficulty; // optional filter: EASY, MEDIUM, HARD
}
