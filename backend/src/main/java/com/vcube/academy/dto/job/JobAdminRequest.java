package com.vcube.academy.dto.job;

import com.vcube.academy.entity.EmploymentType;
import com.vcube.academy.entity.ExperienceLevel;
import com.vcube.academy.entity.JobSource;
import com.vcube.academy.entity.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class JobAdminRequest {
    @NotNull(message = "Company ID is required")
    private Long companyId;

    private Long categoryId;

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job slug is required")
    private String slug;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
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
    private Instant applicationDeadline;
    private Boolean isActive;
    private List<Long> requiredSkillIds;
    private List<Long> preferredSkillIds;
}
