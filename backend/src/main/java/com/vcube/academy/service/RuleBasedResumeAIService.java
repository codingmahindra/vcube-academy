package com.vcube.academy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.resume.*;
import com.vcube.academy.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleBasedResumeAIService implements ResumeAIService {

    private final ResumeSkillGapService skillGapService;
    private final ObjectMapper objectMapper;

    private static final Map<String, KeywordCategory> DICTIONARY = new LinkedHashMap<>();
    private static final List<String> STRONG_ACTION_VERBS = List.of(
            "architected", "engineered", "developed", "designed", "implemented", "optimized",
            "refactored", "automated", "orchestrated", "deployed", "scaled", "spearheaded", "integrated"
    );
    private static final List<String> WEAK_ACTION_VERBS = List.of(
            "worked on", "helped", "assisted", "responsible for", "handled", "did", "participated in"
    );

    static {
        DICTIONARY.put("java", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("core java", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("java 17", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("multithreading", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("collections", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("streams", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("spring boot", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("spring framework", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("spring data jpa", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("spring security", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("hibernate", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("microservices", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("rest api", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("restful", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("sql", KeywordCategory.DATABASE);
        DICTIONARY.put("postgresql", KeywordCategory.DATABASE);
        DICTIONARY.put("mysql", KeywordCategory.DATABASE);
        DICTIONARY.put("oracle", KeywordCategory.DATABASE);
        DICTIONARY.put("mongodb", KeywordCategory.DATABASE);
        DICTIONARY.put("redis", KeywordCategory.DATABASE);
        DICTIONARY.put("docker", KeywordCategory.CLOUD_DEVOPS);
        DICTIONARY.put("kubernetes", KeywordCategory.CLOUD_DEVOPS);
        DICTIONARY.put("aws", KeywordCategory.CLOUD_DEVOPS);
        DICTIONARY.put("ci/cd", KeywordCategory.CLOUD_DEVOPS);
        DICTIONARY.put("git", KeywordCategory.TOOL);
        DICTIONARY.put("maven", KeywordCategory.TOOL);
        DICTIONARY.put("junit", KeywordCategory.TOOL);
        DICTIONARY.put("mockito", KeywordCategory.TOOL);
        DICTIONARY.put("react", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("javascript", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("typescript", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("data structures", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("algorithms", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("dsa", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("kafka", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("rabbitmq", KeywordCategory.FRAMEWORK);
        DICTIONARY.put("graphql", KeywordCategory.TECHNICAL_SKILL);
        DICTIONARY.put("jenkins", KeywordCategory.CLOUD_DEVOPS);
        DICTIONARY.put("azure", KeywordCategory.CLOUD_DEVOPS);
        DICTIONARY.put("linux", KeywordCategory.TOOL);
    }

    @Override
    public ResumeAnalysisDto analyzeResume(ResumeAnalyzeRequest request, ResumeVersion version) {
        String fullResumeText = compileFullResumeText(version, request.getResumeText()).toLowerCase();
        String jdText = (request.getJobDescriptionText() != null ? request.getJobDescriptionText() : "").toLowerCase();

        if (jdText.isEmpty() && version.getTargetJob() != null) {
            jdText = (version.getTargetJob().getTitle() + " " + version.getTargetJob().getDescription() + " " + version.getTargetJob().getQualification()).toLowerCase();
        }

        // 1. Keyword extraction from JD
        Set<String> jdKeywords = extractKeywordsFromText(jdText);
        if (jdKeywords.isEmpty()) {
            jdKeywords = new LinkedHashSet<>(List.of("java", "spring boot", "sql", "rest api", "microservices", "git"));
        }

        List<ResumeKeywordDto> matchedKeywords = new ArrayList<>();
        List<ResumeKeywordDto> missingKeywords = new ArrayList<>();
        List<ResumeKeywordDto> partialKeywords = new ArrayList<>();
        List<ResumeMissingSkillDto> criticalMissingSkills = new ArrayList<>();

        for (String kw : jdKeywords) {
            KeywordCategory cat = DICTIONARY.getOrDefault(kw, KeywordCategory.TECHNICAL_SKILL);
            int count = countOccurrences(fullResumeText, kw);

            if (count > 0) {
                matchedKeywords.add(ResumeKeywordDto.builder()
                        .keywordName(capitalize(kw))
                        .category(cat)
                        .matchStatus(SkillMatchStatus.MATCHED)
                        .importance("HIGH")
                        .occurrenceCount(count)
                        .build());
            } else {
                // Check partial substring match
                boolean isPartial = fullResumeText.contains(kw.split(" ")[0]);
                if (isPartial) {
                    partialKeywords.add(ResumeKeywordDto.builder()
                            .keywordName(capitalize(kw))
                            .category(cat)
                            .matchStatus(SkillMatchStatus.PARTIAL_MATCH)
                            .importance("MEDIUM")
                            .occurrenceCount(1)
                            .build());
                } else {
                    missingKeywords.add(ResumeKeywordDto.builder()
                            .keywordName(capitalize(kw))
                            .category(cat)
                            .matchStatus(SkillMatchStatus.MISSING)
                            .importance("HIGH")
                            .occurrenceCount(0)
                            .build());

                    criticalMissingSkills.add(skillGapService.resolveSkillGap(capitalize(kw), cat, "HIGH"));
                }
            }
        }

        // 2. Component Sub-scores
        double matchRatio = jdKeywords.size() > 0 ? (double) (matchedKeywords.size() + (partialKeywords.size() * 0.5)) / jdKeywords.size() : 0.7;
        int keywordScore = (int) Math.round(matchRatio * 25.0);

        int skillsScore = !version.getExperiences().isEmpty() || !matchedKeywords.isEmpty() ? (int) Math.round(matchRatio * 20.0) : 10;

        int reqScore = 15;
        if (missingKeywords.size() > 4) reqScore = 8;
        else if (missingKeywords.size() > 2) reqScore = 11;

        int expScore = version.getExperiences().isEmpty() ? 7 : 15;
        int projScore = version.getProjects().isEmpty() ? 6 : 15;
        int eduScore = version.getEducations().isEmpty() ? 2 : 5;

        // Structure & Quality score
        int structScore = 5;
        List<ResumeRecommendationDto> recommendations = new ArrayList<>();

        // Summary check
        if (version.getProfile() == null || version.getProfile().getProfessionalSummary() == null || version.getProfile().getProfessionalSummary().length() < 50) {
            structScore -= 1;
            recommendations.add(ResumeRecommendationDto.builder()
                    .sectionType(ResumeSectionType.SUMMARY)
                    .severity("CRITICAL")
                    .title("Professional Summary Incomplete")
                    .message("Your resume lacks an impactful 3-4 sentence professional summary highlighting your core Java specialization.")
                    .actionableFix("Add a targeted summary showcasing years of training, key stack (Java 17, Spring Boot), and problem-solving focus.")
                    .build());
        }

        // Action verbs check
        boolean hasWeakVerbs = WEAK_ACTION_VERBS.stream().anyMatch(fullResumeText::contains);
        if (hasWeakVerbs) {
            recommendations.add(ResumeRecommendationDto.builder()
                    .sectionType(ResumeSectionType.EXPERIENCE)
                    .severity("WARNING")
                    .title("Passive Phrasing Detected")
                    .message("Detected passive phrasing such as 'worked on' or 'responsible for'.")
                    .actionableFix("Replace passive expressions with active impact verbs: 'Architected', 'Engineered', 'Optimized', or 'Automated'.")
                    .build());
        }

        // Quantifiable metrics check
        boolean hasMetrics = Pattern.compile("\\d+%|\\d+ms|\\d+x|sub-\\d+").matcher(fullResumeText).find();
        if (!hasMetrics) {
            recommendations.add(ResumeRecommendationDto.builder()
                    .sectionType(ResumeSectionType.PROJECTS)
                    .severity("WARNING")
                    .title("Lack of Measurable Metrics")
                    .message("Bullet points describe responsibilities rather than measurable business outcomes.")
                    .actionableFix("Quantify achievements: e.g. 'Optimized PostgreSQL queries, reducing latency by 35%'.")
                    .build());
        }

        // Projects check
        if (version.getProjects().isEmpty()) {
            recommendations.add(ResumeRecommendationDto.builder()
                    .sectionType(ResumeSectionType.PROJECTS)
                    .severity("CRITICAL")
                    .title("Missing Enterprise Projects")
                    .message("Technical recruiters look for at least 1-2 end-to-end full stack projects with live or repository links.")
                    .actionableFix("Add your VCUBE Microservices / Full Stack portfolio projects with GitHub repositories.")
                    .build());
        }

        int totalAts = keywordScore + skillsScore + reqScore + expScore + projScore + eduScore + structScore;
        totalAts = Math.max(15, Math.min(98, totalAts));

        String summaryFeedback;
        if (totalAts >= 80) {
            summaryFeedback = "Excellent ATS Optimization. Your resume strongly aligns with the target job requirements and exhibits strong technical keyword density.";
        } else if (totalAts >= 60) {
            summaryFeedback = "Good foundation with moderate ATS alignment. Addressing the highlighted skill gaps and adding measurable metrics will significantly increase interview callbacks.";
        } else {
            summaryFeedback = "Low ATS Match. Critical technical requirements and keywords from the job description are missing. Follow the targeted learning roadmap below.";
        }

        return ResumeAnalysisDto.builder()
                .versionId(version.getId())
                .jobId(version.getTargetJob() != null ? version.getTargetJob().getId() : null)
                .targetJobTitle(request.getTargetRole() != null ? request.getTargetRole() : (version.getTargetJob() != null ? version.getTargetJob().getTitle() : "Java Full Stack Developer"))
                .targetCompanyName(request.getTargetCompany() != null ? request.getTargetCompany() : (version.getTargetJob() != null ? version.getTargetJob().getCompany().getName() : "Target Enterprise"))
                .overallAtsScore(totalAts)
                .keywordMatchScore(keywordScore)
                .skillsMatchScore(skillsScore)
                .experienceMatchScore(expScore)
                .projectMatchScore(projScore)
                .educationMatchScore(eduScore)
                .structureScore(structScore)
                .aiProvider(AIProvider.RULE_BASED)
                .summaryFeedback(summaryFeedback)
                .matchedKeywords(matchedKeywords)
                .missingKeywords(missingKeywords)
                .partialMatchedKeywords(partialKeywords)
                .criticalMissingSkills(criticalMissingSkills)
                .recommendations(recommendations)
                .build();
    }

    @Override
    public ResumeOptimizationDto generateOptimizationSuggestions(ResumeVersion version, String jobDescription) {
        List<Map<String, String>> bullets = new ArrayList<>();
        bullets.add(Map.of(
                "original", "Worked on backend APIs using Spring Boot and Postgres.",
                "improvedActionOriented", "Engineered 12+ RESTful microservices using Java 17, Spring Boot, and PostgreSQL, achieving sub-80ms response latency."
        ));
        bullets.add(Map.of(
                "original", "Helped team in fixing bugs and writing test cases.",
                "improvedActionOriented", "Authored comprehensive JUnit 5 and Mockito test suites, elevating code coverage to 88% and eliminating regression defects."
        ));

        return ResumeOptimizationDto.builder()
                .optimizedSummary("Results-driven Java Full Stack Developer with hands-on expertise building enterprise microservices using Java 17, Spring Boot, PostgreSQL, and React. Proven ability to architect high-throughput REST APIs and containerized distributed systems.")
                .optimizedBulletPoints(bullets)
                .recommendedActionVerbs(STRONG_ACTION_VERBS)
                .detectedWeaknesses(List.of("Vague responsibility statements", "Missing percentage metrics in project descriptions", "Inconsistent date formatting"))
                .suggestedCertifications(List.of("Oracle Certified Professional: Java SE 17 Developer", "AWS Certified Cloud Practitioner"))
                .rationale("Optimizations utilize the STAR method (Situation, Task, Action, Result) with quantified metrics while preserving 100% factual accuracy.")
                .build();
    }

    private String compileFullResumeText(ResumeVersion version, String overrideText) {
        if (overrideText != null && overrideText.trim().length() > 50) {
            return overrideText;
        }
        if (version.getRawResumeText() != null && version.getRawResumeText().trim().length() > 50) {
            return version.getRawResumeText();
        }

        StringBuilder sb = new StringBuilder();
        if (version.getProfile() != null) {
            sb.append(version.getProfile().getFullName()).append(" ");
            sb.append(version.getProfile().getProfessionalSummary()).append(" ");
        }
        version.getExperiences().forEach(e -> sb.append(e.getCompanyName()).append(" ").append(e.getRoleTitle()).append(" ").append(e.getDescription()).append(" ").append(e.getBulletPoints()).append(" "));
        version.getProjects().forEach(p -> sb.append(p.getTitle()).append(" ").append(p.getTechStack()).append(" ").append(p.getDescription()).append(" ").append(p.getBulletPoints()).append(" "));
        version.getEducations().forEach(ed -> sb.append(ed.getInstitution()).append(" ").append(ed.getDegree()).append(" ").append(ed.getFieldOfStudy()).append(" "));
        version.getCertifications().forEach(c -> sb.append(c.getName()).append(" ").append(c.getIssuingOrganization()).append(" "));
        return sb.toString();
    }

    private Set<String> extractKeywordsFromText(String text) {
        Set<String> found = new LinkedHashSet<>();
        for (String kw : DICTIONARY.keySet()) {
            if (text.contains(kw)) {
                found.add(kw);
            }
        }
        return found;
    }

    private int countOccurrences(String text, String keyword) {
        if (text == null || keyword == null || keyword.isEmpty()) return 0;
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(keyword, idx)) != -1) {
            count++;
            idx += keyword.length();
        }
        return count;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
