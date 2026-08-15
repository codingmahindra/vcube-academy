package com.vcube.academy.dto.resume;

import com.vcube.academy.entity.ResumeTemplate;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeVersionDetailDto {
    private Long id;
    private Long profileId;
    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String professionalSummary;
    private Long jobId;
    private String targetJobTitle;
    private String targetCompany;
    private String versionTitle;
    private ResumeTemplate template;
    private String rawResumeText;
    private Integer latestAtsScore;
    private Boolean isPrimary;
    private List<String> technicalSkills;
    private List<ResumeExperienceDto> experiences;
    private List<ResumeEducationDto> educations;
    private List<ResumeProjectDto> projects;
    private List<ResumeCertificationDto> certifications;
    private List<String> achievements;
    private ResumeAnalysisDto latestAnalysis;
    private List<Map<String, Object>> scoreHistories;
    private Instant createdAt;
    private Instant updatedAt;
}
