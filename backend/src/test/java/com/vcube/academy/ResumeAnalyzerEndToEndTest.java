package com.vcube.academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.auth.LoginRequest;
import com.vcube.academy.dto.resume.*;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ResumeAnalyzerEndToEndTest {

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
    private JobRepository jobRepository;

    private String student1Token;
    private String student2Token;
    private String trainerToken;
    private String adminToken;

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

        if (!userRepository.existsByEmail("resume_student1@vcube.com")) {
            userRepository.save(User.builder()
                    .email("resume_student1@vcube.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .fullName("Resume Student One")
                    .isActive(true)
                    .roles(Set.of(studentRole))
                    .build());
        }

        if (!userRepository.existsByEmail("resume_student2@vcube.com")) {
            userRepository.save(User.builder()
                    .email("resume_student2@vcube.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .fullName("Resume Student Two")
                    .isActive(true)
                    .roles(Set.of(studentRole))
                    .build());
        }

        if (!userRepository.existsByEmail("resume_trainer@vcube.com")) {
            userRepository.save(User.builder()
                    .email("resume_trainer@vcube.com")
                    .password(passwordEncoder.encode("Trainer@123"))
                    .fullName("Resume Trainer")
                    .isActive(true)
                    .roles(Set.of(trainerRole))
                    .build());
        }

        if (!userRepository.existsByEmail("resume_admin@vcube.com")) {
            userRepository.save(User.builder()
                    .email("resume_admin@vcube.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("Resume Admin")
                    .isActive(true)
                    .roles(Set.of(adminRole))
                    .build());
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
    @DisplayName("End-to-End: Resume Creation, ATS Analysis, Skill Gap Resolution, and PDF Download")
    void testResumeCreationAndAtsAnalysisFlow() throws Exception {
        student1Token = loginAndGetToken("resume_student1@vcube.com", "Student@123");

        // 1. Create Resume Version
        ResumeDataRequest request = ResumeDataRequest.builder()
                .fullName("Resume Student One")
                .email("resume_student1@vcube.com")
                .phone("+91 99887 76655")
                .location("Hyderabad, India")
                .versionTitle("Java Developer — Enterprise")
                .targetRole("Java Full Stack Engineer")
                .targetCompany("Tata Consultancy Services")
                .professionalSummary("Enthusiastic Java Developer experienced in Java 17, Spring Boot, Microservices, and PostgreSQL.")
                .template(ResumeTemplate.JAVA_FULLSTACK)
                .technicalSkills(List.of("Java 17", "Spring Boot", "PostgreSQL", "REST APIs"))
                .experiences(List.of(
                        ResumeExperienceDto.builder()
                                .companyName("VCUBE Tech Labs")
                                .roleTitle("Junior Backend Developer")
                                .startDate("Jan 2024")
                                .endDate("Present")
                                .isCurrent(true)
                                .description("Developed core REST services")
                                .bulletPoints(List.of(
                                        "Architected 10+ REST microservices with Spring Boot and PostgreSQL",
                                        "Optimized SQL queries reducing latency by 30%"
                                ))
                                .build()
                ))
                .educations(List.of(
                        ResumeEducationDto.builder()
                                .institution("JNTU Hyderabad")
                                .degree("B.Tech")
                                .fieldOfStudy("Computer Science")
                                .startYear("2020")
                                .endYear("2024")
                                .scoreOrCgpa("8.5 CGPA")
                                .build()
                ))
                .projects(List.of(
                        ResumeProjectDto.builder()
                                .title("Fintech Payment Engine")
                                .techStack("Java 17, Spring Boot, PostgreSQL, Docker")
                                .description("High throughput payment processing gateway")
                                .bulletPoints(List.of("Containerized distributed services using Docker"))
                                .build()
                ))
                .isPrimary(true)
                .build();

        MvcResult createResult = mockMvc.perform(apiPost("/student/resume/versions")
                        .header("Authorization", "Bearer " + student1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.versionTitle", is("Java Developer — Enterprise")))
                .andExpect(jsonPath("$.fullName", is("Resume Student One")))
                .andReturn();

        ResumeVersionDetailDto createdVersion = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ResumeVersionDetailDto.class);
        Long versionId = createdVersion.getId();

        // 2. Perform ATS Analysis against Job Description
        ResumeAnalyzeRequest analyzeReq = ResumeAnalyzeRequest.builder()
                .versionId(versionId)
                .jobDescriptionText("Looking for a Java Full Stack Developer with Java 17, Spring Boot, Microservices, PostgreSQL, Docker, and Kafka.")
                .targetRole("Java Full Stack Engineer")
                .targetCompany("Tata Consultancy Services")
                .build();

        mockMvc.perform(apiPost("/student/resume/analyze")
                        .header("Authorization", "Bearer " + student1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(analyzeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallAtsScore", greaterThan(50)))
                .andExpect(jsonPath("$.keywordMatchScore", greaterThan(0)))
                .andExpect(jsonPath("$.matchedKeywords", not(empty())))
                .andExpect(jsonPath("$.criticalMissingSkills", not(empty())));

        // 3. Optimization suggestions
        mockMvc.perform(apiGet("/student/resume/versions/" + versionId + "/optimize")
                        .header("Authorization", "Bearer " + student1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optimizedSummary").exists())
                .andExpect(jsonPath("$.recommendedActionVerbs", not(empty())));

        // 4. Download PDF Resume
        mockMvc.perform(apiGet("/student/resume/versions/" + versionId + "/pdf")
                        .header("Authorization", "Bearer " + student1Token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"));
    }

    @Test
    @DisplayName("Security Isolation: Student A cannot access Student B's resume version")
    void testStudentDataIsolation() throws Exception {
        student1Token = loginAndGetToken("resume_student1@vcube.com", "Student@123");
        student2Token = loginAndGetToken("resume_student2@vcube.com", "Student@123");

        // Create resume for student 1
        ResumeDataRequest request = ResumeDataRequest.builder()
                .fullName("Private Student 1")
                .email("resume_student1@vcube.com")
                .versionTitle("Confidential Resume")
                .build();

        MvcResult createResult = mockMvc.perform(apiPost("/student/resume/versions")
                        .header("Authorization", "Bearer " + student1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeVersionDetailDto version = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ResumeVersionDetailDto.class);

        // Student 2 attempts to fetch Student 1's version -> 403 Forbidden
        mockMvc.perform(apiGet("/student/resume/versions/" + version.getId())
                        .header("Authorization", "Bearer " + student2Token))
                .andExpect(status().isForbidden());

        // Unauthenticated access -> 401 Unauthorized
        mockMvc.perform(apiGet("/student/resume/versions/" + version.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("RBAC: Trainer and Admin Analytics Endpoints")
    void testTrainerAndAdminAnalytics() throws Exception {
        student1Token = loginAndGetToken("resume_student1@vcube.com", "Student@123");
        trainerToken = loginAndGetToken("resume_trainer@vcube.com", "Trainer@123");
        adminToken = loginAndGetToken("resume_admin@vcube.com", "Admin@123");

        // Trainer can access trainer stats
        mockMvc.perform(apiGet("/trainer/resume/stats")
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResumesCreated").exists());

        // Admin can access admin analytics
        mockMvc.perform(apiGet("/admin/resume/analytics")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResumesCreated").exists());

        // Student forbidden from admin analytics
        mockMvc.perform(apiGet("/admin/resume/analytics")
                        .header("Authorization", "Bearer " + student1Token))
                .andExpect(status().isForbidden());
    }
}
