package com.vcube.academy.controller;

import com.vcube.academy.dto.course.*;
import com.vcube.academy.dto.request.CategoryRequest;
import com.vcube.academy.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course browsing and management APIs")
public class CourseController {

    private final CourseService courseService;

    // ─── Public / Student endpoints ───────────────────────────────────────────

    @GetMapping("/categories")
    @Operation(summary = "List all course categories")
    public ResponseEntity<List<CourseCategoryDto>> getCategories() {
        return ResponseEntity.ok(courseService.getAllCategories());
    }

    @GetMapping
    @Operation(summary = "List all published courses")
    public ResponseEntity<List<CourseDto>> getAllCourses(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(courseService.getCoursesByCategory(category));
        }
        return ResponseEntity.ok(courseService.getAllPublishedCourses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course detail by ID")
    public ResponseEntity<CourseDetailDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseDetail(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get course detail by slug")
    public ResponseEntity<CourseDetailDto> getCourseBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(courseService.getCourseDetailBySlug(slug));
    }

    // ─── Trainer / Admin CRUD ─────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Create a new course")
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Update a course")
    public ResponseEntity<CourseDto> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Delete a course")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Module CRUD ──────────────────────────────────────────────────────────

    @PostMapping("/{courseId}/modules")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Create a module in a course")
    public ResponseEntity<CourseModuleDto> createModule(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseModuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createModule(courseId, request));
    }

    @PutMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Update a module")
    public ResponseEntity<CourseModuleDto> updateModule(
            @PathVariable Long moduleId,
            @Valid @RequestBody CourseModuleRequest request) {
        return ResponseEntity.ok(courseService.updateModule(moduleId, request));
    }

    @DeleteMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    @Operation(summary = "Delete a module")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        courseService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    // ─── Category CRUD ────────────────────────────────────────────────────────

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new course category")
    public ResponseEntity<CourseCategoryDto> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCategory(request.getName(), request.getSlug(), request.getDescription()));
    }
}
