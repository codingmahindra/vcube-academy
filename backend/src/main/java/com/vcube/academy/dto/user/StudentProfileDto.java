package com.vcube.academy.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String college;
    private String degree;
    private String graduationYear;
    private Double cgpa;
    private String bio;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private List<String> technicalSkills;
    private Set<String> targetRoles;
    private Set<String> preferredLocations;
    private Boolean includeInResume;
    private Boolean includeInAtsAnalysis;
    private Boolean includeInCopilot;
}
