package com.vcube.academy.dto.interview;

import com.vcube.academy.entity.InterviewDifficulty;
import com.vcube.academy.entity.InterviewQuestionType;
import com.vcube.academy.entity.InterviewRoundType;
import com.vcube.academy.entity.QuestionSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionSummaryDto {
    private Long id;
    private Long topicId;
    private String topicName;
    private Long categoryId;
    private String categoryName;
    private String questionText;
    private InterviewQuestionType questionType;
    private InterviewDifficulty difficulty;
    private InterviewRoundType interviewRound;
    private QuestionSource questionSource;
    private String sourceReference;
    private boolean isCompleted;
    private Double lastScore;
    private List<String> companies;
}
