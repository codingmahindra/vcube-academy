package com.vcube.academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.auth.LoginRequest;
import com.vcube.academy.dto.job.*;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JobPlacementEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobCategoryRepository categoryRepository;

    @Autowired
    private JobSkillRepository skillRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlacementDriveRepository driveRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentToken;
    private String trainerToken;
    private String adminToken;
    private Long seededJobId;
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

        if (!userRepository.existsByEmail("job_student@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Job Student")
                    .email("job_student@vcube.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .isActive(true)
                    .roles(Set.of(studentRole))
                    .build());
        }

        if (!userRepository.existsByEmail("job_trainer@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Job Trainer")
                    .email("job_trainer@vcube.com")
                    .password(passwordEncoder.encode("Trainer@123"))
                    .isActive(true)
                    .roles(Set.of(trainerRole))
                    .build());
        }

        if (!userRepository.existsByEmail("job_admin@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("Job Admin")
                    .email("job_admin@vcube.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .isActive(true)
                    .roles(Set.of(adminRole))
                    .build());
        }

        Company company = companyRepository.findBySlug("tcs")
                .orElseGet(() -> companyRepository.save(Company.builder()
                        .name("Tata Consultancy Services")
                        .slug("tcs")
                        .build()));
        seededCompanyId = company.getId();

        JobCategory category = categoryRepository.findBySlug("java-backend")
                .orElseGet(() -> categoryRepository.save(JobCategory.builder()
                        .name("Java Backend Development")
                        .slug("java-backend")
                        .build()));

        if (jobRepository.findByCompanyIdAndIsActiveTrue(company.getId()).isEmpty()) {
            Job job = jobRepository.save(Job.builder()
                    .company(company)
                    .category(category)
                    .title("Graduate Java Developer")
                    .slug("tcs-graduate-java-developer")
                    .description("Developing core Java microservices")
                    .location("Hyderabad, India")
                    .employmentType(EmploymentType.FULL_TIME)
                    .experienceLevel(ExperienceLevel.FRESHER)
                    .workMode(WorkMode.HYBRID)
                    .salaryMin(new BigDecimal("400000.00"))
                    .salaryMax(new BigDecimal("700000.00"))
                    .salaryText("4.0 - 7.0 LPA")
                    .source(JobSource.COMPANY_CAREER_PAGE)
                    .sourceUrl("https://ibegin.tcs.com")
                    .postedDate(Instant.now())
                    .applicationDeadline(Instant.now().plusSeconds(30L * 24 * 3600))
                    .isActive(true)
                    .build());
            seededJobId = job.getId();
        } else {
            seededJobId = jobRepository.findByCompanyIdAndIsActiveTrue(company.getId()).get(0).getId();
        }

        if (driveRepository.count() == 0) {
            driveRepository.save(PlacementDrive.builder()
                    .company(company)
                    .title("TCS National Qualifier Test (NQT)")
                    .description("National hiring drive for fresh graduates")
                    .location("Pan-India (Virtual + In-Person)")
                    .driveDate(Instant.now().plusSeconds(14L * 24 * 3600))
                    .registrationDeadline(Instant.now().plusSeconds(7L * 24 * 3600))
                    .packageDetails("3.6 - 7.2 LPA")
                    .eligibilityCriteria("BE/B.Tech/MCA/M.Tech (All branches) with minimum 60% aggregate")
                    .selectionProcess("Round 1: Cognitive + Technical NQT | Round 2: Technical Interview | Round 3: HR Interview")
                    .applicationLink("https://learning.tcsionhub.in/hub/national-qualifier-test/")
                    .status(PlacementDriveStatus.UPCOMING)
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
    @DisplayName("Job Portal: Search & Filters -> Detail -> Save -> Matching & Preparation")
    void testStudentJobDiscoveryAndSaveFlow() throws Exception {
        studentToken = loginAndGetToken("job_student@vcube.com", "Student@123");

        // 1. Search jobs
        mockMvc.perform(apiGet("/jobs")
                        .param("keyword", "Java")
                        .param("location", "Hyderabad")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));

        // 2. Fetch categories, locations, skills
        mockMvc.perform(apiGet("/jobs/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(apiGet("/jobs/locations"))
                .andExpect(status().isOk());

        mockMvc.perform(apiGet("/jobs/skills"))
                .andExpect(status().isOk());

        // 3. Get job detail with deterministic matching and roadmap
        MvcResult detailRes = mockMvc.perform(apiGet("/jobs/" + seededJobId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededJobId))
                .andExpect(jsonPath("$.source").value("COMPANY_CAREER_PAGE"))
                .andExpect(jsonPath("$.matchResult.matchPercentage").isNumber())
                .andExpect(jsonPath("$.preparationRoadmap.technicalChecklist").isArray())
                .andReturn();

        String json = detailRes.getResponse().getContentAsString();
        assertThat(json).contains("preparationRoadmap");

        // 4. Save job
        mockMvc.perform(apiPost("/student/jobs/" + seededJobId + "/save")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobId").value(seededJobId));

        // 5. Get saved jobs list
        mockMvc.perform(apiGet("/student/saved-jobs")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));

        // 6. Unsave job
        mockMvc.perform(apiDelete("/student/jobs/" + seededJobId + "/save")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Application Tracker: Apply -> Timeline -> Status History -> Dashboard")
    void testApplicationLifecycleFlow() throws Exception {
        studentToken = loginAndGetToken("job_student@vcube.com", "Student@123");

        // 1. Create application
        JobApplicationRequest applyReq = JobApplicationRequest.builder()
                .jobId(seededJobId)
                .status(ApplicationStatus.APPLIED)
                .notes("Applied through TCS iBegin portal with updated Java resume")
                .nextAction("Prepare for online assessment")
                .build();

        MvcResult createRes = mockMvc.perform(apiPost("/student/applications")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andReturn();

        Long appId = objectMapper.readTree(createRes.getResponse().getContentAsString()).get("id").asLong();

        // 2. Update status to ASSESSMENT then INTERVIEW
        JobApplicationRequest updateReq = JobApplicationRequest.builder()
                .jobId(seededJobId)
                .status(ApplicationStatus.INTERVIEW)
                .notes("Cleared online test! Technical interview scheduled")
                .nextAction("Revise Core Java Collections and SQL Indexing")
                .interviewDate(Instant.now().plusSeconds(7L * 24 * 3600))
                .build();

        mockMvc.perform(apiPut("/student/applications/" + appId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEW"))
                .andExpect(jsonPath("$.statusHistories", hasSize(greaterThanOrEqualTo(2))));

        // 3. Application detail
        mockMvc.perform(apiGet("/student/applications/" + appId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appId));

        // 4. Application dashboard counters
        mockMvc.perform(apiGet("/student/applications/dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.interviewCount").value(greaterThanOrEqualTo(1)));

        // 5. Delete application
        mockMvc.perform(apiDelete("/student/applications/" + appId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Placement Drives, Career Preferences & Trainer/Admin Role Isolation")
    void testPlacementsAndRBACSecurity() throws Exception {
        studentToken = loginAndGetToken("job_student@vcube.com", "Student@123");
        trainerToken = loginAndGetToken("job_trainer@vcube.com", "Trainer@123");
        adminToken = loginAndGetToken("job_admin@vcube.com", "Admin@123");

        // 1. Placement drives listing
        mockMvc.perform(apiGet("/placements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 2. Student configure career preferences
        StudentJobPreferenceDto prefDto = StudentJobPreferenceDto.builder()
                .preferredRoles(List.of("Java Developer", "Backend SDE"))
                .preferredLocations(List.of("Hyderabad", "Bangalore"))
                .preferredTechnologies(List.of("Java", "Spring Boot", "SQL", "Microservices"))
                .experienceLevel(ExperienceLevel.FRESHER)
                .workMode(WorkMode.HYBRID)
                .employmentType(EmploymentType.FULL_TIME)
                .build();

        mockMvc.perform(apiPut("/student/job-preferences")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prefDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferredRoles", hasSize(2)));

        // 3. Student recommendations
        mockMvc.perform(apiGet("/student/job-recommendations")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedJobs").isArray());

        // 4. Trainer create job
        JobAdminRequest newJob = JobAdminRequest.builder()
                .companyId(seededCompanyId)
                .title("Software Engineer - Core Java")
                .slug("tcs-se-core-java-" + System.currentTimeMillis())
                .description("Developing enterprise applications")
                .location("Hyderabad, India")
                .employmentType(EmploymentType.FULL_TIME)
                .experienceLevel(ExperienceLevel.FRESHER)
                .workMode(WorkMode.HYBRID)
                .salaryMin(new BigDecimal("500000.00"))
                .salaryMax(new BigDecimal("800000.00"))
                .source(JobSource.COMPANY_CAREER_PAGE)
                .sourceUrl("https://ibegin.tcs.com")
                .isActive(true)
                .build();

        MvcResult createJobRes = mockMvc.perform(apiPost("/trainer/jobs")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newJob)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        Long createdJobId = objectMapper.readTree(createJobRes.getResponse().getContentAsString()).get("id").asLong();

        // 5. Admin create placement drive
        PlacementDriveAdminRequest driveReq = PlacementDriveAdminRequest.builder()
                .companyId(seededCompanyId)
                .title("TCS Special Campus Drive " + System.currentTimeMillis())
                .description("Exclusive campus recruitment drive")
                .location("Online")
                .driveDate(Instant.now().plusSeconds(14L * 24 * 3600))
                .registrationDeadline(Instant.now().plusSeconds(7L * 24 * 3600))
                .packageDetails("4.0 - 7.0 LPA")
                .status(PlacementDriveStatus.UPCOMING)
                .build();

        MvcResult driveRes = mockMvc.perform(apiPost("/admin/placements")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driveReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        Long createdDriveId = objectMapper.readTree(driveRes.getResponse().getContentAsString()).get("id").asLong();

        // 6. Admin analytics
        mockMvc.perform(apiGet("/admin/jobs/analytics")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActiveJobs").isNumber());

        // 7. Security: Student forbidden from trainer and admin endpoints
        mockMvc.perform(apiPost("/trainer/jobs")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newJob)))
                .andExpect(status().isForbidden());

        mockMvc.perform(apiPost("/admin/placements")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driveReq)))
                .andExpect(status().isForbidden());

        // 8. Cleanup
        mockMvc.perform(apiDelete("/admin/jobs/" + createdJobId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(apiDelete("/admin/placements/" + createdDriveId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
