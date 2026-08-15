package com.vcube.academy.service;

import com.vcube.academy.dto.job.JobAdminRequest;
import com.vcube.academy.dto.job.JobDetailDto;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.BadRequestException;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobAdminTrainerService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobCategoryRepository categoryRepository;
    private final JobSkillRepository skillRepository;
    private final JobSkillMappingRepository skillMappingRepository;
    private final JobApplicationRepository applicationRepository;
    private final PlacementDriveRepository driveRepository;
    private final JobPortalService jobPortalService;

    @Transactional
    public JobDetailDto createJob(JobAdminRequest req) {
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + req.getCompanyId()));

        JobCategory category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId()).orElse(null);
        }

        Job job = Job.builder()
                .company(company)
                .category(category)
                .title(req.getTitle())
                .slug(req.getSlug())
                .description(req.getDescription())
                .location(req.getLocation())
                .employmentType(req.getEmploymentType() != null ? req.getEmploymentType() : EmploymentType.FULL_TIME)
                .experienceLevel(req.getExperienceLevel() != null ? req.getExperienceLevel() : ExperienceLevel.FRESHER)
                .workMode(req.getWorkMode() != null ? req.getWorkMode() : WorkMode.ONSITE)
                .salaryMin(req.getSalaryMin())
                .salaryMax(req.getSalaryMax())
                .salaryCurrency(req.getSalaryCurrency() != null ? req.getSalaryCurrency() : "INR")
                .salaryText(req.getSalaryText())
                .source(req.getSource() != null ? req.getSource() : JobSource.COMPANY_CAREER_PAGE)
                .sourceUrl(req.getSourceUrl())
                .qualification(req.getQualification())
                .responsibilities(req.getResponsibilities())
                .selectionProcess(req.getSelectionProcess())
                .postedDate(Instant.now())
                .applicationDeadline(req.getApplicationDeadline() != null ? req.getApplicationDeadline() : Instant.now().plusSeconds(30L * 24 * 3600))
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .build();

        job = jobRepository.save(job);

        // Required skills
        if (req.getRequiredSkillIds() != null) {
            for (Long sId : req.getRequiredSkillIds()) {
                JobSkill skill = skillRepository.findById(sId).orElse(null);
                if (skill != null) {
                    JobSkillMapping sm = JobSkillMapping.builder()
                            .job(job)
                            .skill(skill)
                            .isRequired(true)
                            .build();
                    skillMappingRepository.save(sm);
                }
            }
        }

        // Preferred skills
        if (req.getPreferredSkillIds() != null) {
            for (Long sId : req.getPreferredSkillIds()) {
                JobSkill skill = skillRepository.findById(sId).orElse(null);
                if (skill != null) {
                    JobSkillMapping sm = JobSkillMapping.builder()
                            .job(job)
                            .skill(skill)
                            .isRequired(false)
                            .build();
                    skillMappingRepository.save(sm);
                }
            }
        }

        return jobPortalService.getJobDetail(job.getId(), null);
    }

    @Transactional
    public JobDetailDto updateJob(Long id, JobAdminRequest req) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        if (req.getCompanyId() != null) {
            Company company = companyRepository.findById(req.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + req.getCompanyId()));
            job.setCompany(company);
        }

        if (req.getCategoryId() != null) {
            JobCategory category = categoryRepository.findById(req.getCategoryId()).orElse(null);
            job.setCategory(category);
        }

        if (req.getTitle() != null) job.setTitle(req.getTitle());
        if (req.getDescription() != null) job.setDescription(req.getDescription());
        if (req.getLocation() != null) job.setLocation(req.getLocation());
        if (req.getEmploymentType() != null) job.setEmploymentType(req.getEmploymentType());
        if (req.getExperienceLevel() != null) job.setExperienceLevel(req.getExperienceLevel());
        if (req.getWorkMode() != null) job.setWorkMode(req.getWorkMode());
        if (req.getSalaryMin() != null) job.setSalaryMin(req.getSalaryMin());
        if (req.getSalaryMax() != null) job.setSalaryMax(req.getSalaryMax());
        if (req.getSalaryText() != null) job.setSalaryText(req.getSalaryText());
        if (req.getSource() != null) job.setSource(req.getSource());
        if (req.getSourceUrl() != null) job.setSourceUrl(req.getSourceUrl());
        if (req.getQualification() != null) job.setQualification(req.getQualification());
        if (req.getResponsibilities() != null) job.setResponsibilities(req.getResponsibilities());
        if (req.getSelectionProcess() != null) job.setSelectionProcess(req.getSelectionProcess());
        if (req.getApplicationDeadline() != null) job.setApplicationDeadline(req.getApplicationDeadline());
        if (req.getIsActive() != null) job.setIsActive(req.getIsActive());

        job = jobRepository.save(job);

        // Update skill mappings if passed
        if (req.getRequiredSkillIds() != null || req.getPreferredSkillIds() != null) {
            skillMappingRepository.deleteByJobId(job.getId());

            if (req.getRequiredSkillIds() != null) {
                for (Long sId : req.getRequiredSkillIds()) {
                    JobSkill skill = skillRepository.findById(sId).orElse(null);
                    if (skill != null) {
                        skillMappingRepository.save(JobSkillMapping.builder().job(job).skill(skill).isRequired(true).build());
                    }
                }
            }
            if (req.getPreferredSkillIds() != null) {
                for (Long sId : req.getPreferredSkillIds()) {
                    JobSkill skill = skillRepository.findById(sId).orElse(null);
                    if (skill != null) {
                        skillMappingRepository.save(JobSkillMapping.builder().job(job).skill(skill).isRequired(false).build());
                    }
                }
            }
        }

        return jobPortalService.getJobDetail(job.getId(), null);
    }

    @Transactional
    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
        jobRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getJobAnalytics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActiveJobs", jobRepository.countByIsActiveTrue());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("totalPlacementDrives", driveRepository.count());
        stats.put("totalCompanies", companyRepository.count());
        return stats;
    }
}
