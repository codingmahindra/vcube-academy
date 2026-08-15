package com.vcube.academy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.resume.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ResumeProfileRepository profileRepository;
    private final ResumeVersionRepository versionRepository;
    private final ResumeExperienceRepository experienceRepository;
    private final ResumeEducationRepository educationRepository;
    private final ResumeProjectRepository projectRepository;
    private final ResumeCertificationRepository certificationRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final ResumeKeywordRepository keywordRepository;
    private final ResumeMissingSkillRepository missingSkillRepository;
    private final ResumeRecommendationRepository recommendationRepository;
    private final ResumeAnalysisHistoryRepository historyRepository;
    private final ResumeAIService resumeAIService;
    private final ResumePdfGeneratorService pdfGeneratorService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ResumeProfileDto getProfile(Long userId) {
        ResumeProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfileForUser(userId));
        return mapToProfileDto(profile);
    }

    @Transactional(readOnly = true)
    public List<ResumeVersionSummaryDto> listVersions(Long userId) {
        return versionRepository.findByProfileUserIdOrderByUpdatedAtDesc(userId)
                .stream().map(this::mapToVersionSummary).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResumeVersionDetailDto getVersionDetail(Long versionId, Long userId) {
        ResumeVersion version = findVersionWithSecurity(versionId, userId);
        return mapToVersionDetail(version);
    }

    @Transactional
    public ResumeVersionDetailDto saveResume(ResumeDataRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        ResumeProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ResumeProfile np = ResumeProfile.builder()
                            .user(user)
                            .fullName(request.getFullName())
                            .email(request.getEmail())
                            .phone(request.getPhone())
                            .location(request.getLocation())
                            .linkedinUrl(request.getLinkedinUrl())
                            .githubUrl(request.getGithubUrl())
                            .portfolioUrl(request.getPortfolioUrl())
                            .professionalSummary(request.getProfessionalSummary())
                            .build();
                    return profileRepository.save(np);
                });

        // Update profile header
        profile.setFullName(request.getFullName());
        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setProfessionalSummary(request.getProfessionalSummary());
        profileRepository.save(profile);

        Job targetJob = null;
        if (request.getJobId() != null) {
            targetJob = jobRepository.findById(request.getJobId()).orElse(null);
        }

        ResumeVersion version = ResumeVersion.builder()
                .profile(profile)
                .targetJob(targetJob)
                .versionTitle(request.getVersionTitle() != null ? request.getVersionTitle() : "Resume — " + (targetJob != null ? targetJob.getTitle() : "General"))
                .targetRole(request.getTargetRole() != null ? request.getTargetRole() : (targetJob != null ? targetJob.getTitle() : "Java Full Stack Developer"))
                .targetCompany(request.getTargetCompany() != null ? request.getTargetCompany() : (targetJob != null ? targetJob.getCompany().getName() : "Enterprise"))
                .template(request.getTemplate() != null ? request.getTemplate() : ResumeTemplate.ATS_CLASSIC)
                .rawResumeText(request.getRawResumeText())
                .isPrimary(Boolean.TRUE.equals(request.getIsPrimary()))
                .build();

        version = versionRepository.save(version);

        saveChildCollections(version, request);

        return mapToVersionDetail(versionRepository.findById(version.getId()).orElse(version));
    }

    @Transactional
    public ResumeVersionDetailDto updateResume(Long versionId, ResumeDataRequest request, Long userId) {
        ResumeVersion version = findVersionWithSecurity(versionId, userId);

        if (request.getVersionTitle() != null) version.setVersionTitle(request.getVersionTitle());
        if (request.getTargetRole() != null) version.setTargetRole(request.getTargetRole());
        if (request.getTargetCompany() != null) version.setTargetCompany(request.getTargetCompany());
        if (request.getTemplate() != null) version.setTemplate(request.getTemplate());
        if (request.getRawResumeText() != null) version.setRawResumeText(request.getRawResumeText());
        if (request.getIsPrimary() != null) version.setIsPrimary(request.getIsPrimary());

        if (request.getJobId() != null) {
            Job targetJob = jobRepository.findById(request.getJobId()).orElse(null);
            version.setTargetJob(targetJob);
        }

        ResumeProfile profile = version.getProfile();
        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getProfessionalSummary() != null) profile.setProfessionalSummary(request.getProfessionalSummary());
        profileRepository.save(profile);

        // Clear and repopulate collections
        experienceRepository.deleteByVersionId(versionId);
        educationRepository.deleteByVersionId(versionId);
        projectRepository.deleteByVersionId(versionId);
        certificationRepository.deleteByVersionId(versionId);

        saveChildCollections(version, request);

        return mapToVersionDetail(versionRepository.findById(versionId).orElse(version));
    }

    @Transactional
    public ResumeAnalysisDto analyzeResume(ResumeAnalyzeRequest request, Long userId) {
        ResumeVersion version;
        if (request.getVersionId() != null) {
            version = findVersionWithSecurity(request.getVersionId(), userId);
        } else {
            List<ResumeVersion> userVersions = versionRepository.findByProfileUserIdOrderByUpdatedAtDesc(userId);
            if (!userVersions.isEmpty()) {
                version = userVersions.get(0);
            } else {
                ResumeDataRequest dr = ResumeDataRequest.builder()
                        .fullName("Candidate")
                        .email("candidate@vcube.com")
                        .versionTitle("Uploaded Resume Analysis")
                        .rawResumeText(request.getResumeText())
                        .build();
                ResumeVersionDetailDto vd = saveResume(dr, userId);
                version = versionRepository.findById(vd.getId()).orElseThrow();
            }
        }

        if (request.getJobId() != null) {
            Job j = jobRepository.findById(request.getJobId()).orElse(null);
            if (j != null) {
                version.setTargetJob(j);
                if (request.getTargetRole() == null) request.setTargetRole(j.getTitle());
                if (request.getTargetCompany() == null) request.setTargetCompany(j.getCompany().getName());
                if (request.getJobDescriptionText() == null) request.setJobDescriptionText(j.getDescription() + " " + j.getQualification());
            }
        }

        int scoreBefore = version.getLatestAtsScore() != null ? version.getLatestAtsScore() : 0;

        ResumeAnalysisDto analysisDto = resumeAIService.analyzeResume(request, version);

        // Persist analysis
        ResumeAnalysis analysis = ResumeAnalysis.builder()
                .version(version)
                .targetJob(version.getTargetJob())
                .targetJobTitle(analysisDto.getTargetJobTitle())
                .targetCompanyName(analysisDto.getTargetCompanyName())
                .jobDescriptionText(request.getJobDescriptionText())
                .overallAtsScore(analysisDto.getOverallAtsScore())
                .keywordMatchScore(analysisDto.getKeywordMatchScore())
                .skillsMatchScore(analysisDto.getSkillsMatchScore())
                .experienceMatchScore(analysisDto.getExperienceMatchScore())
                .projectMatchScore(analysisDto.getProjectMatchScore())
                .educationMatchScore(analysisDto.getEducationMatchScore())
                .structureScore(analysisDto.getStructureScore())
                .aiProvider(analysisDto.getAiProvider() != null ? analysisDto.getAiProvider() : AIProvider.RULE_BASED)
                .summaryFeedback(analysisDto.getSummaryFeedback())
                .build();

        analysis = analysisRepository.save(analysis);
        analysisDto.setId(analysis.getId());

        // Save keywords
        if (analysisDto.getMatchedKeywords() != null) {
            for (ResumeKeywordDto kw : analysisDto.getMatchedKeywords()) {
                keywordRepository.save(ResumeKeyword.builder()
                        .analysis(analysis)
                        .keywordName(kw.getKeywordName())
                        .category(kw.getCategory())
                        .matchStatus(kw.getMatchStatus())
                        .importance(kw.getImportance())
                        .occurrenceCount(kw.getOccurrenceCount())
                        .build());
            }
        }
        if (analysisDto.getMissingKeywords() != null) {
            for (ResumeKeywordDto kw : analysisDto.getMissingKeywords()) {
                keywordRepository.save(ResumeKeyword.builder()
                        .analysis(analysis)
                        .keywordName(kw.getKeywordName())
                        .category(kw.getCategory())
                        .matchStatus(kw.getMatchStatus())
                        .importance(kw.getImportance())
                        .occurrenceCount(0)
                        .build());
            }
        }

        // Save critical missing skills
        if (analysisDto.getCriticalMissingSkills() != null) {
            for (ResumeMissingSkillDto ms : analysisDto.getCriticalMissingSkills()) {
                missingSkillRepository.save(ResumeMissingSkill.builder()
                        .analysis(analysis)
                        .skillName(ms.getSkillName())
                        .category(ms.getCategory())
                        .importance(ms.getImportance())
                        .whyItMatters(ms.getWhyItMatters())
                        .build());
            }
        }

        // Save recommendations
        if (analysisDto.getRecommendations() != null) {
            for (ResumeRecommendationDto rec : analysisDto.getRecommendations()) {
                recommendationRepository.save(ResumeRecommendation.builder()
                        .analysis(analysis)
                        .sectionType(rec.getSectionType())
                        .severity(rec.getSeverity())
                        .title(rec.getTitle())
                        .message(rec.getMessage())
                        .actionableFix(rec.getActionableFix())
                        .build());
            }
        }

        // Record history & update version latest score
        version.setLatestAtsScore(analysisDto.getOverallAtsScore());
        versionRepository.save(version);

        historyRepository.save(ResumeAnalysisHistory.builder()
                .version(version)
                .scoreBefore(scoreBefore)
                .scoreAfter(analysisDto.getOverallAtsScore())
                .changeSummary("Evaluated against " + (analysisDto.getTargetJobTitle() != null ? analysisDto.getTargetJobTitle() : "General"))
                .build());

        return analysisDto;
    }

    @Transactional(readOnly = true)
    public ResumeOptimizationDto optimizeResume(Long versionId, Long userId) {
        ResumeVersion version = findVersionWithSecurity(versionId, userId);
        String jd = version.getTargetJob() != null ? version.getTargetJob().getDescription() : "";
        return resumeAIService.generateOptimizationSuggestions(version, jd);
    }

    @Transactional(readOnly = true)
    public byte[] generatePdf(Long versionId, Long userId) {
        ResumeVersionDetailDto detail = getVersionDetail(versionId, userId);
        return pdfGeneratorService.generatePdf(detail);
    }

    @Transactional
    public void deleteVersion(Long versionId, Long userId) {
        ResumeVersion version = findVersionWithSecurity(versionId, userId);
        versionRepository.delete(version);
    }

    private void saveChildCollections(ResumeVersion version, ResumeDataRequest request) {
        if (request.getExperiences() != null) {
            for (int i = 0; i < request.getExperiences().size(); i++) {
                ResumeExperienceDto ed = request.getExperiences().get(i);
                String bpJson = null;
                if (ed.getBulletPoints() != null) {
                    try { bpJson = objectMapper.writeValueAsString(ed.getBulletPoints()); } catch (Exception ignored) {}
                }
                experienceRepository.save(ResumeExperience.builder()
                        .version(version)
                        .companyName(ed.getCompanyName())
                        .roleTitle(ed.getRoleTitle())
                        .location(ed.getLocation())
                        .startDate(ed.getStartDate())
                        .endDate(ed.getEndDate())
                        .isCurrent(Boolean.TRUE.equals(ed.getIsCurrent()))
                        .description(ed.getDescription())
                        .bulletPoints(bpJson)
                        .displayOrder(i + 1)
                        .build());
            }
        }

        if (request.getEducations() != null) {
            for (int i = 0; i < request.getEducations().size(); i++) {
                ResumeEducationDto ed = request.getEducations().get(i);
                educationRepository.save(ResumeEducation.builder()
                        .version(version)
                        .institution(ed.getInstitution())
                        .degree(ed.getDegree())
                        .fieldOfStudy(ed.getFieldOfStudy())
                        .startYear(ed.getStartYear())
                        .endYear(ed.getEndYear())
                        .scoreOrCgpa(ed.getScoreOrCgpa())
                        .displayOrder(i + 1)
                        .build());
            }
        }

        if (request.getProjects() != null) {
            for (int i = 0; i < request.getProjects().size(); i++) {
                ResumeProjectDto pd = request.getProjects().get(i);
                String bpJson = null;
                if (pd.getBulletPoints() != null) {
                    try { bpJson = objectMapper.writeValueAsString(pd.getBulletPoints()); } catch (Exception ignored) {}
                }
                projectRepository.save(ResumeProject.builder()
                        .version(version)
                        .title(pd.getTitle())
                        .techStack(pd.getTechStack())
                        .liveUrl(pd.getLiveUrl())
                        .githubUrl(pd.getGithubUrl())
                        .description(pd.getDescription())
                        .bulletPoints(bpJson)
                        .displayOrder(i + 1)
                        .build());
            }
        }

        if (request.getCertifications() != null) {
            for (int i = 0; i < request.getCertifications().size(); i++) {
                ResumeCertificationDto cd = request.getCertifications().get(i);
                certificationRepository.save(ResumeCertification.builder()
                        .version(version)
                        .name(cd.getName())
                        .issuingOrganization(cd.getIssuingOrganization())
                        .issueDate(cd.getIssueDate())
                        .credentialUrl(cd.getCredentialUrl())
                        .displayOrder(i + 1)
                        .build());
            }
        }
    }

    private ResumeVersion findVersionWithSecurity(Long versionId, Long userId) {
        ResumeVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume version not found: " + versionId));
        if (!version.getProfile().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied to resume version: " + versionId);
        }
        return version;
    }

    private ResumeProfile createDefaultProfileForUser(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        ResumeProfile rp = ResumeProfile.builder()
                .user(u)
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone("+91 90000 00000")
                .location("Hyderabad, India")
                .professionalSummary("Enthusiastic Java Full Stack Developer with training in Core Java, Spring Boot, PostgreSQL, and modern frontend tools.")
                .build();
        return profileRepository.save(rp);
    }

    private ResumeProfileDto mapToProfileDto(ResumeProfile p) {
        List<ResumeVersionSummaryDto> versions = p.getVersions().stream()
                .map(this::mapToVersionSummary)
                .collect(Collectors.toList());

        return ResumeProfileDto.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .location(p.getLocation())
                .linkedinUrl(p.getLinkedinUrl())
                .githubUrl(p.getGithubUrl())
                .portfolioUrl(p.getPortfolioUrl())
                .professionalSummary(p.getProfessionalSummary())
                .versions(versions)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ResumeVersionSummaryDto mapToVersionSummary(ResumeVersion v) {
        return ResumeVersionSummaryDto.builder()
                .id(v.getId())
                .profileId(v.getProfile().getId())
                .jobId(v.getTargetJob() != null ? v.getTargetJob().getId() : null)
                .versionTitle(v.getVersionTitle())
                .targetRole(v.getTargetRole())
                .targetCompany(v.getTargetCompany())
                .template(v.getTemplate())
                .latestAtsScore(v.getLatestAtsScore())
                .isPrimary(v.getIsPrimary())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    private ResumeVersionDetailDto mapToVersionDetail(ResumeVersion v) {
        ResumeProfile p = v.getProfile();

        List<ResumeExperienceDto> exps = v.getExperiences().stream().map(e -> {
            List<String> bullets = Collections.emptyList();
            if (e.getBulletPoints() != null) {
                try { bullets = objectMapper.readValue(e.getBulletPoints(), new TypeReference<List<String>>() {}); } catch (Exception ignored) {}
            }
            return ResumeExperienceDto.builder()
                    .id(e.getId())
                    .companyName(e.getCompanyName())
                    .roleTitle(e.getRoleTitle())
                    .location(e.getLocation())
                    .startDate(e.getStartDate())
                    .endDate(e.getEndDate())
                    .isCurrent(e.getIsCurrent())
                    .description(e.getDescription())
                    .bulletPoints(bullets)
                    .displayOrder(e.getDisplayOrder())
                    .build();
        }).collect(Collectors.toList());

        List<ResumeEducationDto> edus = v.getEducations().stream().map(ed -> ResumeEducationDto.builder()
                .id(ed.getId())
                .institution(ed.getInstitution())
                .degree(ed.getDegree())
                .fieldOfStudy(ed.getFieldOfStudy())
                .startYear(ed.getStartYear())
                .endYear(ed.getEndYear())
                .scoreOrCgpa(ed.getScoreOrCgpa())
                .displayOrder(ed.getDisplayOrder())
                .build()).collect(Collectors.toList());

        List<ResumeProjectDto> projs = v.getProjects().stream().map(pr -> {
            List<String> bullets = Collections.emptyList();
            if (pr.getBulletPoints() != null) {
                try { bullets = objectMapper.readValue(pr.getBulletPoints(), new TypeReference<List<String>>() {}); } catch (Exception ignored) {}
            }
            return ResumeProjectDto.builder()
                    .id(pr.getId())
                    .title(pr.getTitle())
                    .techStack(pr.getTechStack())
                    .liveUrl(pr.getLiveUrl())
                    .githubUrl(pr.getGithubUrl())
                    .description(pr.getDescription())
                    .bulletPoints(bullets)
                    .displayOrder(pr.getDisplayOrder())
                    .build();
        }).collect(Collectors.toList());

        List<ResumeCertificationDto> certs = v.getCertifications().stream().map(c -> ResumeCertificationDto.builder()
                .id(c.getId())
                .name(c.getName())
                .issuingOrganization(c.getIssuingOrganization())
                .issueDate(c.getIssueDate())
                .credentialUrl(c.getCredentialUrl())
                .displayOrder(c.getDisplayOrder())
                .build()).collect(Collectors.toList());

        List<Map<String, Object>> histories = v.getScoreHistories().stream().map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("scoreBefore", h.getScoreBefore());
            map.put("scoreAfter", h.getScoreAfter());
            map.put("changeSummary", h.getChangeSummary());
            map.put("analyzedAt", h.getAnalyzedAt());
            return map;
        }).collect(Collectors.toList());

        List<String> skills = List.of("Java 17", "Spring Boot", "PostgreSQL", "REST APIs", "Microservices", "Docker", "Git", "React");

        return ResumeVersionDetailDto.builder()
                .id(v.getId())
                .profileId(p.getId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .location(p.getLocation())
                .linkedinUrl(p.getLinkedinUrl())
                .githubUrl(p.getGithubUrl())
                .portfolioUrl(p.getPortfolioUrl())
                .professionalSummary(p.getProfessionalSummary())
                .jobId(v.getTargetJob() != null ? v.getTargetJob().getId() : null)
                .targetJobTitle(v.getTargetRole())
                .targetCompany(v.getTargetCompany())
                .versionTitle(v.getVersionTitle())
                .template(v.getTemplate())
                .rawResumeText(v.getRawResumeText())
                .latestAtsScore(v.getLatestAtsScore())
                .isPrimary(v.getIsPrimary())
                .technicalSkills(skills)
                .experiences(exps)
                .educations(edus)
                .projects(projs)
                .certifications(certs)
                .scoreHistories(histories)
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}
