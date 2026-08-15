package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementPaperQuestionDto {
    private Long id;
    private String sectionName;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer marks;
    private BigDecimal negativeMarks;
    private Integer displayOrder;
    // Note: correctOption and explanation are hidden during live attempt!
    private String correctOption;
    private String explanation;
    private String selectedOption;
}
