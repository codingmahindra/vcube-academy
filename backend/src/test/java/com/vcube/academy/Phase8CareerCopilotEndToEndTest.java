package com.vcube.academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.auth.LoginRequest;
import com.vcube.academy.dto.career.CopilotChatRequest;
import com.vcube.academy.dto.career.PlacementPaperAnswerRequest;
import com.vcube.academy.entity.*;
import com.vcube.academy.repository.PlacementPaperQuestionRepository;
import com.vcube.academy.repository.PlacementPaperRepository;
import com.vcube.academy.repository.RoleRepository;
import com.vcube.academy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("default")
public class Phase8CareerCopilotEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlacementPaperRepository placementPaperRepository;

    @Autowired
    private PlacementPaperQuestionRepository placementPaperQuestionRepository;

    private String studentToken;
    private String trainerToken;
    private String adminToken;
    private Long testPaperId;
    private Long testQuestionId;

    private MockHttpServletRequestBuilder apiPost(String url) {
        return post(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiGet(String url) {
        return get(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    @BeforeEach
    void setUp() throws Exception {
        Role studentRole = roleRepository.findByName(RoleType.STUDENT)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.STUDENT).build()));
        Role trainerRole = roleRepository.findByName(RoleType.TRAINER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.TRAINER).build()));
        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ADMIN).build()));

        if (!userRepository.existsByEmail("copilot_student@vcube.com")) {
            userRepository.save(User.builder()
                    .email("copilot_student@vcube.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .fullName("Copilot Student")
                    .isActive(true)
                    .roles(Set.of(studentRole))
                    .build());
        }

        if (!userRepository.existsByEmail("copilot_trainer@vcube.com")) {
            userRepository.save(User.builder()
                    .email("copilot_trainer@vcube.com")
                    .password(passwordEncoder.encode("Trainer@123"))
                    .fullName("Copilot Trainer")
                    .isActive(true)
                    .roles(Set.of(trainerRole))
                    .build());
        }

        if (!userRepository.existsByEmail("copilot_admin@vcube.com")) {
            userRepository.save(User.builder()
                    .email("copilot_admin@vcube.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("Copilot Admin")
                    .isActive(true)
                    .roles(Set.of(adminRole))
                    .build());
        }

        studentToken = loginAndGetToken("copilot_student@vcube.com", "Student@123");
        trainerToken = loginAndGetToken("copilot_trainer@vcube.com", "Trainer@123");
        adminToken = loginAndGetToken("copilot_admin@vcube.com", "Admin@123");

        if (placementPaperRepository.count() == 0) {
            PlacementPaper paper = placementPaperRepository.save(PlacementPaper.builder()
                    .title("TCS NQT National Placement Paper 2024 (Cognitive + Technical)")
                    .slug("tcs-nqt-2024-placement-paper")
                    .year("2024")
                    .targetRole("Graduate Software Engineer / Ninja & Digital")
                    .roundName("Round 1: Online Assessment")
                    .durationMinutes(60)
                    .totalMarks(100)
                    .passingMarks(60)
                    .difficulty(InterviewDifficulty.INTERMEDIATE)
                    .paperSource(PlacementPaperSource.VERIFIED)
                    .instructions("Comprehensive assessment covering Quantitative Aptitude, Logical Reasoning, Core Java, SQL queries, and Data Structures.")
                    .isActive(true)
                    .build());

            PlacementPaperQuestion q = placementPaperQuestionRepository.save(PlacementPaperQuestion.builder()
                    .paper(paper)
                    .sectionName("JAVA")
                    .questionText("What will be the output when executing: List<Integer> list = List.of(1, 2, 3); list.add(4); in Java 17?")
                    .optionA("List containing [1, 2, 3, 4]")
                    .optionB("UnsupportedOperationException at runtime")
                    .optionC("Compilation error")
                    .optionD("NullPointerException")
                    .correctOption("B")
                    .explanation("List.of() produces an immutable list implementation.")
                    .marks(2)
                    .displayOrder(1)
                    .build());

            testPaperId = paper.getId();
            testQuestionId = q.getId();
        } else {
            PlacementPaper paper = placementPaperRepository.findAll().get(0);
            testPaperId = paper.getId();
            var questions = placementPaperQuestionRepository.findByPaperIdOrderByDisplayOrderAsc(testPaperId);
            if (!questions.isEmpty()) {
                testQuestionId = questions.get(0).getId();
            } else {
                PlacementPaperQuestion q = placementPaperQuestionRepository.save(PlacementPaperQuestion.builder()
                        .paper(paper)
                        .sectionName("JAVA")
                        .questionText("What will be the output when executing: List<Integer> list = List.of(1, 2, 3); list.add(4); in Java 17?")
                        .optionA("List containing [1, 2, 3, 4]")
                        .optionB("UnsupportedOperationException at runtime")
                        .optionC("Compilation error")
                        .optionD("NullPointerException")
                        .correctOption("B")
                        .explanation("List.of() produces an immutable list implementation.")
                        .marks(2)
                        .displayOrder(1)
                        .build());
                testQuestionId = q.getId();
            }
        }
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        LoginRequest req = LoginRequest.builder().email(email).password(password).build();
        MvcResult res = mockMvc.perform(apiPost("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        String json = res.getResponse().getContentAsString();
        return objectMapper.readTree(json).get("accessToken").asText();
    }

    @Test
    @DisplayName("1. Career Dashboard & Personalized Roadmap API Endpoints")
    void testCareerDashboardAndRoadmap() throws Exception {
        // GET /api/student/career/dashboard
        mockMvc.perform(apiGet("/student/career/dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileCompletionPercentage", greaterThanOrEqualTo(50)))
                .andExpect(jsonPath("$.interviewReadinessStatus", notNullValue()))
                .andExpect(jsonPath("$.currentRoadmapStage", notNullValue()));

        // GET /api/student/career/roadmap
        mockMvc.perform(apiGet("/student/career/roadmap")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetRole", is("Java Full Stack Developer")))
                .andExpect(jsonPath("$.stages", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$.stages[0].stage", is("FOUNDATION")))
                .andExpect(jsonPath("$.stages[0].status", is("COMPLETED")));
    }

    @Test
    @DisplayName("2. AI Career Copilot Context-Aware Dialogue & Rule-Based Fallback")
    void testCareerCopilotChat() throws Exception {
        CopilotChatRequest chatReq = CopilotChatRequest.builder()
                .message("What skills am I missing for Java Developer roles?")
                .build();

        mockMvc.perform(apiPost("/student/career/copilot/chat")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseText", containsString("Personalized Skill Gap Analysis")))
                .andExpect(jsonPath("$.aiProvider", is("RULE_BASED")))
                .andExpect(jsonPath("$.recommendedActions", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("3. Placement Papers: List, Detail, Start Attempt, Submit Answer, and Complete")
    void testPlacementPapersLifecycle() throws Exception {
        // 1. List papers
        mockMvc.perform(apiGet("/placement-papers")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 2. Start attempt for Paper
        MvcResult attemptResult = mockMvc.perform(apiPost("/placement-papers/" + testPaperId + "/attempt")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.questions", hasSize(greaterThanOrEqualTo(1))))
                .andReturn();

        Long attemptId = objectMapper.readTree(attemptResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. Submit answer to question
        PlacementPaperAnswerRequest answerReq = PlacementPaperAnswerRequest.builder()
                .attemptId(attemptId)
                .questionId(testQuestionId)
                .selectedOption("B") // Correct option for Q1
                .timeTakenSeconds(15)
                .build();

        mockMvc.perform(apiPost("/placement-papers/" + testPaperId + "/answer")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerReq)))
                .andExpect(status().isOk());

        // 4. Complete attempt
        mockMvc.perform(apiPost("/placement-papers/" + testPaperId + "/complete?attemptId=" + attemptId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctAnswers", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.percentage", notNullValue()))
                .andExpect(jsonPath("$.sectionScores", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("4. Daily Preparation Plan & Weak Area Engine")
    void testDailyPlanAndWeakAreas() throws Exception {
        // GET /api/student/career/daily-plan
        MvcResult planResult = mockMvc.perform(apiGet("/student/career/daily-plan")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(greaterThanOrEqualTo(5))))
                .andReturn();

        Long firstTaskId = objectMapper.readTree(planResult.getResponse().getContentAsString())
                .get("items").get(0).get("id").asLong();

        // Toggle task
        mockMvc.perform(apiPost("/student/career/daily-plan/toggle/" + firstTaskId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedTasks", greaterThanOrEqualTo(1)));

        // Weak areas
        mockMvc.perform(apiGet("/student/career/weak-areas")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("5. RBAC & Security Isolation on Career & Placement Endpoints")
    void testCareerSecurityAndIsolation() throws Exception {
        // Unauthenticated access -> 401
        mockMvc.perform(apiGet("/student/career/dashboard"))
                .andExpect(status().isUnauthorized());

        // Student accessing Admin Career Analytics -> 403
        mockMvc.perform(apiGet("/admin/career/analytics")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        // Trainer accessing Trainer Career Stats -> 200
        mockMvc.perform(apiGet("/trainer/career/stats")
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlacementPapers", notNullValue()));

        // Admin accessing Admin Career Analytics -> 200
        mockMvc.perform(apiGet("/admin/career/analytics")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageAtsScore", notNullValue()));
    }
}
