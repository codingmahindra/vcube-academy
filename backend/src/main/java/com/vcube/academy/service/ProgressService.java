package com.vcube.academy.service;

import com.vcube.academy.dto.response.ProgressDto;
import com.vcube.academy.dto.response.StudentStatsDto;
import com.vcube.academy.entity.StudentProgress;
import com.vcube.academy.entity.WeakTopic;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressService {

    private final StudentProgressRepository progressRepository;
    private final WeakTopicRepository weakTopicRepository;
    private final TopicCompletionRepository completionRepository;

    public StudentStatsDto getStudentStats(Long studentId) {
        List<StudentProgress> allProgress = progressRepository.findByStudentIdWithCourse(studentId);

        int totalTopicsCompleted = (int) completionRepository.countByStudentId(studentId);
        int totalQuizAttempts = allProgress.stream().mapToInt(StudentProgress::getQuizAttempts).sum();
        int totalCorrect = allProgress.stream().mapToInt(StudentProgress::getTotalCorrect).sum();
        int totalAttempted = allProgress.stream().mapToInt(StudentProgress::getTotalAttemptedQuestions).sum();

        BigDecimal overallAccuracy = totalAttempted > 0
                ? BigDecimal.valueOf((double) totalCorrect / totalAttempted * 100).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<ProgressDto> courseProgressList = allProgress.stream()
                .map(sp -> toCourseProgressDto(sp, studentId))
                .toList();

        return StudentStatsDto.builder()
                .totalCoursesEnrolled((int) progressRepository.countDistinctCoursesByStudentId(studentId))
                .totalTopicsCompleted(totalTopicsCompleted)
                .totalQuizAttempts(totalQuizAttempts)
                .totalCorrectAnswers(totalCorrect)
                .totalAttemptedQuestions(totalAttempted)
                .overallAccuracy(overallAccuracy)
                .courseProgress(courseProgressList)
                .build();
    }

    public List<ProgressDto> getCourseProgress(Long studentId) {
        return progressRepository.findByStudentIdWithCourse(studentId).stream()
                .map(sp -> toCourseProgressDto(sp, studentId))
                .toList();
    }

    public List<ProgressDto.WeakTopicDto> getWeakTopics(Long studentId) {
        BigDecimal threshold = BigDecimal.valueOf(60);
        return weakTopicRepository.findWeakByStudentId(studentId, threshold).stream()
                .map(wt -> ProgressDto.WeakTopicDto.builder()
                        .topicId(wt.getTopic().getId())
                        .topicTitle(wt.getTopic().getTitle())
                        .totalQuestions(wt.getTotalQuestions())
                        .correctCount(wt.getCorrectCount())
                        .accuracyPct(wt.getAccuracyPct())
                        .lastAttemptedAt(wt.getLastAttemptedAt())
                        .build())
                .toList();
    }

    private ProgressDto toCourseProgressDto(StudentProgress sp, Long studentId) {
        List<WeakTopic> weakTopics = weakTopicRepository.findByStudentIdOrderByAccuracyAsc(studentId);

        BigDecimal accuracy = sp.getTotalAttemptedQuestions() > 0
                ? BigDecimal.valueOf((double) sp.getTotalCorrect() / sp.getTotalAttemptedQuestions() * 100)
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<ProgressDto.WeakTopicDto> weakDtos = weakTopics.stream()
                .filter(wt -> wt.getTopic().getModule().getCourse().getId().equals(sp.getCourse().getId()))
                .map(wt -> ProgressDto.WeakTopicDto.builder()
                        .topicId(wt.getTopic().getId())
                        .topicTitle(wt.getTopic().getTitle())
                        .totalQuestions(wt.getTotalQuestions())
                        .correctCount(wt.getCorrectCount())
                        .accuracyPct(wt.getAccuracyPct())
                        .lastAttemptedAt(wt.getLastAttemptedAt())
                        .build())
                .toList();

        return ProgressDto.builder()
                .courseId(sp.getCourse().getId())
                .courseTitle(sp.getCourse().getTitle())
                .courseSlug(sp.getCourse().getSlug())
                .topicsCompleted(sp.getTopicsCompleted())
                .totalTopics(sp.getTotalTopics())
                .quizAttempts(sp.getQuizAttempts())
                .totalCorrect(sp.getTotalCorrect())
                .totalAttemptedQuestions(sp.getTotalAttemptedQuestions())
                .overallAccuracy(accuracy)
                .lastActivityAt(sp.getLastActivityAt())
                .weakTopics(weakDtos)
                .build();
    }
}
