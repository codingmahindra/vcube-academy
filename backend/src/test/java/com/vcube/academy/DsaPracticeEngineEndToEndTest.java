package com.vcube.academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.auth.LoginRequest;
import com.vcube.academy.dto.dsa.DsaProblemRequest;
import com.vcube.academy.dto.dsa.DsaSubmissionRequest;
import com.vcube.academy.dto.dsa.DsaTestCaseRequest;
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
public class DsaPracticeEngineEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DsaCategoryRepository categoryRepository;

    @Autowired
    private DsaProblemRepository problemRepository;

    @Autowired
    private DsaTestCaseRepository testCaseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentToken;
    private String trainerToken;
    private String adminToken;
    private Long seededProblemId;

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

        if (!userRepository.existsByEmail("dsa_student@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("DSA Student")
                    .email("dsa_student@vcube.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .isActive(true)
                    .roles(Set.of(studentRole))
                    .build());
        }

        if (!userRepository.existsByEmail("dsa_trainer@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("DSA Trainer")
                    .email("dsa_trainer@vcube.com")
                    .password(passwordEncoder.encode("Trainer@123"))
                    .isActive(true)
                    .roles(Set.of(trainerRole))
                    .build());
        }

        if (!userRepository.existsByEmail("dsa_admin@vcube.com")) {
            userRepository.save(User.builder()
                    .fullName("DSA Admin")
                    .email("dsa_admin@vcube.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .isActive(true)
                    .roles(Set.of(adminRole))
                    .build());
        }

        DsaCategory cat = categoryRepository.findBySlug("arrays")
                .orElseGet(() -> categoryRepository.save(DsaCategory.builder()
                        .name("Arrays")
                        .slug("arrays")
                        .description("Array algorithms and manipulation")
                        .icon("Array")
                        .displayOrder(1)
                        .isActive(true)
                        .build()));

        DsaProblem problem = problemRepository.findBySlug("two-sum")
                .orElseGet(() -> problemRepository.save(DsaProblem.builder()
                        .category(cat)
                        .title("Two Sum")
                        .slug("two-sum")
                        .description("Find pair with given sum")
                        .difficulty(DsaDifficulty.EASY)
                        .subtopic("Hash Table")
                        .javaStarterCode("public class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        return new int[]{};\n    }\n}")
                        .solutionExplanation("Use HashMap for O(N) solution")
                        .solutionJavaCode("import java.util.*;\npublic class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> map = new HashMap<>();\n        for(int i=0; i<nums.length; i++) {\n            int diff = target - nums[i];\n            if(map.containsKey(diff)) return new int[]{map.get(diff), i};\n            map.put(nums[i], i);\n        }\n        return new int[]{};\n    }\n}")
                        .isPublished(true)
                        .build()));
        seededProblemId = problem.getId();

        if (testCaseRepository.findByProblemIdOrderByDisplayOrderAsc(problem.getId()).isEmpty()) {
            testCaseRepository.save(DsaTestCase.builder()
                    .problem(problem)
                    .input("2 7 11 15\n9")
                    .expectedOutput("0 1")
                    .isSample(true)
                    .isHidden(false)
                    .displayOrder(1)
                    .build());

            testCaseRepository.save(DsaTestCase.builder()
                    .problem(problem)
                    .input("3 3\n6")
                    .expectedOutput("0 1")
                    .isSample(false)
                    .isHidden(true)
                    .displayOrder(2)
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
    @DisplayName("DSA Student: Browse categories -> Filter problems -> Detail -> Run Code -> Submit -> View Submissions & Progress")
    void testStudentDsaFlow() throws Exception {
        studentToken = loginAndGetToken("dsa_student@vcube.com", "Student@123");

        // 1. Fetch categories
        mockMvc.perform(apiGet("/dsa/categories")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 2. Fetch problems list
        mockMvc.perform(apiGet("/dsa/problems?page=0&size=10")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));

        // 3. Fetch problem detail
        MvcResult detailRes = mockMvc.perform(apiGet("/dsa/problems/" + seededProblemId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seededProblemId))
                .andExpect(jsonPath("$.sampleTestCases", hasSize(greaterThanOrEqualTo(1))))
                .andReturn();

        String detailJson = detailRes.getResponse().getContentAsString();
        // Verify hidden test cases are NOT exposed in problem detail!
        assertThat(detailJson).doesNotContain("\"isHidden\":true");

        // 4. Run Code (Sample test cases)
        String validCode = "import java.util.*;\npublic class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> map = new HashMap<>();\n        for(int i=0; i<nums.length; i++) {\n            int diff = target - nums[i];\n            if(map.containsKey(diff)) return new int[]{map.get(diff), i};\n            map.put(nums[i], i);\n        }\n        return new int[]{};\n    }\n}";

        DsaSubmissionRequest runReq = DsaSubmissionRequest.builder().sourceCode(validCode).language("JAVA").build();

        mockMvc.perform(apiPost("/dsa/problems/" + seededProblemId + "/run")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(runReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // 5. Submit Code (All test cases)
        MvcResult submitRes = mockMvc.perform(apiPost("/dsa/problems/" + seededProblemId + "/submit")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(runReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.passedTestCases").value(greaterThanOrEqualTo(1)))
                .andReturn();

        Long submissionId = objectMapper.readTree(submitRes.getResponse().getContentAsString()).get("id").asLong();
        assertThat(submissionId).isNotNull();

        // 6. Fetch user submissions
        mockMvc.perform(apiGet("/dsa/submissions")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));

        // 7. Fetch progress analytics
        mockMvc.perform(apiGet("/dsa/progress")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solvedProblems").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.easySolved").value(greaterThanOrEqualTo(1)));

        // 8. Fetch hints
        mockMvc.perform(apiGet("/dsa/problems/" + seededProblemId + "/hints")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        // 9. Fetch solution
        mockMvc.perform(apiGet("/dsa/problems/" + seededProblemId + "/solution")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solutionJavaCode").isNotEmpty());
    }

    @Test
    @DisplayName("DSA Trainer & Admin Operations: Create problem -> Edit -> Check Admin Stats -> Delete Problem")
    void testTrainerAndAdminDsaOperations() throws Exception {
        trainerToken = loginAndGetToken("dsa_trainer@vcube.com", "Trainer@123");
        adminToken = loginAndGetToken("dsa_admin@vcube.com", "Admin@123");

        DsaCategory cat = categoryRepository.findBySlug("arrays").orElseThrow();

        // 1. Trainer create DSA problem
        DsaProblemRequest createReq = DsaProblemRequest.builder()
                .categoryId(cat.getId())
                .title("Reverse String DSA " + System.currentTimeMillis())
                .slug("reverse-string-" + System.currentTimeMillis())
                .description("Reverse a string in-place")
                .difficulty(DsaDifficulty.EASY)
                .javaStarterCode("public class Solution { public String reverse(String s) { return s; } }")
                .testCases(List.of(
                        DsaTestCaseRequest.builder().input("hello").expectedOutput("olleh").isSample(true).isHidden(false).build()
                ))
                .build();

        MvcResult createRes = mockMvc.perform(apiPost("/trainer/dsa/problems")
                        .header("Authorization", "Bearer " + trainerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        Long newProblemId = objectMapper.readTree(createRes.getResponse().getContentAsString()).get("id").asLong();

        // 2. Admin view stats
        mockMvc.perform(apiGet("/admin/dsa/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProblems").isNumber());

        // 3. Trainer delete problem
        mockMvc.perform(apiDelete("/trainer/dsa/problems/" + newProblemId)
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DSA Security: Student cannot create DSA problems or access admin stats")
    void testDsaSecurityRestrictions() throws Exception {
        studentToken = loginAndGetToken("dsa_student@vcube.com", "Student@123");

        // 1. Student trying to create problem -> 403
        DsaProblemRequest hackReq = DsaProblemRequest.builder()
                .categoryId(1L)
                .title("Hack")
                .description("Unauthorized problem creation attempt")
                .difficulty(DsaDifficulty.EASY)
                .javaStarterCode("public class Hack {}")
                .build();

        mockMvc.perform(apiPost("/trainer/dsa/problems")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hackReq)))
                .andExpect(status().isForbidden());

        // 2. Student trying to access admin dashboard -> 403
        mockMvc.perform(apiGet("/admin/dsa/dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }
}
