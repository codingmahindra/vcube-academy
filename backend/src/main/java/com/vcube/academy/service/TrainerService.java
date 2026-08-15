package com.vcube.academy.service;

import com.vcube.academy.dto.quiz.QuizResultDto;
import com.vcube.academy.entity.QuizResult;
import com.vcube.academy.entity.RoleType;
import com.vcube.academy.entity.StudentProgress;
import com.vcube.academy.entity.User;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizResultRepository quizResultRepository;
    private final StudentProgressRepository studentProgressRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardData() {
        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName() == RoleType.STUDENT))
                .collect(Collectors.toList());

        long coursesCount = courseRepository.count();
        long topicsCount = topicRepository.count();
        long questionsCount = questionRepository.count();
        long totalAttempts = quizAttemptRepository.count();

        List<QuizResult> results = quizResultRepository.findAll();
        Double avgScore = results.stream()
                .mapToDouble(r -> r.getScorePercentage() != null ? r.getScorePercentage().doubleValue() : 0.0)
                .average()
                .orElse(0.0);

        List<Map<String, Object>> recentResults = results.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", r.getId());
                    map.put("studentName", r.getStudent() != null ? r.getStudent().getFullName() : "Unknown");
                    map.put("studentEmail", r.getStudent() != null ? r.getStudent().getEmail() : "");
                    map.put("totalQuestions", r.getTotalQuestions());
                    map.put("correctAnswers", r.getCorrectCount());
                    map.put("scorePercentage", r.getScorePercentage());
                    map.put("passed", r.getScorePercentage() != null && r.getScorePercentage().doubleValue() >= 60.0);
                    map.put("createdAt", r.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        List<StudentProgress> allProgress = studentProgressRepository.findAll();
        List<Map<String, Object>> progressSummaries = allProgress.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("studentName", p.getStudent() != null ? p.getStudent().getFullName() : "Unknown");
                    map.put("courseTitle", p.getCourse() != null ? p.getCourse().getTitle() : "General");
                    map.put("completedTopicsCount", p.getTopicsCompleted());
                    double pct = (p.getTotalTopics() != null && p.getTotalTopics() > 0)
                            ? Math.round(((double) p.getTopicsCompleted() / p.getTotalTopics()) * 100.0)
                            : 0.0;
                    map.put("completionPercentage", pct);
                    double quizAcc = (p.getTotalAttemptedQuestions() != null && p.getTotalAttemptedQuestions() > 0)
                            ? Math.round(((double) p.getTotalCorrect() / p.getTotalAttemptedQuestions()) * 100.0)
                            : 0.0;
                    map.put("quizAverageScore", quizAcc);
                    map.put("lastActivityAt", p.getLastActivityAt());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", students.size());
        data.put("totalCourses", coursesCount);
        data.put("totalTopics", topicsCount);
        data.put("totalQuestions", questionsCount);
        data.put("totalAttempts", totalAttempts);
        data.put("averageScorePercentage", Math.round(avgScore * 10.0) / 10.0);
        data.put("recentResults", recentResults);
        data.put("studentProgress", progressSummaries);

        return data;
    }
}
