package com.vcube.academy.service;

import com.vcube.academy.dto.job.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.vcube.academy.dto.interview.CompanySummaryDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPortalService {

    private final JobRepository jobRepository;
    private final JobCategoryRepository categoryRepository;
    private final JobSkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final SavedJobRepository savedJobRepository;
    private final JobApplicationRepository applicationRepository;
    private final JobMatchingService matchingService;
    private final JobPreparationService preparationService;

    @Transactional(readOnly = true)
    public Page<JobSummaryDto> searchJobs(JobFilterRequest filter, Long userId) {
        int page = Math.max(0, filter.getPage());
        int size = filter.getSize() > 0 ? filter.getSize() : 12;

        Sort sort = Sort.by(Sort.Direction.DESC, "postedDate");
        if ("deadline".equalsIgnoreCase(filter.getSortBy())) {
            sort = Sort.by(Sort.Direction.ASC, "applicationDeadline");
        } else if ("salary".equalsIgnoreCase(filter.getSortBy())) {
            sort = Sort.by(Sort.Direction.DESC, "salaryMax");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Job> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates.getExpressions().add(cb.isTrue(root.get("isActive")));

            if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
                String like = "%" + filter.getKeyword().toLowerCase().trim() + "%";
                predicates.getExpressions().add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("company").get("name")), like)
                ));
            }
            if (filter.getCompanyId() != null) {
                predicates.getExpressions().add(cb.equal(root.get("company").get("id"), filter.getCompanyId()));
            }
            if (filter.getCategoryId() != null) {
                predicates.getExpressions().add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }
            if (filter.getLocation() != null && !filter.getLocation().trim().isEmpty()) {
                predicates.getExpressions().add(cb.like(cb.lower(root.get("location")), "%" + filter.getLocation().toLowerCase().trim() + "%"));
            }
            if (filter.getEmploymentType() != null) {
                predicates.getExpressions().add(cb.equal(root.get("employmentType"), filter.getEmploymentType()));
            }
            if (filter.getExperienceLevel() != null) {
                predicates.getExpressions().add(cb.equal(root.get("experienceLevel"), filter.getExperienceLevel()));
            }
            if (filter.getWorkMode() != null) {
                predicates.getExpressions().add(cb.equal(root.get("workMode"), filter.getWorkMode()));
            }
            if (filter.getMinSalary() != null) {
                predicates.getExpressions().add(cb.greaterThanOrEqualTo(root.get("salaryMax"), filter.getMinSalary()));
            }

            return predicates;
        };

        Page<Job> jobsPage = jobRepository.findAll(spec, pageable);
        return jobsPage.map(job -> mapToSummaryDto(job, userId));
    }

    @Transactional(readOnly = true)
    public JobDetailDto getJobDetail(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .filter(Job::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        boolean isSaved = false;
        boolean hasApplied = false;
        String appStatus = null;

        if (userId != null) {
            isSaved = savedJobRepository.existsByUserIdAndJobId(userId, jobId);
            var appOpt = applicationRepository.findByUserIdAndJobId(userId, jobId);
            if (appOpt.isPresent()) {
                hasApplied = true;
                appStatus = appOpt.get().getStatus().name();
            }
        }

        List<JobSkillDto> skills = job.getSkillMappings().stream()
                .map(sm -> JobSkillDto.builder()
                        .id(sm.getSkill().getId())
                        .name(sm.getSkill().getName())
                        .slug(sm.getSkill().getSlug())
                        .category(sm.getSkill().getCategory())
                        .isRequired(sm.getIsRequired())
                        .build())
                .collect(Collectors.toList());

        JobMatchResultDto match = matchingService.calculateMatch(job, userId);
        JobPreparationRecommendationDto prep = preparationService.generateRoadmap(job);

        return JobDetailDto.builder()
                .id(job.getId())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .companyLogoUrl(job.getCompany().getLogoUrl())
                .companyDescription(job.getCompany().getDescription())
                .companyTier(job.getCompany().getTier())
                .categoryId(job.getCategory() != null ? job.getCategory().getId() : null)
                .categoryName(job.getCategory() != null ? job.getCategory().getName() : null)
                .title(job.getTitle())
                .slug(job.getSlug())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .experienceLevel(job.getExperienceLevel())
                .workMode(job.getWorkMode())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .salaryCurrency(job.getSalaryCurrency())
                .salaryText(job.getSalaryText())
                .source(job.getSource())
                .sourceUrl(job.getSourceUrl())
                .qualification(job.getQualification())
                .responsibilities(job.getResponsibilities())
                .selectionProcess(job.getSelectionProcess())
                .postedDate(job.getPostedDate())
                .applicationDeadline(job.getApplicationDeadline())
                .isSaved(isSaved)
                .hasApplied(hasApplied)
                .applicationStatus(appStatus)
                .skills(skills)
                .matchResult(match)
                .preparationRoadmap(prep)
                .build();
    }

    @Transactional(readOnly = true)
    public List<JobCategoryDto> getCategories() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(c -> JobCategoryDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .slug(c.getSlug())
                        .description(c.getDescription())
                        .icon(c.getIcon())
                        .totalJobs(c.getJobs().stream().filter(Job::getIsActive).count())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getLocations() {
        return jobRepository.findDistinctLocations();
    }

    @Transactional(readOnly = true)
    public List<JobSkillDto> getSkills() {
        return skillRepository.findAllByOrderByNameAsc().stream()
                .map(s -> JobSkillDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .slug(s.getSlug())
                        .category(s.getCategory())
                        .isRequired(true)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompanySummaryDto> getCompanies() {
        return companyRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(comp -> CompanySummaryDto.builder()
                        .id(comp.getId())
                        .name(comp.getName())
                        .slug(comp.getSlug())
                        .logoUrl(comp.getLogoUrl())
                        .description(comp.getDescription())
                        .industry(comp.getIndustry())
                        .tier(comp.getTier())
                        .totalQuestions(comp.getCompanyQuestions() != null ? comp.getCompanyQuestions().size() : 0)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStudentJobRecommendations(Long userId) {
        List<Job> allJobs = jobRepository.findAll().stream()
                .filter(Job::getIsActive)
                .collect(Collectors.toList());

        List<Map<String, Object>> recommendations = new ArrayList<>();
        for (Job job : allJobs) {
            JobMatchResultDto match = matchingService.calculateMatch(job, userId);
            if (match.getMatchPercentage() >= 50) {
                Map<String, Object> item = new HashMap<>();
                item.put("job", mapToSummaryDto(job, userId));
                item.put("matchScore", match.getMatchPercentage());
                item.put("matchedSkills", match.getMatchedSkills());
                item.put("missingSkills", match.getMissingSkills());
                item.put("summary", match.getSummary());
                recommendations.add(item);
            }
        }

        recommendations.sort((a, b) -> Integer.compare((int) b.get("matchScore"), (int) a.get("matchScore")));

        Map<String, Object> result = new HashMap<>();
        result.put("recommendedJobs", recommendations);
        result.put("recommendedSkillRevision", List.of("Java 17 Concurrency", "Spring Boot Cloud Microservices", "PostgreSQL Query Optimization", "Top 50 LeetCode Patterns"));
        return result;
    }

    public JobSummaryDto mapToSummaryDto(Job job, Long userId) {
        boolean isSaved = false;
        boolean hasApplied = false;
        String appStatus = null;

        if (userId != null) {
            isSaved = savedJobRepository.existsByUserIdAndJobId(userId, job.getId());
            var appOpt = applicationRepository.findByUserIdAndJobId(userId, job.getId());
            if (appOpt.isPresent()) {
                hasApplied = true;
                appStatus = appOpt.get().getStatus().name();
            }
        }

        List<JobSkillDto> skills = job.getSkillMappings().stream()
                .map(sm -> JobSkillDto.builder()
                        .id(sm.getSkill().getId())
                        .name(sm.getSkill().getName())
                        .slug(sm.getSkill().getSlug())
                        .category(sm.getSkill().getCategory())
                        .isRequired(sm.getIsRequired())
                        .build())
                .collect(Collectors.toList());

        return JobSummaryDto.builder()
                .id(job.getId())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .companyLogoUrl(job.getCompany().getLogoUrl())
                .companyTier(job.getCompany().getTier())
                .categoryId(job.getCategory() != null ? job.getCategory().getId() : null)
                .categoryName(job.getCategory() != null ? job.getCategory().getName() : null)
                .title(job.getTitle())
                .slug(job.getSlug())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .experienceLevel(job.getExperienceLevel())
                .workMode(job.getWorkMode())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .salaryText(job.getSalaryText())
                .source(job.getSource())
                .sourceUrl(job.getSourceUrl())
                .postedDate(job.getPostedDate())
                .applicationDeadline(job.getApplicationDeadline())
                .isSaved(isSaved)
                .hasApplied(hasApplied)
                .applicationStatus(appStatus)
                .skills(skills)
                .build();
    }
}
