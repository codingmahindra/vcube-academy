package com.vcube.academy.dto.resume;

import com.vcube.academy.entity.AIProvider;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisDto {
    private Long id;
    private Long versionId;
    private Long jobId;
    private String targetJobTitle;
    private String targetCompanyName;
    private Integer overallAtsScore;
    private Integer keywordMatchScore;
    private Integer skillsMatchScore;
    private Integer experienceMatchScore;
    private Integer projectMatchScore;
    private Integer educationMatchScore;
    private Integer structureScore;
    private AIProvider aiProvider;
    private String summaryFeedback;
    private List<ResumeKeywordDto> matchedKeywords;
    private List<ResumeKeywordDto> missingKeywords;
    private List<ResumeKeywordDto> partialMatchedKeywords;
    private List<ResumeMissingSkillDto> criticalMissingSkills;
    private List<ResumeRecommendationDto> recommendations;
    private Instant createdAt;
}
