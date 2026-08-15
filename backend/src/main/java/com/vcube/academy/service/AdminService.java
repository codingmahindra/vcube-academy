package com.vcube.academy.service;

import com.vcube.academy.dto.user.UserDto;
import com.vcube.academy.entity.Role;
import com.vcube.academy.entity.RoleType;
import com.vcube.academy.entity.User;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizResultRepository quizResultRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        List<User> allUsers = userRepository.findAll();
        long studentsCount = allUsers.stream().filter(u -> u.getRoles().stream().anyMatch(r -> r.getName() == RoleType.STUDENT)).count();
        long trainersCount = allUsers.stream().filter(u -> u.getRoles().stream().anyMatch(r -> r.getName() == RoleType.TRAINER)).count();
        long adminsCount = allUsers.stream().filter(u -> u.getRoles().stream().anyMatch(r -> r.getName() == RoleType.ADMIN)).count();

        long coursesCount = courseRepository.count();
        long topicsCount = topicRepository.count();
        long questionsCount = questionRepository.count();
        long totalAttempts = quizAttemptRepository.count();

        Double avgScore = quizResultRepository.findAll().stream()
                .mapToDouble(r -> r.getScorePercentage() != null ? r.getScorePercentage().doubleValue() : 0.0)
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentsCount);
        stats.put("totalTrainers", trainersCount);
        stats.put("totalAdmins", adminsCount);
        stats.put("totalCourses", coursesCount);
        stats.put("totalTopics", topicsCount);
        stats.put("totalQuestions", questionsCount);
        stats.put("totalQuizAttempts", totalAttempts);
        stats.put("averageScorePercentage", Math.round(avgScore * 10.0) / 10.0);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        user.setIsActive(!user.getIsActive());
        User saved = userRepository.save(user);
        return toUserDto(saved);
    }

    @Transactional
    public UserDto updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        RoleType roleType = RoleType.valueOf(roleName.toUpperCase());
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User saved = userRepository.save(user);
        return toUserDto(saved);
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }
}
