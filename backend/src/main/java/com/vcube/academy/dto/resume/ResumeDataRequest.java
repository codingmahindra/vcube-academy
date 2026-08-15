package com.vcube.academy.dto.resume;

import com.vcube.academy.entity.ResumeTemplate;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDataRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String email;
    private String phone;
    private String location;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String professionalSummary;
    private String versionTitle;
    private Long jobId;
    private String targetRole;
    private String targetCompany;
    private ResumeTemplate template;
    private String rawResumeText;
    private List<String> technicalSkills;
    private List<ResumeExperienceDto> experiences;
    private List<ResumeEducationDto> educations;
    private List<ResumeProjectDto> projects;
    private List<ResumeCertificationDto> certifications;
    private List<String> achievements;
    private Boolean isPrimary;
}
