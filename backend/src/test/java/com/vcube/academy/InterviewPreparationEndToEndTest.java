package com.vcube.academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.auth.LoginRequest;
import com.vcube.academy.dto.interview.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class InterviewPreparationEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InterviewCategoryRepository categoryRepository;

    @Autowired
    private InterviewTopicRepository topicRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private InterviewQuestionRepository questionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentToken;
    private String trainerToken;
    private String adminToken;
    private Long seededQuestionId;
    private Long seededCompanyId;

    private MockHttpServletRequestBuilder apiPost(String url) {
        return post(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiGet(String url) {
        return get(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiPut(String url) {
        return put(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiDelete(String url) {
        return delete(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    @BeforeEach
    void setUp() {
        Role studentRole = roleRepository.findByName(RoleType.STUDENT)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.STUDENT).build()));
        Role trainerRole = roleRepository.findByName(RoleType.TRAINER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.TRAINER).build()));
        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ADMIN).build()));

        if (!userRepository.existsByEmail("interview_student@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Interview Student")
                    .email("interview_student@vcube.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .isActive(true)
                    .roles(Set.of(studentRole))
                    .build());
        }

        if (!userRepository.existsByEmail("interview_trainer@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Interview Trainer")
                    .email("interview_trainer@vcube.com")
                    .password(passwordEncoder.encode("Trainer@123"))
                    .isActive(true)
                    .roles(Set.of(trainerRole))
                    .build());
        }

        if (!userRepository.existsByEmail("interview_admin@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Interview Admin")
                    .email("interview_admin@vcube.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .isActive(true)
                    .roles(Set.of(adminRole))
                    .build());
        }

        InterviewCategory cat = categoryRepository.findBySlug("core-java")
                .orElseGet(() -> categoryRepository.save(InterviewCategory.builder()
                        .name("Core Java & OOP")
                        .slug("core-java")
                        .description("Java basics")
                        .build()));

        InterviewTopic topic = topicRepository.findBySlug("collections-framework")
                .orElseGet(() -> topicRepository.save(InterviewTopic.builder()
                        .category(cat)
                        .name("Java Collections Framework")
                        .slug("collections-framework")
                        .description("Collections")
                        .build()));

        Company company = companyRepository.findBySlug("tcs")
                .orElseGet(() -> companyRepository.save(Company.builder()
                        .name("Tata Consultancy Services")
                        .slug("tcs")
                        .build()));
        seededCompanyId = company.getId();

        if (questionRepository.findByTopicIdAndIsPublishedTrue(topic.getId()).isEmpty()) {
            InterviewQuestion q = questionRepository.save(InterviewQuestion.builder()
                    .topic(topic)
                    .questionText("How does HashMap work internally in Java?")
                    .questionType(InterviewQuestionType.CONCEPTUAL)
                    .difficulty(InterviewDifficulty.INTERMEDIATE)
                    .interviewRound(InterviewRoundType.ROUND_3_TECHNICAL)
                    .questionSource(QuestionSource.REPORTED_PLACEMENT_QUESTION)
                    .sourceReference("Reported in TCS & Infosys placement rounds")
                    .expectedAnswer("HashMap uses hashing with an array of Node buckets. Collisions are handled via separate chaining and treeified to Red-Black tree in Java 8.")
                    .explanation("Detailed HashMap explanation")
                    .evaluationKeywords("[\"hashing\", \"bucket\", \"collision\", \"linkedlist\", \"red-black tree\", \"treeify\"]")
                    .isPublished(true)
                    .build());
            seededQuestionId = q.getId();
        } else {
            seededQuestionId = questionRepository.findByTopicIdAndIsPublishedTrue(topic.getId()).get(0).getId();
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
    @DisplayName("Interview Preparation: Category browsing -> Company catalog -> Practice Evaluation -> Progress")
    void testStudentInterviewPracticeFlow() throws Exception {
        studentToken = loginAndGetToken("interview_student@vcube.com", "Student@123");

        // 1. Fetch categories
        mockMvc.perform(apiGet("/interview/categories")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 2. Fetch companies
        mockMvc.perform(apiGet("/interview/companies")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 3. Fetch company detail
        mockMvc.perform(apiGet("/interview/companies/" + seededCompanyId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededCompanyId));

        // 4. Fetch question detail
        MvcResult qDetail = mockMvc.perform(apiGet("/interview/questions/" + seededQuestionId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededQuestionId))
                .andExpect(jsonPath("$.questionSource").value("REPORTED_PLACEMENT_QUESTION"))
                .andReturn();

        String qJson = qDetail.getResponse().getContentAsString();
        assertThat(qJson).contains("questionText");

        // 5. Evaluate practice answer
        InterviewEvaluationRequest evalReq = InterviewEvaluationRequest.builder()
                .userAnswer("HashMap uses hashing and bucket array. When collision happens it uses linkedlist separate chaining and in Java 8 it converts to red-black tree if bucket size exceeds threshold.")
                .build();

        mockMvc.perform(apiPost("/interview/questions/" + seededQuestionId + "/evaluate")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evalReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(greaterThanOrEqualTo(60.0)))
                .andExpect(jsonPath("$.strengths", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.improvedAnswer").isNotEmpty());

        // 6. Check student progress
        mockMvc.perform(apiGet("/interview/progress")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedQuestions").value(greaterThanOrEqualTo(1)));

        // 7. Check recommendations
        mockMvc.perform(apiGet("/interview/recommendations")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedRevisionTopics").isArray());
    }

    @Test
    @DisplayName("Mock Interview Engine: Start Mock -> Step-by-Step Answer -> Complete -> Comprehensive Report")
    void testMockInterviewFlow() throws Exception {
        studentToken = loginAndGetToken("interview_student@vcube.com", "Student@123");

        // 1. Start mock interview
        MockInterviewStartRequest startReq = MockInterviewStartRequest.builder()
                .roleTitle("Java Full Stack Developer")
                .totalQuestions(2)
                .interviewType("TECHNICAL")
                .difficulty("INTERMEDIATE")
                .build();

        MvcResult startRes = mockMvc.perform(apiPost("/interview/mock/start")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.questions", hasSize(greaterThanOrEqualTo(1))))
                .andReturn();

        Long mockId = objectMapper.readTree(startRes.getResponse().getContentAsString()).get("id").asLong();

        // 2. Answer question 1
        MockInterviewAnswerRequest ansReq = MockInterviewAnswerRequest.builder()
                .questionOrder(1)
                .userAnswer("In Java, HashMap works by calculating the hash code of the key and determining the bucket index. When collisions occur, separate chaining is used with LinkedList and Red-Black tree.")
                .timeTakenSeconds(45)
                .build();

        mockMvc.perform(apiPost("/interview/mock/" + mockId + "/answer")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ansReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").isNumber())
                .andExpect(jsonPath("$.userAnswer").isNotEmpty());

        // 3. Complete mock interview
        mockMvc.perform(apiPost("/interview/mock/" + mockId + "/complete")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallScore").isNumber())
                .andExpect(jsonPath("$.recommendationStatus").isNotEmpty())
                .andExpect(jsonPath("$.interviewReadinessPercentage").isNumber())
                .andExpect(jsonPath("$.feedbackSummary").isNotEmpty());

        // 4. Retrieve mock interview history
        mockMvc.perform(apiGet("/interview/mock")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Trainer & Admin Management and RBAC Security")
    void testTrainerAndAdminInterviewOperations() throws Exception {
        trainerToken = loginAndGetToken("interview_trainer@vcube.com", "Trainer@123");
        adminToken = loginAndGetToken("interview_admin@vcube.com", "Admin@123");
        studentToken = loginAndGetToken("interview_student@vcube.com", "Student@123");

        InterviewTopic topic = topicRepository.findBySlug("collections-framework").orElseThrow();

        // 1. Trainer create interview question
        InterviewQuestionAdminRequest createQ = InterviewQuestionAdminRequest.builder()
                .topicId(topic.getId())
                .questionText("What is the difference between SynchronizedList and CopyOnWriteArrayList?")
                .questionType(InterviewQuestionType.CONCEPTUAL)
                .difficulty(InterviewDifficulty.ADVANCED)
                .interviewRound(InterviewRoundType.ROUND_3_TECHNICAL)
                .questionSource(QuestionSource.REPORTED_PLACEMENT_QUESTION)
                .sourceReference("Reported in Morgan Stanley technical round")
                .expectedAnswer("SynchronizedList locks the entire collection on every read/write. CopyOnWriteArrayList creates a fresh copy on every mutation, offering fast lock-free reads.")
                .explanation("CopyOnWriteArrayList is best suited for read-heavy scenarios.")
                .evaluationKeywords("[\"synchronizedlist\", \"copyonwritearraylist\", \"lock-free\", \"read-heavy\", \"mutation\"]")
                .isPublished(true)
                .build();

        MvcResult createQRes = mockMvc.perform(apiPost("/trainer/interview/questions")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createQ)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        Long newQId = objectMapper.readTree(createQRes.getResponse().getContentAsString()).get("id").asLong();

        // 2. Admin create company
        CompanyAdminRequest createComp = CompanyAdminRequest.builder()
                .name("Microsoft Corporation " + System.currentTimeMillis())
                .slug("microsoft-" + System.currentTimeMillis())
                .description("Cloud and enterprise platforms")
                .industry("Product")
                .tier("TIER_1")
                .build();

        MvcResult compRes = mockMvc.perform(apiPost("/admin/interview/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createComp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        Long newCompId = objectMapper.readTree(compRes.getResponse().getContentAsString()).get("id").asLong();

        // 3. Admin view dashboard stats
        mockMvc.perform(apiGet("/admin/interview/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuestions").isNumber());

        // 4. Student forbidden from creating questions or companies
        mockMvc.perform(apiPost("/trainer/interview/questions")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createQ)))
                .andExpect(status().isForbidden());

        mockMvc.perform(apiPost("/admin/interview/companies")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createComp)))
                .andExpect(status().isForbidden());

        // 5. Cleanup
        mockMvc.perform(apiDelete("/trainer/interview/questions/" + newQId)
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isOk());

        mockMvc.perform(apiDelete("/admin/interview/companies/" + newCompId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
