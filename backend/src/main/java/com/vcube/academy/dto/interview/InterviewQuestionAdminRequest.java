package com.vcube.academy.dto.interview;

import com.vcube.academy.entity.InterviewDifficulty;
import com.vcube.academy.entity.InterviewQuestionType;
import com.vcube.academy.entity.InterviewRoundType;
import com.vcube.academy.entity.QuestionSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionAdminRequest {
    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotBlank(message = "Question text is required")
    private String questionText;

    private InterviewQuestionType questionType;
    private InterviewDifficulty difficulty;
    private InterviewRoundType interviewRound;
    private QuestionSource questionSource;
    private String sourceReference;

    @NotBlank(message = "Expected answer is required")
    private String expectedAnswer;

    @NotBlank(message = "Explanation is required")
    private String explanation;

    private String interviewPoints;
    private String commonMistakes;
    private String followUpQuestions;
    private String realWorldExample;
    private String evaluationKeywords;
    private Boolean isPublished;
    private List<Long> companyIds;
}
