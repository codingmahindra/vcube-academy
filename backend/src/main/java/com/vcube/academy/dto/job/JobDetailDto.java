package com.vcube.academy.dto.job;

import com.vcube.academy.entity.EmploymentType;
import com.vcube.academy.entity.ExperienceLevel;
import com.vcube.academy.entity.JobSource;
import com.vcube.academy.entity.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailDto {
    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
    private String companyDescription;
    private String companyTier;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String slug;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private ExperienceLevel experienceLevel;
    private WorkMode workMode;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private String salaryText;
    private JobSource source;
    private String sourceUrl;
    private String qualification;
    private String responsibilities;
    private String selectionProcess;
    private Instant postedDate;
    private Instant applicationDeadline;
    private boolean isSaved;
    private boolean hasApplied;
    private String applicationStatus;
    private List<JobSkillDto> skills;
    private JobMatchResultDto matchResult;
    private JobPreparationRecommendationDto preparationRoadmap;
}
