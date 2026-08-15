package com.vcube.academy.service;

import com.vcube.academy.dto.career.CopilotChatResponse;
import com.vcube.academy.dto.career.CopilotChatResponse.ActionRecommendation;
import com.vcube.academy.entity.User;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RuleBasedCareerAIService implements CareerAIService {

    private final StudentProgressRepository studentProgressRepository;
    private final CourseRepository courseRepository;
    private final DsaStudentProgressRepository dsaProgressRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeMissingSkillRepository missingSkillRepository;
    private final JobApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final DsaProblemRepository dsaProblemRepository;

    @Override
    public CopilotChatResponse generateCopilotResponse(User student, String userQuery, Long conversationId) {
        String q = userQuery == null ? "" : userQuery.toLowerCase(Locale.ROOT).trim();

        // 1. Fetch student contextual metrics
        int enrolledCount = studentProgressRepository.findByStudentIdWithCourse(student.getId()).size();
        int dsaSolved = (int) dsaProgressRepository.findByUserId(student.getId()).stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsSolved())).count();
        int mockCount = (int) mockInterviewRepository.countByUserIdAndStatus(student.getId(), com.vcube.academy.entity.MockInterviewStatus.COMPLETED);
        var primaryResume = resumeVersionRepository.findByProfileUserIdOrderByUpdatedAtDesc(student.getId())
                .stream().filter(v -> Boolean.TRUE.equals(v.getIsPrimary())).findFirst().orElse(null);
        int atsScore = primaryResume != null && primaryResume.getLatestAtsScore() != null ? primaryResume.getLatestAtsScore() : 0;
        var missingSkills = missingSkillRepository.findAll().stream()
                .filter(m -> m.getAnalysis() != null && m.getAnalysis().getVersion() != null &&
                        m.getAnalysis().getVersion().getProfile() != null &&
                        student.getId().equals(m.getAnalysis().getVersion().getProfile().getUser().getId()))
                .toList();

        List<ActionRecommendation> actions = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        if (q.contains("missing") || q.contains("skill gap") || q.contains("what skills")) {
            sb.append("### Personalized Skill Gap Analysis for ").append(student.getFullName()).append("\n\n");
            if (!missingSkills.isEmpty()) {
                sb.append("Based on your latest ATS resume scans against target job descriptions, you have gaps in:\n");
                for (var ms : missingSkills) {
                    sb.append("- **").append(ms.getSkillName()).append("**: ").append(ms.getWhyItMatters()).append("\n");
                }
                sb.append("\n**Actionable Roadmap:** Prioritize mastering these missing frameworks and database technologies in the Academy.");
            } else {
                sb.append("Your current profile shows strong alignment with standard Java Full Stack requirements. Keep practicing advanced Microservices, Docker, and Kafka.");
            }
            actions.add(ActionRecommendation.builder().label("Analyze ATS Resume").actionType("RESUME").link("/student/resume/analyzer").build());
            actions.add(ActionRecommendation.builder().label("Explore Course Catalog").actionType("COURSE").link("/student/courses").build());

        } else if (q.contains("ats") || q.contains("resume") || q.contains("low score") || q.contains("improve my resume")) {
            sb.append("### Resume & ATS Optimization Feedback\n\n");
            sb.append("Your current primary resume ATS score is **").append(atsScore).append("/100**.\n\n");
            sb.append("**Key Recommendations to Increase ATS Score:**\n");
            sb.append("1. **Quantify Achievements (STAR Method):** Replace vague statements like 'Worked on REST API' with 'Architected 15+ Spring Boot REST endpoints reducing latency by 35%'.\n");
            sb.append("2. **Incorporate Target JD Keywords:** Ensure keywords such as Spring Boot, PostgreSQL, Microservices, and JUnit are explicitly present.\n");
            sb.append("3. **Highlight Measurable Projects:** Emphasize real production-like projects with Docker Compose and service discovery.\n");
            actions.add(ActionRecommendation.builder().label("Open Resume Builder").actionType("RESUME").link("/student/resume/builder").build());
            actions.add(ActionRecommendation.builder().label("Scan Against Target JD").actionType("RESUME").link("/student/resume/analyzer").build());

        } else if (q.contains("dsa") || q.contains("problem") || q.contains("algorithm") || q.contains("data structure")) {
            sb.append("### DSA Preparation Status & Recommended Problems\n\n");
            sb.append("You have solved **").append(dsaSolved).append(" DSA problems** in VCUBE Academy.\n\n");
            sb.append("**Recommended Next DSA Topics:**\n");
            sb.append("- **Two Pointers & Sliding Window:** Crucial for array substring problems (TCS Digital, Amazon).\n");
            sb.append("- **Binary Search & Trees:** Standard technical round staple for product and high-package service companies.\n");
            sb.append("- **Dynamic Programming:** Focus on 0/1 Knapsack and Longest Common Subsequence.\n");
            actions.add(ActionRecommendation.builder().label("DSA Problem Arena").actionType("DSA").link("/student/dsa/problems").build());
            actions.add(ActionRecommendation.builder().label("DSA Submissions Tracker").actionType("DSA").link("/student/dsa/submissions").build());

        } else if (q.contains("mock") || q.contains("conduct a mock") || q.contains("interview result")) {
            sb.append("### Mock Interview Assessment\n\n");
            sb.append("You have completed **").append(mockCount).append(" mock interview sessions**.\n\n");
            sb.append("**Pro-Tips for Your Next Mock Interview:**\n");
            sb.append("- Structure answers starting with fundamental definition, followed by real-world use case and internal working.\n");
            sb.append("- For Java: Be prepared for JVM Memory Model, Garbage Collection, Spring Bean Lifecycle, and Transaction isolation levels.\n");
            actions.add(ActionRecommendation.builder().label("Start New Mock Interview").actionType("MOCK").link("/student/interview/mock").build());
            actions.add(ActionRecommendation.builder().label("Practice Question Bank").actionType("INTERVIEW").link("/student/interview/questions").build());

        } else if (q.contains("company") || q.contains("infosys") || q.contains("tcs") || q.contains("amazon") || q.contains("wipro") || q.contains("accenture")) {
            sb.append("### Company-Specific Preparation Strategy\n\n");
            sb.append("Top recruiters (TCS, Infosys, Amazon, Accenture) evaluate candidates on:\n");
            sb.append("1. **Online Assessment:** Aptitude + Technical MCQs (Java, SQL) + 2 Coding Problems.\n");
            sb.append("2. **Technical Round 1:** Core Java OOPs, Multithreading, Spring Boot annotations, and Database normalization/indexing.\n");
            sb.append("3. **Technical Round 2 / Managerial:** Project architecture, Microservices design, exception handling, and Agile methodologies.\n");
            actions.add(ActionRecommendation.builder().label("Company Question Vault").actionType("INTERVIEW").link("/student/interview/companies").build());
            actions.add(ActionRecommendation.builder().label("Placement Papers").actionType("INTERVIEW").link("/student/placement-papers").build());

        } else if (q.contains("job") || q.contains("matching") || q.contains("why am i not matching") || q.contains("apply")) {
            sb.append("### Job Market & Application Strategy\n\n");
            sb.append("VCUBE Academy Job Portal lists active hiring drives with verified eligibility requirements.\n\n");
            sb.append("- Ensure your Career Preferences are configured (Target Role, Locations, Work Mode).\n");
            sb.append("- Tailor your resume version specifically for each job before submitting.\n");
            actions.add(ActionRecommendation.builder().label("Browse Job Portal").actionType("JOB").link("/student/jobs").build());
            actions.add(ActionRecommendation.builder().label("Job Recommendations").actionType("JOB").link("/student/job-recommendations").build());
            actions.add(ActionRecommendation.builder().label("Application Tracker").actionType("JOB").link("/student/applications").build());

        } else {
            sb.append("### Hello ").append(student.getFullName()).append(", I am your AI Career Copilot!\n\n");
            sb.append("Here is a quick snapshot of your current academy progress:\n");
            sb.append("- **Courses Enrolled:** ").append(enrolledCount).append("\n");
            sb.append("- **DSA Problems Solved:** ").append(dsaSolved).append("\n");
            sb.append("- **Mock Interviews Completed:** ").append(mockCount).append("\n");
            sb.append("- **Primary Resume ATS Score:** ").append(atsScore).append("/100\n\n");
            sb.append("Ask me anything about Java Full Stack interview questions, skill gaps, company preparation, resume improvements, or today's practice roadmap!");
            actions.add(ActionRecommendation.builder().label("View Career Roadmap").actionType("COURSE").link("/student/career/roadmap").build());
            actions.add(ActionRecommendation.builder().label("Today's Daily Plan").actionType("COURSE").link("/student/career/daily-plan").build());
        }

        return CopilotChatResponse.builder()
                .conversationId(conversationId)
                .responseText(sb.toString())
                .recommendedActions(actions)
                .aiProvider("RULE_BASED")
                .build();
    }
}
