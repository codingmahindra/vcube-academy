package com.vcube.academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.auth.LoginRequest;
import com.vcube.academy.dto.auth.RefreshTokenRequest;
import com.vcube.academy.dto.auth.RegisterRequest;
import com.vcube.academy.dto.course.CourseRequest;
import com.vcube.academy.dto.quiz.QuestionOptionRequest;
import com.vcube.academy.dto.quiz.QuestionRequest;
import com.vcube.academy.dto.quiz.StartQuizRequest;
import com.vcube.academy.dto.quiz.SubmitAnswerRequest;
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
public class VcubeAcademyEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CourseCategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseModuleRepository moduleRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicContentRepository topicContentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentToken;
    private String trainerToken;
    private String adminToken;
    private Long seededCourseId;
    private Long seededTopicId;

    private MockHttpServletRequestBuilder apiPost(String url) {
        return post(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiGet(String url) {
        return get(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiPut(String url) {
        return put(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiPatch(String url) {
        return patch(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
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

        if (!userRepository.existsByEmail("test_student@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Test Student")
                    .email("test_student@vcube.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .isActive(true)
                    .roles(Set.of(studentRole))
                    .build());
        }

        if (!userRepository.existsByEmail("test_trainer@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Test Trainer")
                    .email("test_trainer@vcube.com")
                    .password(passwordEncoder.encode("Trainer@123"))
                    .isActive(true)
                    .roles(Set.of(trainerRole))
                    .build());
        }

        if (!userRepository.existsByEmail("test_admin@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Test Admin")
                    .email("test_admin@vcube.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .isActive(true)
                    .roles(Set.of(adminRole))
                    .build());
        }

        CourseCategory cat = categoryRepository.findBySlug("core-java")
                .orElseGet(() -> categoryRepository.save(CourseCategory.builder()
                        .name("Core Java")
                        .slug("core-java")
                        .description("Master Core Java from scratch")
                        .displayOrder(1)
                        .isActive(true)
                        .build()));

        Course course = courseRepository.findBySlug("java-fundamentals")
                .orElseGet(() -> courseRepository.save(Course.builder()
                        .category(cat)
                        .title("Java Fundamentals & OOP")
                        .slug("java-fundamentals")
                        .description("Core Java comprehensive guide")
                        .difficulty("BEGINNER")
                        .isPublished(true)
                        .displayOrder(1)
                        .build()));
        seededCourseId = course.getId();

        CourseModule module = moduleRepository.findByCourseIdOrderByDisplayOrder(course.getId()).stream().findFirst()
                .orElseGet(() -> moduleRepository.save(CourseModule.builder()
                        .course(course)
                        .title("Introduction to Java")
                        .displayOrder(1)
                        .build()));

        Topic topic = topicRepository.findByModuleIdOrderByDisplayOrder(module.getId()).stream().findFirst()
                .orElseGet(() -> topicRepository.save(Topic.builder()
                        .module(module)
                        .title("Java JVM, JDK & JRE Architecture")
                        .slug("jvm-jdk-jre")
                        .difficulty("BEGINNER")
                        .displayOrder(1)
                        .isPublished(true)
                        .build()));
        seededTopicId = topic.getId();

        if (topicContentRepository.findByTopicId(topic.getId()).isEmpty()) {
            topicContentRepository.save(TopicContent.builder()
                    .topic(topic)
                    .explanation("The JVM is an abstract computing machine that enables a computer to run a Java program.")
                    .simpleExplanation("JVM runs bytecode, JRE provides libraries, JDK provides compiler.")
                    .syntaxExample("public class Main { public static void main(String[] args) {} }")
                    .codeExample("public class Hello { public static void main(String[] a){ System.out.println(\"Hi\"); }}")
                    .interviewPoints("JVM performs JIT compilation and garbage collection.")
                    .commonMistakes("Confusing JDK with JRE.")
                    .build());
        }

        if (questionRepository.findByTopicId(topic.getId()).isEmpty()) {
            Question q = Question.builder()
                    .topic(topic)
                    .course(course)
                    .questionText("Which component of Java platform executes the bytecode?")
                    .difficulty("EASY")
                    .explanation("JVM (Java Virtual Machine) is responsible for executing bytecode.")
                    .isActive(true)
                    .build();

            QuestionOption o1 = QuestionOption.builder().question(q).optionLabel("A").optionText("JDK").isCorrect(false).whyWrong("JDK is development kit.").build();
            QuestionOption o2 = QuestionOption.builder().question(q).optionLabel("B").optionText("JVM").isCorrect(true).build();
            QuestionOption o3 = QuestionOption.builder().question(q).optionLabel("C").optionText("JDB").isCorrect(false).whyWrong("JDB is debugger.").build();
            QuestionOption o4 = QuestionOption.builder().question(q).optionLabel("D").optionText("javadoc").isCorrect(false).whyWrong("javadoc is doc tool.").build();

            q.getOptions().addAll(List.of(o1, o2, o3, o4));
            questionRepository.save(q);
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

    // =========================================================================
    // 1. AUTHENTICATION TESTS
    // =========================================================================

    @Test
    @DisplayName("Auth: Register new student -> Login -> Fetch Me -> Refresh Token -> Logout")
    void testAuthenticationLifecycle() throws Exception {
        String uniqueEmail = "new_student_" + System.currentTimeMillis() + "@vcube.com";
        RegisterRequest registerReq = RegisterRequest.builder()
                .fullName("Full Stack Student")
                .email(uniqueEmail)
                .password("Password@123")
                .phone("9876543210")
                .build();

        // 1. Register
        MvcResult regResult = mockMvc.perform(apiPost("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.roles", hasItem("STUDENT")))
                .andReturn();

        String refreshToken = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("refreshToken").asText();

        // 2. Duplicate register fails (409 Conflict)
        mockMvc.perform(apiPost("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isConflict());

        // 3. Login with correct password
        LoginRequest loginReq = LoginRequest.builder().email(uniqueEmail).password("Password@123").build();
        MvcResult loginResult = mockMvc.perform(apiPost("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("accessToken").asText();
        String activeRefreshToken = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("refreshToken").asText();

        // 4. Login with invalid password fails (401)
        mockMvc.perform(apiPost("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoginRequest.builder().email(uniqueEmail).password("WrongPassword!").build())))
                .andExpect(status().isUnauthorized());

        // 5. Fetch /auth/me
        mockMvc.perform(apiGet("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.fullName").value("Full Stack Student"));

        // 6. Refresh token
        RefreshTokenRequest refreshReq = RefreshTokenRequest.builder().refreshToken(activeRefreshToken).build();
        mockMvc.perform(apiPost("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

        // 7. Logout
        mockMvc.perform(apiPost("/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    // =========================================================================
    // 2. STUDENT LEARNING & QUIZ FLOW
    // =========================================================================

    @Test
    @DisplayName("Student Flow: Browse courses -> Modules -> Topic Content -> Mark Complete -> Start Quiz -> Submit Answer -> Complete Quiz -> Progress")
    void testStudentCompleteLearningAndQuizFlow() throws Exception {
        studentToken = loginAndGetToken("test_student@vcube.com", "Student@123");

        // 1. Fetch categories
        mockMvc.perform(apiGet("/courses/categories")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 2. Fetch courses list
        mockMvc.perform(apiGet("/courses")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 3. Fetch course details
        mockMvc.perform(apiGet("/courses/" + seededCourseId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededCourseId))
                .andExpect(jsonPath("$.modules", hasSize(greaterThanOrEqualTo(1))));

        // 4. Fetch topic detail & content
        mockMvc.perform(apiGet("/topics/" + seededTopicId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededTopicId))
                .andExpect(jsonPath("$.content.explanation").isNotEmpty());

        // 5. Mark topic as completed
        mockMvc.perform(apiPost("/topics/" + seededTopicId + "/complete")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Topic marked as completed."));

        // 6. Check completion status
        mockMvc.perform(apiGet("/topics/" + seededTopicId + "/completion")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

        // 7. Start Topic Quiz
        StartQuizRequest startReq = StartQuizRequest.builder()
                .quizType("TOPIC_QUIZ")
                .topicId(seededTopicId)
                .build();

        MvcResult quizStartResult = mockMvc.perform(apiPost("/quiz/start")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attemptId").isNotEmpty())
                .andExpect(jsonPath("$.totalQuestions").value(greaterThanOrEqualTo(1)))
                .andReturn();

        Long attemptId = objectMapper.readTree(quizStartResult.getResponse().getContentAsString()).get("attemptId").asLong();

        // 8. SECURITY CHECK: Verify current question options do NOT expose correct answer flags!
        MvcResult qResult = mockMvc.perform(apiGet("/quiz/" + attemptId + "/question")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.options", hasSize(greaterThanOrEqualTo(2))))
                .andReturn();

        String questionJson = qResult.getResponse().getContentAsString();
        assertThat(questionJson).doesNotContain("\"isCorrect\"");
        assertThat(questionJson).doesNotContain("\"correct\"");

        Long questionId = objectMapper.readTree(questionJson).get("id").asLong();
        Long optionA_Id = objectMapper.readTree(questionJson).get("options").get(0).get("id").asLong();

        // 9. Submit a wrong answer (Option A = JDK)
        SubmitAnswerRequest wrongAnswer = SubmitAnswerRequest.builder()
                .questionId(questionId)
                .selectedOptionId(optionA_Id)
                .build();

        mockMvc.perform(apiPost("/quiz/" + attemptId + "/answer")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongAnswer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCorrect").value(false))
                .andExpect(jsonPath("$.correctOptionLabel").value("B"))
                .andExpect(jsonPath("$.explanation").isNotEmpty());

        // 10. Complete quiz
        mockMvc.perform(apiPost("/quiz/" + attemptId + "/complete")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptId").value(attemptId))
                .andExpect(jsonPath("$.totalQuestions").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.scorePercentage").isNumber());

        // 11. Fetch student overall stats
        mockMvc.perform(apiGet("/progress/stats")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTopicsCompleted").value(greaterThanOrEqualTo(1)));

        // 12. Fetch per-course progress
        mockMvc.perform(apiGet("/progress/courses")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 13. Fetch weak topics
        mockMvc.perform(apiGet("/progress/weak-topics")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // 3. TRAINER FLOW
    // =========================================================================

    @Test
    @DisplayName("Trainer Flow: Check Dashboard -> Create Course -> Add Question to Bank -> Delete Test Question")
    void testTrainerOperations() throws Exception {
        trainerToken = loginAndGetToken("test_trainer@vcube.com", "Trainer@123");

        // 1. Fetch trainer dashboard data
        mockMvc.perform(apiGet("/trainer/dashboard")
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCourses").isNumber())
                .andExpect(jsonPath("$.totalQuestions").isNumber());

        // 2. Create a Course
        CourseCategory cat = categoryRepository.findBySlug("core-java").orElseThrow();
        CourseRequest courseReq = CourseRequest.builder()
                .categoryId(cat.getId())
                .title("Advanced Spring Boot Microservices " + System.currentTimeMillis())
                .slug("spring-boot-ms-" + System.currentTimeMillis())
                .description("Build enterprise cloud applications")
                .difficulty("ADVANCED")
                .isPublished(true)
                .build();

        MvcResult courseResult = mockMvc.perform(apiPost("/courses")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        Long createdCourseId = objectMapper.readTree(courseResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. Add question to question bank
        QuestionRequest qReq = QuestionRequest.builder()
                .courseId(createdCourseId)
                .questionText("Which annotation defines a RESTful Controller in Spring Boot?")
                .difficulty("EASY")
                .explanation("@RestController is a specialized version of @Controller with @ResponseBody.")
                .options(List.of(
                        QuestionOptionRequest.builder().optionLabel("A").optionText("@Controller").isCorrect(false).build(),
                        QuestionOptionRequest.builder().optionLabel("B").optionText("@RestController").isCorrect(true).build(),
                        QuestionOptionRequest.builder().optionLabel("C").optionText("@Service").isCorrect(false).build(),
                        QuestionOptionRequest.builder().optionLabel("D").optionText("@Component").isCorrect(false).build()
                ))
                .build();

        MvcResult qRes = mockMvc.perform(apiPost("/questions")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.options", hasSize(4)))
                .andReturn();

        Long questionId = objectMapper.readTree(qRes.getResponse().getContentAsString()).get("id").asLong();

        // 4. Delete the question
        mockMvc.perform(apiDelete("/questions/" + questionId)
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isOk());

        // 5. Delete the course
        mockMvc.perform(apiDelete("/courses/" + createdCourseId)
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isNoContent());
    }

    // =========================================================================
    // 4. ADMIN FLOW & USER MANAGEMENT
    // =========================================================================

    @Test
    @DisplayName("Admin Flow: Check Admin Dashboard -> List Users -> Update Role -> Toggle Status")
    void testAdminOperations() throws Exception {
        adminToken = loginAndGetToken("test_admin@vcube.com", "Admin@123");

        // 1. Fetch admin dashboard
        mockMvc.perform(apiGet("/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").isNumber())
                .andExpect(jsonPath("$.totalTrainers").isNumber())
                .andExpect(jsonPath("$.totalAdmins").isNumber());

        // 2. List all users
        mockMvc.perform(apiGet("/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));

        // 3. Toggle user active status
        User student = userRepository.findByEmail("test_student@vcube.com").orElseThrow();
        mockMvc.perform(apiPatch("/admin/users/" + student.getId() + "/toggle-status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        // 4. Re-enable student status
        mockMvc.perform(apiPatch("/admin/users/" + student.getId() + "/toggle-status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));
    }

    // =========================================================================
    // 5. SECURITY & ROLE AUTHORIZATION CHECKS
    // =========================================================================

    @Test
    @DisplayName("Security: Role isolation, 401 on missing auth, 403 on forbidden role")
    void testSecurityEnforcement() throws Exception {
        studentToken = loginAndGetToken("test_student@vcube.com", "Student@123");
        trainerToken = loginAndGetToken("test_trainer@vcube.com", "Trainer@123");

        // 1. Unauthenticated request to protected endpoint -> 401
        mockMvc.perform(apiGet("/auth/me"))
                .andExpect(status().isUnauthorized());

        // 2. Student accessing Trainer Dashboard -> 403
        mockMvc.perform(apiGet("/trainer/dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        // 3. Student accessing Admin Dashboard -> 403
        mockMvc.perform(apiGet("/admin/dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        // 4. Trainer accessing Admin Dashboard -> 403
        mockMvc.perform(apiGet("/admin/dashboard")
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isForbidden());

        // 5. Student cannot create a course -> 403
        CourseCategory cat = categoryRepository.findBySlug("core-java").orElseThrow();
        mockMvc.perform(apiPost("/courses")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CourseRequest.builder().categoryId(cat.getId()).title("Hack").slug("hack").build())))
                .andExpect(status().isForbidden());
    }
}
