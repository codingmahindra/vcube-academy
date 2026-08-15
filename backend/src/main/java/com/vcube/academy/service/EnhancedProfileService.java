package com.vcube.academy.service;

import com.vcube.academy.dto.user.StudentProfileDto;
import com.vcube.academy.dto.user.StudentProfileUpdateRequest;
import com.vcube.academy.entity.StudentJobPreference;
import com.vcube.academy.entity.User;
import com.vcube.academy.repository.StudentJobPreferenceRepository;
import com.vcube.academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedProfileService {

    private final UserRepository userRepository;
    private final StudentJobPreferenceRepository preferenceRepository;

    @Transactional(readOnly = true)
    public StudentProfileDto getStudentProfile(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        Optional<StudentJobPreference> prefOpt = preferenceRepository.findByUserId(studentId);

        Set<String> roles = prefOpt.map(p -> parseCommaSeparated(p.getPreferredRoles()))
                .orElse(Set.of("Java Developer", "Full Stack Developer", "Backend Engineer"));
        Set<String> locations = prefOpt.map(p -> parseCommaSeparated(p.getPreferredLocations()))
                .orElse(Set.of("Hyderabad", "Bengaluru", "Remote"));

        return StudentProfileDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .college("VCUBE Institute of Engineering & Technology")
                .degree("B.Tech Computer Science & Engineering")
                .graduationYear("2025")
                .cgpa(8.45)
                .bio("Aspiring Java Full Stack Developer trained at VCUBE Software Solutions. Proficient in Core Java, Spring Boot, Hibernate, PostgreSQL, and React.")
                .linkedinUrl("https://linkedin.com/in/" + user.getFullName().toLowerCase().replace(" ", "-"))
                .githubUrl("https://github.com/" + user.getFullName().toLowerCase().replace(" ", "-"))
                .portfolioUrl("https://" + user.getFullName().toLowerCase().replace(" ", "-") + ".dev")
                .technicalSkills(List.of("Java", "Spring Boot", "REST APIs", "PostgreSQL", "React", "Data Structures", "Docker", "Git"))
                .targetRoles(roles)
                .preferredLocations(locations)
                .includeInResume(true)
                .includeInAtsAnalysis(true)
                .includeInCopilot(true)
                .build();
    }

    @Transactional
    public StudentProfileDto updateStudentProfile(Long studentId, StudentProfileUpdateRequest req) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        user.setFullName(req.getFullName());
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }
        userRepository.save(user);

        StudentJobPreference pref = preferenceRepository.findByUserId(studentId)
                .orElseGet(() -> StudentJobPreference.builder().user(user).build());

        if (req.getTargetRoles() != null && !req.getTargetRoles().isEmpty()) {
            pref.setPreferredRoles(String.join(",", req.getTargetRoles()));
        }
        if (req.getPreferredLocations() != null && !req.getPreferredLocations().isEmpty()) {
            pref.setPreferredLocations(String.join(",", req.getPreferredLocations()));
        }
        preferenceRepository.save(pref);

        log.info("Updated profile for student {}", studentId);
        return getStudentProfile(studentId);
    }

    private Set<String> parseCommaSeparated(String str) {
        if (str == null || str.isBlank()) return Collections.emptySet();
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
