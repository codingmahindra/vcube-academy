package com.vcube.academy.service;

import com.vcube.academy.dto.career.CareerRoadmapDto;
import com.vcube.academy.dto.career.CareerRoadmapStageDto;
import com.vcube.academy.entity.CareerRoadmapStage;
import com.vcube.academy.entity.CareerRoadmapStatus;
import com.vcube.academy.entity.User;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerRoadmapService {

    private final StudentProgressRepository studentProgressRepository;
    private final DsaStudentProgressRepository dsaProgressRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final JobApplicationRepository applicationRepository;

    public CareerRoadmapDto generateRoadmap(User student) {
        Long userId = student.getId();

        // 1. Metrics evaluation
        int enrolledCount = studentProgressRepository.findByStudentIdWithCourse(userId).size();
        int dsaSolved = (int) dsaProgressRepository.findByUserId(userId).stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsSolved())).count();
        int mockCount = (int) mockInterviewRepository.countByUserIdAndStatus(userId, com.vcube.academy.entity.MockInterviewStatus.COMPLETED);
        var primaryResume = resumeVersionRepository.findByProfileUserIdOrderByUpdatedAtDesc(userId)
                .stream().filter(v -> Boolean.TRUE.equals(v.getIsPrimary())).findFirst().orElse(null);
        int atsScore = primaryResume != null && primaryResume.getLatestAtsScore() != null ? primaryResume.getLatestAtsScore() : 0;
        int appCount = applicationRepository.findByUserId(userId).size();

        List<CareerRoadmapStageDto> stages = new ArrayList<>();

        // Stage 1: Foundation & Onboarding
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.FOUNDATION)
                .title("1. Foundation & Onboarding")
                .description("Profile setup, career preference configuration, and learning curriculum selection.")
                .status(CareerRoadmapStatus.COMPLETED)
                .completionPercentage(100)
                .focusAreas(List.of("Profile Setup", "Preferences", "Platform Navigation"))
                .recommendedActionTitle("Review Profile")
                .recommendedActionLink("/profile")
                .build());

        // Stage 2: Core Java Mastery
        int coreJavaPct = Math.min(100, Math.max(30, enrolledCount * 25));
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.CORE_JAVA)
                .title("2. Core Java Mastery (Java 17/21)")
                .description("OOPs, JVM Architecture, Collections Framework, Multithreading & Streams API.")
                .status(coreJavaPct >= 80 ? CareerRoadmapStatus.COMPLETED : CareerRoadmapStatus.IN_PROGRESS)
                .completionPercentage(coreJavaPct)
                .focusAreas(List.of("OOPs Principles", "Collections Framework", "Streams API", "Exception Handling"))
                .recommendedActionTitle("Continue Java Course")
                .recommendedActionLink("/student/courses")
                .build());

        // Stage 3: Backend & Spring Boot
        int springPct = Math.min(100, enrolledCount > 1 ? 75 : 40);
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.BACKEND_SPRING)
                .title("3. Spring Boot & Microservices")
                .description("REST API design, Spring Security JWT, Spring Data JPA, Microservices & Docker.")
                .status(springPct >= 80 ? CareerRoadmapStatus.COMPLETED : CareerRoadmapStatus.IN_PROGRESS)
                .completionPercentage(springPct)
                .focusAreas(List.of("Spring Boot 3", "REST APIs", "Spring Data JPA", "Microservices"))
                .recommendedActionTitle("Explore Backend Modules")
                .recommendedActionLink("/student/courses")
                .build());

        // Stage 4: Database & SQL Mastery
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.DATABASE_SQL)
                .title("4. PostgreSQL & Advanced SQL")
                .description("Complex JOINs, Subqueries, Aggregations, Transactions & Index Optimization.")
                .status(CareerRoadmapStatus.IN_PROGRESS)
                .completionPercentage(60)
                .focusAreas(List.of("PostgreSQL", "Complex Joins", "Indexes", "Transactions"))
                .recommendedActionTitle("Practice SQL MCQs")
                .recommendedActionLink("/student/courses")
                .build());

        // Stage 5: DSA Problem Solving
        int dsaPct = Math.min(100, dsaSolved * 20);
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.DSA_PROBLEM_SOLVING)
                .title("5. DSA Problem Solving")
                .description("Arrays, Two Pointers, Linked Lists, Trees, Graphs & Dynamic Programming.")
                .status(dsaPct >= 80 ? CareerRoadmapStatus.COMPLETED : dsaPct > 0 ? CareerRoadmapStatus.IN_PROGRESS : CareerRoadmapStatus.RECOMMENDED)
                .completionPercentage(dsaPct)
                .focusAreas(List.of("Arrays & Strings", "Binary Trees", "Graphs", "DP"))
                .recommendedActionTitle("Solve Next DSA Problem")
                .recommendedActionLink("/student/dsa")
                .build());

        // Stage 6: Interview Preparation
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.INTERVIEW_PREPARATION)
                .title("6. Technical Interview Preparation")
                .description("Verified company question banks for TCS, Infosys, Amazon, JPMorgan, Accenture.")
                .status(CareerRoadmapStatus.IN_PROGRESS)
                .completionPercentage(50)
                .focusAreas(List.of("TCS NQT", "Infosys DSE", "Amazon SDE-1", "System Design"))
                .recommendedActionTitle("Practice Interview Questions")
                .recommendedActionLink("/student/interview")
                .build());

        // Stage 7: Advanced Mock Interview Mastery
        int mockPct = Math.min(100, mockCount * 35);
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.MOCK_INTERVIEW_MASTERY)
                .title("7. Live Mock Interview Simulations")
                .description("Full-length interactive simulations with instant AI grading and follow-ups.")
                .status(mockPct >= 80 ? CareerRoadmapStatus.COMPLETED : mockPct > 0 ? CareerRoadmapStatus.IN_PROGRESS : CareerRoadmapStatus.RECOMMENDED)
                .completionPercentage(mockPct)
                .focusAreas(List.of("Live Technical Round", "HR Round", "Scenario Questions"))
                .recommendedActionTitle("Take Mock Interview")
                .recommendedActionLink("/student/interview/mock")
                .build());

        // Stage 8: ATS Resume Perfection
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.ATS_RESUME_PERFECTION)
                .title("8. ATS Resume Perfection")
                .description("Tailored ATS resume versions with quantified achievements and keyword match > 80%.")
                .status(atsScore >= 75 ? CareerRoadmapStatus.COMPLETED : atsScore > 0 ? CareerRoadmapStatus.IN_PROGRESS : CareerRoadmapStatus.RECOMMENDED)
                .completionPercentage(atsScore)
                .focusAreas(List.of("ATS Scoring", "Keyword Optimization", "PDF Export"))
                .recommendedActionTitle("Optimize Resume")
                .recommendedActionLink("/student/resume/analyzer")
                .build());

        // Stage 9: Active Job Applications
        int appPct = Math.min(100, appCount * 30);
        stages.add(CareerRoadmapStageDto.builder()
                .stage(CareerRoadmapStage.ACTIVE_JOB_APPLICATIONS)
                .title("9. Active Job Applications & Placements")
                .description("Apply to matching company hiring drives, on-campus placements, and track status.")
                .status(appPct >= 80 ? CareerRoadmapStatus.COMPLETED : appPct > 0 ? CareerRoadmapStatus.IN_PROGRESS : CareerRoadmapStatus.RECOMMENDED)
                .completionPercentage(appPct)
                .focusAreas(List.of("Job Portal", "Placement Drives", "Application Tracker"))
                .recommendedActionTitle("Apply to Jobs")
                .recommendedActionLink("/student/jobs")
                .build());

        int overall = (int) stages.stream().mapToInt(CareerRoadmapStageDto::getCompletionPercentage).average().orElse(0);

        return CareerRoadmapDto.builder()
                .targetRole("Java Full Stack Developer")
                .overallProgressPercentage(overall)
                .currentStageName(dsaPct < 50 ? "DSA Problem Solving" : atsScore < 70 ? "ATS Resume Perfection" : "Active Job Applications")
                .stages(stages)
                .currentSkillGaps(List.of("Microservices Circuit Breaker", "Docker Compose Deployment", "Kafka Messaging"))
                .primaryNextAction("Complete Scheduled Daily Tasks & Mock Assessment")
                .primaryNextActionLink("/student/career/daily-plan")
                .build();
    }
}
