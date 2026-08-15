package com.vcube.academy.service;

import com.vcube.academy.dto.course.*;
import com.vcube.academy.entity.Course;
import com.vcube.academy.entity.CourseCategory;
import com.vcube.academy.entity.CourseModule;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.CourseCategoryRepository;
import com.vcube.academy.repository.CourseModuleRepository;
import com.vcube.academy.repository.CourseRepository;
import com.vcube.academy.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository categoryRepository;
    private final CourseModuleRepository moduleRepository;
    private final TopicRepository topicRepository;

    // ─── Public Course Listing ─────────────────────────────────────────────────

    public List<CourseCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toCategoryDto)
                .toList();
    }

    public List<CourseDto> getAllPublishedCourses() {
        return courseRepository.findAllPublished().stream()
                .map(c -> toCourseDto(c, true))
                .toList();
    }

    public List<CourseDto> getCoursesByCategory(String categorySlug) {
        return courseRepository.findPublishedByCategorySlug(categorySlug).stream()
                .map(c -> toCourseDto(c, false))
                .toList();
    }

    public CourseDetailDto getCourseDetail(Long id) {
        Course course = courseRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return toCourseDetailDto(course);
    }

    public CourseDetailDto getCourseDetailBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with slug: " + slug));
        return toCourseDetailDto(course);
    }

    // ─── Trainer / Admin Course CRUD ──────────────────────────────────────────

    @Transactional
    public CourseDto createCourse(CourseRequest request) {
        CourseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        Course course = Course.builder()
                .category(category)
                .title(request.getTitle())
                .slug(request.getSlug())
                .description(request.getDescription())
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "BEGINNER")
                .estimatedHours(request.getEstimatedHours())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        Course saved = courseRepository.save(course);
        return toCourseDto(saved, false);
    }

    @Transactional
    public CourseDto updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        if (request.getCategoryId() != null) {
            CourseCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));
            course.setCategory(category);
        }
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getSlug() != null) course.setSlug(request.getSlug());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getDifficulty() != null) course.setDifficulty(request.getDifficulty());
        if (request.getEstimatedHours() != null) course.setEstimatedHours(request.getEstimatedHours());
        if (request.getIsPublished() != null) course.setIsPublished(request.getIsPublished());
        if (request.getDisplayOrder() != null) course.setDisplayOrder(request.getDisplayOrder());

        Course saved = courseRepository.save(course);
        return toCourseDto(saved, false);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    // ─── Module CRUD ──────────────────────────────────────────────────────────

    @Transactional
    public CourseModuleDto createModule(Long courseId, CourseModuleRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        CourseModule module = CourseModule.builder()
                .course(course)
                .title(request.getTitle())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        CourseModule saved = moduleRepository.save(module);
        return toModuleDto(saved);
    }

    @Transactional
    public CourseModuleDto updateModule(Long moduleId, CourseModuleRequest request) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found: " + moduleId));

        if (request.getTitle() != null) module.setTitle(request.getTitle());
        if (request.getDescription() != null) module.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) module.setDisplayOrder(request.getDisplayOrder());

        return toModuleDto(moduleRepository.save(module));
    }

    @Transactional
    public void deleteModule(Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module not found: " + moduleId);
        }
        moduleRepository.deleteById(moduleId);
    }

    // ─── Category CRUD ────────────────────────────────────────────────────────

    @Transactional
    public CourseCategoryDto createCategory(String name, String slug, String description) {
        CourseCategory category = CourseCategory.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .build();
        return toCategoryDto(categoryRepository.save(category));
    }

    // ─── Mapping helpers ──────────────────────────────────────────────────────

    private CourseCategoryDto toCategoryDto(CourseCategory c) {
        return CourseCategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .description(c.getDescription())
                .icon(c.getIcon())
                .displayOrder(c.getDisplayOrder())
                .isActive(c.getIsActive())
                .courseCount((long) c.getCourses().size())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private CourseDto toCourseDto(Course c, boolean withCounts) {
        long moduleCount = withCounts ? c.getModules().size() : 0L;
        long topicCount = withCounts
                ? topicRepository.countPublishedByCourseId(c.getId())
                : 0L;

        return CourseDto.builder()
                .id(c.getId())
                .categoryId(c.getCategory().getId())
                .categoryName(c.getCategory().getName())
                .categorySlug(c.getCategory().getSlug())
                .title(c.getTitle())
                .slug(c.getSlug())
                .description(c.getDescription())
                .difficulty(c.getDifficulty())
                .estimatedHours(c.getEstimatedHours())
                .isPublished(c.getIsPublished())
                .displayOrder(c.getDisplayOrder())
                .moduleCount(moduleCount)
                .topicCount(topicCount)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CourseDetailDto toCourseDetailDto(Course c) {
        List<CourseModule> modules = moduleRepository.findByCourseIdOrderByDisplayOrder(c.getId());
        List<CourseModuleDto> moduleDtos = modules.stream()
                .map(this::toModuleDtoWithTopics)
                .toList();

        return CourseDetailDto.builder()
                .id(c.getId())
                .categoryId(c.getCategory().getId())
                .categoryName(c.getCategory().getName())
                .categorySlug(c.getCategory().getSlug())
                .title(c.getTitle())
                .slug(c.getSlug())
                .description(c.getDescription())
                .difficulty(c.getDifficulty())
                .estimatedHours(c.getEstimatedHours())
                .isPublished(c.getIsPublished())
                .displayOrder(c.getDisplayOrder())
                .modules(moduleDtos)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CourseModuleDto toModuleDto(CourseModule m) {
        return CourseModuleDto.builder()
                .id(m.getId())
                .courseId(m.getCourse().getId())
                .title(m.getTitle())
                .description(m.getDescription())
                .displayOrder(m.getDisplayOrder())
                .build();
    }

    private CourseModuleDto toModuleDtoWithTopics(CourseModule m) {
        List<TopicSummaryDto> topics = topicRepository.findByModuleIdOrderByDisplayOrder(m.getId())
                .stream()
                .map(t -> TopicSummaryDto.builder()
                        .id(t.getId())
                        .moduleId(m.getId())
                        .title(t.getTitle())
                        .slug(t.getSlug())
                        .difficulty(t.getDifficulty())
                        .estimatedMinutes(t.getEstimatedMinutes())
                        .displayOrder(t.getDisplayOrder())
                        .isPublished(t.getIsPublished())
                        .build())
                .toList();

        return CourseModuleDto.builder()
                .id(m.getId())
                .courseId(m.getCourse().getId())
                .title(m.getTitle())
                .description(m.getDescription())
                .displayOrder(m.getDisplayOrder())
                .topics(topics)
                .build();
    }
}
