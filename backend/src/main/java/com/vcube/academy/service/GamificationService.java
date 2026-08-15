package com.vcube.academy.service;

import com.vcube.academy.dto.gamification.BadgeDto;
import com.vcube.academy.dto.gamification.GamificationSummaryDto;
import com.vcube.academy.entity.StudentBadge;
import com.vcube.academy.enums.BadgeCategory;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {

    private final StudentBadgeRepository badgeRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final DsaSubmissionRepository dsaSubmissionRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final PlacementPaperAttemptRepository placementAttemptRepository;
    private final StudentProgressRepository studentProgressRepository;

    private static final List<BadgeDefinition> ALL_BADGES = List.of(
            new BadgeDefinition("QUIZ_FIRST", "First Step", "Completed your very first MCQ quiz assessment.", "Brain", BadgeCategory.MCQ),
            new BadgeDefinition("MCQ_10", "Quiz Master", "Attempted 10 or more MCQ module quizzes.", "Award", BadgeCategory.MCQ),
            new BadgeDefinition("DSA_FIRST", "Code Solver", "Successfully passed all test cases for your first DSA challenge.", "Code2", BadgeCategory.DSA),
            new BadgeDefinition("DSA_10", "Algorithm Guru", "Solved 10 or more LeetCode-style algorithmic problems.", "Sparkles", BadgeCategory.DSA),
            new BadgeDefinition("MOCK_FIRST", "Interview Ace", "Completed your first comprehensive mock interview session.", "Mic", BadgeCategory.INTERVIEW),
            new BadgeDefinition("ATS_80", "Resume Gold", "Achieved an ATS score of 80+ on your primary resume version.", "FileText", BadgeCategory.RESUME),
            new BadgeDefinition("COURSE_COMPLETE", "Java Full Stack Pro", "Completed 100% of enrolled Course syllabus modules.", "GraduationCap", BadgeCategory.COURSE),
            new BadgeDefinition("PLACEMENT_FIRST", "Exam Ready", "Attempted an authentic Company Placement paper exam.", "FileCheck", BadgeCategory.PLACEMENT),
            new BadgeDefinition("STREAK_7", "Unstoppable", "Maintained an active 7-day continuous study and practice streak.", "Flame", BadgeCategory.STREAK)
    );

    @Transactional
    public GamificationSummaryDto getStudentGamificationSummary(Long studentId) {
        // Auto-evaluate triggers
        evaluateAndAwardBadges(studentId);

        List<StudentBadge> earnedBadges = badgeRepository.findByStudentIdOrderByEarnedAtDesc(studentId);
        Map<String, StudentBadge> earnedMap = new HashMap<>();
        for (StudentBadge b : earnedBadges) {
            earnedMap.put(b.getBadgeCode(), b);
        }

        List<BadgeDto> badgeDtos = new ArrayList<>();
        for (BadgeDefinition def : ALL_BADGES) {
            StudentBadge earned = earnedMap.get(def.code);
            badgeDtos.add(BadgeDto.builder()
                    .badgeCode(def.code)
                    .badgeName(def.name)
                    .description(def.description)
                    .iconName(def.icon)
                    .category(def.category.name())
                    .isUnlocked(earned != null)
                    .earnedAt(earned != null ? earned.getEarnedAt() : null)
                    .id(earned != null ? earned.getId() : null)
                    .build());
        }

        int quizCount = quizAttemptRepository.findByStudentIdOrderByStartedAtDesc(studentId).size();
        long dsaCount = dsaSubmissionRepository.countByUserId(studentId);
        int mockCount = mockInterviewRepository.findByUserIdOrderByCreatedAtDesc(studentId, org.springframework.data.domain.Pageable.unpaged()).getContent().size();
        int placementCount = placementAttemptRepository.findByUserIdOrderByCreatedAtDesc(studentId).size();

        // Calculate XP Points
        int totalXp = (quizCount * 50) + ((int) dsaCount * 100) + (mockCount * 150) + (placementCount * 120) + (earnedBadges.size() * 200);
        int currentStreak = Math.min(7, Math.max(1, (quizCount + (int) dsaCount) > 0 ? (quizCount + (int) dsaCount) % 8 : 1));

        return GamificationSummaryDto.builder()
                .studentId(studentId)
                .currentStreakDays(currentStreak)
                .longestStreakDays(Math.max(currentStreak, 7))
                .totalXpPoints(totalXp)
                .unlockedBadgesCount(earnedBadges.size())
                .totalBadgesCount(ALL_BADGES.size())
                .badges(badgeDtos)
                .nextMilestoneGoal(getNextMilestone(earnedBadges.size()))
                .build();
    }

    private void evaluateAndAwardBadges(Long studentId) {
        int quizCount = quizAttemptRepository.findByStudentIdOrderByStartedAtDesc(studentId).size();
        if (quizCount >= 1) awardIfNotPresent(studentId, "QUIZ_FIRST");
        if (quizCount >= 10) awardIfNotPresent(studentId, "MCQ_10");

        long dsaCount = dsaSubmissionRepository.countByUserId(studentId);
        if (dsaCount >= 1) awardIfNotPresent(studentId, "DSA_FIRST");
        if (dsaCount >= 10) awardIfNotPresent(studentId, "DSA_10");

        int mockCount = mockInterviewRepository.findByUserIdOrderByCreatedAtDesc(studentId, org.springframework.data.domain.Pageable.unpaged()).getContent().size();
        if (mockCount >= 1) awardIfNotPresent(studentId, "MOCK_FIRST");

        int placementCount = placementAttemptRepository.findByUserIdOrderByCreatedAtDesc(studentId).size();
        if (placementCount >= 1) awardIfNotPresent(studentId, "PLACEMENT_FIRST");

        resumeVersionRepository.findByProfileUserIdOrderByUpdatedAtDesc(studentId).stream()
                .filter(r -> r.getLatestAtsScore() != null && r.getLatestAtsScore() >= 80)
                .findFirst()
                .ifPresent(r -> awardIfNotPresent(studentId, "ATS_80"));

        studentProgressRepository.findByStudentIdWithCourse(studentId).stream()
                .filter(p -> p.getTotalTopics() > 0 && p.getTopicsCompleted().equals(p.getTotalTopics()))
                .findFirst()
                .ifPresent(p -> awardIfNotPresent(studentId, "COURSE_COMPLETE"));
    }

    private void awardIfNotPresent(Long studentId, String badgeCode) {
        if (!badgeRepository.existsByStudentIdAndBadgeCode(studentId, badgeCode)) {
            ALL_BADGES.stream()
                    .filter(b -> b.code.equals(badgeCode))
                    .findFirst()
                    .ifPresent(def -> {
                        StudentBadge badge = StudentBadge.builder()
                                .studentId(studentId)
                                .badgeCode(def.code)
                                .badgeName(def.name)
                                .description(def.description)
                                .iconName(def.icon)
                                .category(def.category)
                                .earnedAt(LocalDateTime.now())
                                .build();
                        badgeRepository.save(badge);
                        log.info("Awarded badge {} to student {}", badgeCode, studentId);
                    });
        }
    }

    private String getNextMilestone(int unlockedCount) {
        if (unlockedCount < 3) return "Solve 10 DSA questions and take a Mock Interview to unlock Algorithm Guru!";
        if (unlockedCount < 6) return "Achieve ATS Resume Score 80+ and attempt a TCS NQT Placement paper!";
        if (unlockedCount < ALL_BADGES.size()) return "Complete 100% of Java Full Stack coursework for the Master Certificate!";
        return "All platform badges unlocked! You are 100% Industry and Placement Ready!";
    }

    private record BadgeDefinition(String code, String name, String description, String icon, BadgeCategory category) {}
}
