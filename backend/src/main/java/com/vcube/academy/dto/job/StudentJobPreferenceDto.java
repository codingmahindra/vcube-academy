package com.vcube.academy.dto.job;

import com.vcube.academy.entity.EmploymentType;
import com.vcube.academy.entity.ExperienceLevel;
import com.vcube.academy.entity.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentJobPreferenceDto {
    private List<String> preferredRoles;
    private List<String> preferredLocations;
    private List<String> preferredTechnologies;
    private ExperienceLevel experienceLevel;
    private WorkMode workMode;
    private EmploymentType employmentType;
    private BigDecimal expectedSalaryMin;
}
