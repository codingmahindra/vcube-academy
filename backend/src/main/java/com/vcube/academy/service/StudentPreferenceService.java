package com.vcube.academy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.job.StudentJobPreferenceDto;
import com.vcube.academy.entity.EmploymentType;
import com.vcube.academy.entity.ExperienceLevel;
import com.vcube.academy.entity.StudentJobPreference;
import com.vcube.academy.entity.User;
import com.vcube.academy.entity.WorkMode;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.StudentJobPreferenceRepository;
import com.vcube.academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentPreferenceService {

    private final StudentJobPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public StudentJobPreferenceDto getPreferences(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .map(this::mapToDto)
                .orElseGet(() -> StudentJobPreferenceDto.builder()
                        .preferredRoles(List.of("Java Developer", "Full Stack Developer", "Backend Engineer"))
                        .preferredLocations(List.of("Hyderabad", "Bangalore", "Pune", "Remote"))
                        .preferredTechnologies(List.of("Java", "Spring Boot", "SQL", "React"))
                        .experienceLevel(ExperienceLevel.FRESHER)
                        .workMode(WorkMode.HYBRID)
                        .employmentType(EmploymentType.FULL_TIME)
                        .build());
    }

    @Transactional
    public StudentJobPreferenceDto savePreferences(StudentJobPreferenceDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StudentJobPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> StudentJobPreference.builder().user(user).build());

        pref.setPreferredRoles(writeJson(dto.getPreferredRoles()));
        pref.setPreferredLocations(writeJson(dto.getPreferredLocations()));
        pref.setPreferredTechnologies(writeJson(dto.getPreferredTechnologies()));
        if (dto.getExperienceLevel() != null) pref.setExperienceLevel(dto.getExperienceLevel());
        if (dto.getWorkMode() != null) pref.setWorkMode(dto.getWorkMode());
        if (dto.getEmploymentType() != null) pref.setEmploymentType(dto.getEmploymentType());
        if (dto.getExpectedSalaryMin() != null) pref.setExpectedSalaryMin(dto.getExpectedSalaryMin());

        pref = preferenceRepository.save(pref);
        return mapToDto(pref);
    }

    private StudentJobPreferenceDto mapToDto(StudentJobPreference p) {
        return StudentJobPreferenceDto.builder()
                .preferredRoles(parseJsonList(p.getPreferredRoles()))
                .preferredLocations(parseJsonList(p.getPreferredLocations()))
                .preferredTechnologies(parseJsonList(p.getPreferredTechnologies()))
                .experienceLevel(p.getExperienceLevel())
                .workMode(p.getWorkMode())
                .employmentType(p.getEmploymentType())
                .expectedSalaryMin(p.getExpectedSalaryMin())
                .build();
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
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
