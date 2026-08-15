package com.vcube.academy.dto.career;

import com.vcube.academy.dto.interview.InterviewQuestionSummaryDto;
import com.vcube.academy.dto.dsa.DsaProblemSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyPrepHubDto {
    private Long companyId;
    private String companyName;
    private String slug;
    private String industry;
    private String description;
    private List<String> hiringRounds;
    private List<String> frequentlyTestedSkills;
    private int verifiedQuestionCount;
    private int reportedQuestionCount;
    private List<InterviewQuestionSummaryDto> companyQuestions;
    private List<PlacementPaperSummaryDto> placementPapers;
    private List<DsaProblemSummaryDto> recommendedDsaProblems;
}
