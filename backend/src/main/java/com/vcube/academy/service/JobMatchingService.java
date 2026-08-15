package com.vcube.academy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.job.JobMatchResultDto;
import com.vcube.academy.entity.Job;
import com.vcube.academy.entity.JobSkillMapping;
import com.vcube.academy.entity.StudentJobPreference;
import com.vcube.academy.repository.StudentJobPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobMatchingService {

    private final StudentJobPreferenceRepository preferenceRepository;
    private final ObjectMapper objectMapper;

    public JobMatchResultDto calculateMatch(Job job, Long userId) {
        if (userId == null) {
            return defaultMatchResult(job);
        }

        Optional<StudentJobPreference> prefOpt = preferenceRepository.findByUserId(userId);
        if (prefOpt.isEmpty()) {
            return defaultMatchResult(job);
        }

        StudentJobPreference pref = prefOpt.get();
        List<String> userTechs = parseJsonList(pref.getPreferredTechnologies()).stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> userRoles = parseJsonList(pref.getPreferredRoles()).stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> userLocations = parseJsonList(pref.getPreferredLocations()).stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        // Skills analysis
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        int requiredCount = 0;
        int requiredMatched = 0;

        for (JobSkillMapping sm : job.getSkillMappings()) {
            String skillName = sm.getSkill().getName();
            boolean isUserHasSkill = userTechs.stream().anyMatch(t -> skillName.toLowerCase().contains(t) || t.contains(skillName.toLowerCase()));

            if (sm.getIsRequired()) {
                requiredCount++;
                if (isUserHasSkill) {
                    requiredMatched++;
                    matchedSkills.add(skillName);
                } else {
                    missingSkills.add(skillName);
                }
            } else {
                if (isUserHasSkill) {
                    matchedSkills.add(skillName);
                }
            }
        }

        double skillScore = requiredCount > 0 ? ((double) requiredMatched / requiredCount) * 60.0 : 50.0;

        // Role matching
        boolean roleMatched = userRoles.isEmpty() || userRoles.stream().anyMatch(r -> job.getTitle().toLowerCase().contains(r));
        double roleScore = roleMatched ? 10.0 : 0.0;

        // Location matching
        boolean locMatched = userLocations.isEmpty() || userLocations.stream().anyMatch(l -> job.getLocation().toLowerCase().contains(l));
        double locScore = locMatched ? 10.0 : 0.0;

        // Work Mode matching
        boolean modeMatched = pref.getWorkMode() == null || pref.getWorkMode() == job.getWorkMode();
        double modeScore = modeMatched ? 10.0 : 0.0;

        // Experience matching
        boolean expMatched = pref.getExperienceLevel() == null || pref.getExperienceLevel() == job.getExperienceLevel();
        double expScore = expMatched ? 10.0 : 0.0;

        int totalMatch = (int) Math.round(skillScore + roleScore + locScore + modeScore + expScore);
        totalMatch = Math.max(10, Math.min(100, totalMatch));

        String summary;
        if (totalMatch >= 80) {
            summary = "High Match! Your skills and career preferences strongly align with this opportunity.";
        } else if (totalMatch >= 55) {
            summary = "Moderate Match. Bridge the missing technical skills using our recommended roadmap below.";
        } else {
            summary = "Skill Gap Identified. Review the recommended practice roadmap before applying.";
        }

        return JobMatchResultDto.builder()
                .matchPercentage(totalMatch)
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .rolePreferenceMatched(roleMatched)
                .locationPreferenceMatched(locMatched)
                .workModeMatched(modeMatched)
                .experienceMatched(expMatched)
                .summary(summary)
                .build();
    }

    private JobMatchResultDto defaultMatchResult(Job job) {
        List<String> requiredSkills = job.getSkillMappings().stream()
                .filter(JobSkillMapping::getIsRequired)
                .map(sm -> sm.getSkill().getName())
                .collect(Collectors.toList());

        return JobMatchResultDto.builder()
                .matchPercentage(70)
                .matchedSkills(List.of("Core Java"))
                .missingSkills(requiredSkills)
                .rolePreferenceMatched(true)
                .locationPreferenceMatched(true)
                .workModeMatched(true)
                .experienceMatched(true)
                .summary("Configure your career preferences in your profile to receive personalized match calculation.")
                .build();
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.singletonList(json);
        }
    }
}
