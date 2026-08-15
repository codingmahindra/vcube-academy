package com.vcube.academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.auth.LoginRequest;
import com.vcube.academy.dto.bookmark.BookmarkCreateRequest;
import com.vcube.academy.dto.user.StudentProfileUpdateRequest;
import com.vcube.academy.entity.Role;
import com.vcube.academy.entity.RoleType;
import com.vcube.academy.entity.User;
import com.vcube.academy.enums.BookmarkItemType;
import com.vcube.academy.repository.CourseRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("default")
@Transactional
public class Phase9ProductionEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentToken;
    private String trainerToken;
    private Long studentId;

    private MockHttpServletRequestBuilder apiPost(String url) {
        return post(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiGet(String url) {
        return get(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiDelete(String url) {
        return delete(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiPatch(String url) {
        return patch(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    private MockHttpServletRequestBuilder apiPut(String url) {
        return put(url.startsWith("/api") ? url : "/api" + url).contextPath("/api");
    }

    @BeforeEach
    void setUp() throws Exception {
        Role studentRole = roleRepository.findByName(RoleType.STUDENT)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.STUDENT).build()));
        Role trainerRole = roleRepository.findByName(RoleType.TRAINER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.TRAINER).build()));

        String email = "p9_student_" + System.currentTimeMillis() + "@vcube.com";
        User student = userRepository.save(User.builder()
                .fullName("Srikanth Student")
                .email(email)
                .password(passwordEncoder.encode("Password@123"))
                .roles(new HashSet<>(Set.of(studentRole)))
                .isActive(true)
                .build());
        studentId = student.getId();

        String trainerEmail = "p9_trainer_" + System.currentTimeMillis() + "@vcube.com";
        userRepository.save(User.builder()
                .fullName("Trainer Viswanath")
                .email(trainerEmail)
                .password(passwordEncoder.encode("Password@123"))
                .roles(new HashSet<>(Set.of(trainerRole)))
                .isActive(true)
                .build());

        // Authenticate student
        MvcResult res = mockMvc.perform(apiPost("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "Password@123"))))
                .andExpect(status().isOk())
                .andReturn();
        studentToken = objectMapper.readTree(res.getResponse().getContentAsString()).get("accessToken").asText();

        // Authenticate trainer
        MvcResult trainerRes = mockMvc.perform(apiPost("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(trainerEmail, "Password@123"))))
                .andExpect(status().isOk())
                .andReturn();
        trainerToken = objectMapper.readTree(trainerRes.getResponse().getContentAsString()).get("accessToken").asText();
    }

    @Test
    @DisplayName("P9-1: Student Gamification summary & badges evaluation")
    void testGamificationSummary() throws Exception {
        mockMvc.perform(apiGet("/student/gamification/summary")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId", is(studentId.intValue())))
                .andExpect(jsonPath("$.totalBadgesCount", greaterThanOrEqualTo(8)))
                .andExpect(jsonPath("$.badges", not(empty())))
                .andExpect(jsonPath("$.currentStreakDays", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.nextMilestoneGoal", notNullValue()));
    }

    @Test
    @DisplayName("P9-2: Universal Bookmarks full lifecycle (add, list, check, remove)")
    void testBookmarksLifecycle() throws Exception {
        BookmarkCreateRequest req = BookmarkCreateRequest.builder()
                .itemType(BookmarkItemType.DSA_PROBLEM)
                .itemId(1L)
                .itemTitle("Two Sum")
                .itemSubtitle("Arrays & Hashing")
                .itemRoute("/student/dsa/problems/1")
                .build();

        // 1. Add Bookmark
        mockMvc.perform(apiPost("/student/bookmarks")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemTitle", is("Two Sum")))
                .andExpect(jsonPath("$.itemType", is("DSA_PROBLEM")));

        // 2. Check if bookmarked
        mockMvc.perform(apiGet("/student/bookmarks/check/DSA_PROBLEM/1")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // 3. List Bookmarks
        mockMvc.perform(apiGet("/student/bookmarks")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].itemTitle", is("Two Sum")));

        // 4. Remove Bookmark
        mockMvc.perform(apiDelete("/student/bookmarks/DSA_PROBLEM/1")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNoContent());

        // 5. Check if bookmarked after removal
        mockMvc.perform(apiGet("/student/bookmarks/check/DSA_PROBLEM/1")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("P9-3: In-App Notifications lifecycle (list, unread-count, mark-read, mark-all)")
    void testNotificationsLifecycle() throws Exception {
        // 1. List Notifications
        mockMvc.perform(apiGet("/student/notifications")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 2. Get Unread Count
        mockMvc.perform(apiGet("/student/notifications/unread-count")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount", greaterThanOrEqualTo(1)));

        // 3. Mark All Read
        mockMvc.perform(apiPatch("/student/notifications/read-all")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNoContent());

        // 4. Check unread count is 0
        mockMvc.perform(apiGet("/student/notifications/unread-count")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount", is(0)));
    }

    @Test
    @DisplayName("P9-4: Global Search across multiple domains")
    void testGlobalSearch() throws Exception {
        mockMvc.perform(apiGet("/search?q=Java")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("P9-5: Student Profile view, update, and security isolation")
    void testStudentProfile() throws Exception {
        // 1. Get Profile
        mockMvc.perform(apiGet("/student/profile")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(studentId.intValue())))
                .andExpect(jsonPath("$.fullName", is("Srikanth Student")));

        // 2. Update Profile
        StudentProfileUpdateRequest updateReq = StudentProfileUpdateRequest.builder()
                .fullName("Srikanth Vcube Alumnus")
                .phone("+91 9876543210")
                .college("VCUBE Academy of Technology")
                .degree("B.Tech CSE")
                .graduationYear("2025")
                .cgpa(8.9)
                .bio("Specialized in Spring Boot Microservices and React.")
                .targetRoles(new HashSet<>(Set.of("Java Developer", "Spring Boot Engineer")))
                .preferredLocations(new HashSet<>(Set.of("Hyderabad", "Bengaluru")))
                .includeInResume(true)
                .includeInAtsAnalysis(true)
                .includeInCopilot(true)
                .build();

        mockMvc.perform(apiPut("/student/profile")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Srikanth Vcube Alumnus")))
                .andExpect(jsonPath("$.phone", is("+91 9876543210")));

        // 3. Security: Trainer cannot access student profile endpoint
        mockMvc.perform(apiGet("/student/profile")
                        .header("Authorization", "Bearer " + trainerToken))
                .andExpect(status().isForbidden());

        // 4. Security: Unauthenticated request returns 401
        mockMvc.perform(apiGet("/student/profile"))
                .andExpect(status().isUnauthorized());
    }
}
