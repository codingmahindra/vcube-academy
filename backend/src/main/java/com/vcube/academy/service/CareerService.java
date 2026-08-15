package com.vcube.academy.service;

import com.vcube.academy.dto.career.CareerDashboardDto;
import com.vcube.academy.entity.User;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerService {

    private final StudentProgressRepository studentProgressRepository;
    private final TopicCompletionRepository topicCompletionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final DsaStudentProgressRepository dsaProgressRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final JobApplicationRepository applicationRepository;
    private final DailyPreparationPlanRepository dailyPlanRepository;
    private final CareerWeakAreaRepository weakAreaRepository;

    public CareerDashboardDto getStudentCareerDashboard(User student) {
        Long userId = student.getId();

        int enrolled = studentProgressRepository.findByStudentIdWithCourse(userId).size();
        int topicsDone = topicCompletionRepository.findByStudentId(userId).size();
        var quizAttempts = quizAttemptRepository.findByStudentIdOrderByStartedAtDesc(userId);
        int mcqAttempts = quizAttempts.size();
        double mcqAccuracy = quizAttempts.isEmpty() ? 0.0 :
                quizAttempts.stream()
                        .filter(q -> q.getResult() != null && q.getResult().getScorePercentage() != null)
                        .mapToDouble(q -> q.getResult().getScorePercentage().doubleValue())
                        .average().orElse(0.0);

        int dsaSolved = (int) dsaProgressRepository.findByUserId(userId).stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsSolved())).count();

        int mockCount = (int) mockInterviewRepository.countByUserIdAndStatus(userId, com.vcube.academy.entity.MockInterviewStatus.COMPLETED);
        Double avgMockScoreVal = mockInterviewRepository.getAverageScoreByUserId(userId);
        int avgMockScore = avgMockScoreVal != null ? avgMockScoreVal.intValue() : 0;

        var primaryResume = resumeVersionRepository.findByProfileUserIdOrderByUpdatedAtDesc(userId)
                .stream().filter(v -> Boolean.TRUE.equals(v.getIsPrimary())).findFirst().orElse(null);
        int atsScore = primaryResume != null && primaryResume.getLatestAtsScore() != null ? primaryResume.getLatestAtsScore() : 0;

        var apps = applicationRepository.findByUserId(userId);
        int appCount = apps.size();
        int shortlisted = (int) apps.stream().filter(a -> "SHORTLISTED".equalsIgnoreCase(a.getStatus().name())).count();
        int interviewScheduled = (int) apps.stream().filter(a -> "INTERVIEW_SCHEDULED".equalsIgnoreCase(a.getStatus().name())).count();
        int offers = (int) apps.stream().filter(a -> "OFFERED".equalsIgnoreCase(a.getStatus().name())).count();

        var dailyPlan = dailyPlanRepository.findByUserIdAndPlanDate(userId, LocalDate.now()).orElse(null);
        int dailyPct = dailyPlan != null ? dailyPlan.getCompletionPercentage() : 0;

        String readiness = avgMockScore >= 75 && atsScore >= 70 && dsaSolved >= 5 ? "READY" :
                avgMockScore >= 50 || atsScore >= 50 ? "NEEDS_MORE_PREPARATION" : "NOT_READY";

        var weakAreas = weakAreaRepository.findByUserIdOrderByWeaknessScoreDesc(userId).stream()
                .map(w -> w.getSkillOrTopicName())
                .limit(3)
                .toList();

        return CareerDashboardDto.builder()
                .profileCompletionPercentage(90)
                .latestAtsScore(atsScore)
                .averageJobMatchPercentage(atsScore > 0 ? atsScore : 65)
                .coursesEnrolled(enrolled)
                .coursesCompleted(enrolled > 0 ? 1 : 0)
                .totalTopicsCompleted(topicsDone)
                .mcqTotalAttempts(mcqAttempts)
                .mcqAccuracyPercentage(Math.round(mcqAccuracy * 100.0) / 100.0)
                .dsaProblemsSolved(dsaSolved)
                .mockInterviewsCompleted(mockCount)
                .averageMockScore(avgMockScore)
                .interviewReadinessStatus(readiness)
                .totalApplicationsCount(appCount)
                .shortlistedApplicationsCount(shortlisted)
                .interviewScheduledCount(interviewScheduled)
                .offersCount(offers)
                .topWeakSkills(weakAreas.isEmpty() ? List.of("Spring Security", "PostgreSQL Indexes", "Binary Trees") : weakAreas)
                .currentRoadmapStage(dsaSolved < 5 ? "DSA Problem Solving" : atsScore < 70 ? "ATS Resume Perfection" : "Active Job Applications")
                .dailyGoalCompletionPercentage(dailyPct)
                .build();
    }
}
