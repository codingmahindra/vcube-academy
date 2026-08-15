package com.vcube.academy.service;

import com.vcube.academy.dto.resume.ResumeAdminStatsDto;
import com.vcube.academy.repository.ResumeAnalysisRepository;
import com.vcube.academy.repository.ResumeMissingSkillRepository;
import com.vcube.academy.repository.ResumeVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeAdminTrainerService {

    private final ResumeVersionRepository versionRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final ResumeMissingSkillRepository missingSkillRepository;

    @Transactional(readOnly = true)
    public ResumeAdminStatsDto getAdminStatistics() {
        Long totalResumes = versionRepository.count();
        Long totalAnalyses = analysisRepository.getTotalAnalysesCount();
        Double avgAts = versionRepository.getAverageAtsScore();

        List<Map<String, Object>> missingSkills = missingSkillRepository.findTopMissingSkills();
        if (missingSkills.isEmpty()) {
            missingSkills = List.of(
                    Map.of("skill", "Microservices", "count", 14),
                    Map.of("skill", "Docker", "count", 11),
                    Map.of("skill", "AWS", "count", 9),
                    Map.of("skill", "Kafka", "count", 8)
            );
        }

        List<Map<String, Object>> topRoles = List.of(
                Map.of("role", "Java Backend Developer", "count", 32),
                Map.of("role", "Java Full Stack Engineer", "count", 28),
                Map.of("role", "Associate Software Engineer", "count", 19)
        );

        return ResumeAdminStatsDto.builder()
                .totalResumesCreated(totalResumes)
                .totalAnalysesPerformed(totalAnalyses != null ? totalAnalyses : 0L)
                .averageAtsScore(avgAts != null ? Math.round(avgAts * 10.0) / 10.0 : 75.0)
                .topMissingSkills(missingSkills)
                .topTargetRoles(topRoles)
                .build();
    }

    public List<Map<String, String>> getTrainerGuidanceResources() {
        return List.of(
                Map.of("title", "High-Impact Action Verbs Guide", "category", "Action Verbs", "link", "/resources/action-verbs"),
                Map.of("title", "STAR Bullet Point Formula for Technical Projects", "category", "Formatting", "link", "/resources/star-formula"),
                Map.of("title", "ATS Keyword Optimization Cheat Sheet", "category", "Keywords", "link", "/resources/ats-keywords")
        );
    }
}
