package com.vcube.academy.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAdminDto {
    private Long id;
    private Long topicId;
    private String topicTitle;
    private Long courseId;
    private String courseTitle;
    private String questionText;
    private String difficulty;
    private String explanation;
    private String interviewPoint;
    private String companyTags;
    private Boolean isActive;
    private List<QuestionOptionAdminDto> options;
}
