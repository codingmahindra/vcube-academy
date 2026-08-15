package com.vcube.academy.dto.interview;

import com.vcube.academy.entity.InterviewDifficulty;
import com.vcube.academy.entity.InterviewQuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInterviewQuestionDto {
    private Long id;
    private Long questionId;
    private Integer questionOrder;
    private String questionText;
    private InterviewQuestionType questionType;
    private InterviewDifficulty difficulty;
    private String topicName;
    private String categoryName;
    private String userAnswer;
    private Integer timeTakenSeconds;
    private Double score;
    private String feedback;
    private List<String> missingPoints;
    private String improvedAnswer;
    private String expectedAnswer;
    private String explanation;
}
