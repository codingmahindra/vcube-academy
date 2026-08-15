package com.vcube.academy.dto.job;

import com.vcube.academy.entity.EmploymentType;
import com.vcube.academy.entity.ExperienceLevel;
import com.vcube.academy.entity.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobFilterRequest {
    private String keyword;
    private Long companyId;
    private Long categoryId;
    private String location;
    private EmploymentType employmentType;
    private ExperienceLevel experienceLevel;
    private WorkMode workMode;
    private Long skillId;
    private BigDecimal minSalary;
    private String sortBy; // newest, deadline, salary
    private int page;
    private int size;
}
