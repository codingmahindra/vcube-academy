package com.vcube.academy.service;

import com.vcube.academy.dto.topic.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicContentRepository contentRepository;
    private final TopicCompletionRepository completionRepository;
    private final StudentProgressRepository progressRepository;
    private final QuestionRepository questionRepository;
    private final CourseModuleRepository moduleRepository;
    private final UserRepository userRepository;

    // ─── Public topic reading ─────────────────────────────────────────────────

    public List<TopicDto> getTopicsByModule(Long moduleId) {
        return topicRepository.findByModuleIdOrderByDisplayOrder(moduleId).stream()
                .map(this::toTopicDto)
                .toList();
    }

    public TopicDetailDto getTopicDetail(Long topicId) {
        Topic topic = topicRepository.findByIdWithModule(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));

        TopicContent content = contentRepository.findByTopicId(topicId).orElse(null);
        long questionCount = questionRepository.countByTopicIdAndIsActiveTrue(topicId);

        return toTopicDetailDto(topic, content, (int) questionCount);
    }

    // ─── Student: mark topic complete ─────────────────────────────────────────

    @Transactional
    public void markTopicComplete(Long studentId, Long topicId) {
        // Idempotent: if already completed, do nothing
        if (completionRepository.existsByStudentIdAndTopicId(studentId, topicId)) {
            return;
        }

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        TopicCompletion completion = TopicCompletion.builder()
                .student(student)
                .topic(topic)
                .completedAt(Instant.now())
                .build();
        completionRepository.save(completion);

        // Update StudentProgress for the course
        Long courseId = topic.getModule().getCourse().getId();
        Course course = topic.getModule().getCourse();

        Optional<StudentProgress> progressOpt = progressRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (progressOpt.isPresent()) {
            StudentProgress progress = progressOpt.get();
            progress.setTopicsCompleted(progress.getTopicsCompleted() + 1);
            progress.setLastActivityAt(Instant.now());
            progressRepository.save(progress);
        } else {
            long totalTopics = topicRepository.countPublishedByCourseId(courseId);
            StudentProgress progress = StudentProgress.builder()
                    .student(student)
                    .course(course)
                    .topicsCompleted(1)
                    .totalTopics((int) totalTopics)
                    .lastActivityAt(Instant.now())
                    .build();
            progressRepository.save(progress);
        }
    }

    public boolean isTopicCompleted(Long studentId, Long topicId) {
        return completionRepository.existsByStudentIdAndTopicId(studentId, topicId);
    }

    // ─── Trainer CRUD ─────────────────────────────────────────────────────────

    @Transactional
    public TopicDto createTopic(Long moduleId, TopicRequest request) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found: " + moduleId));

        Topic topic = Topic.builder()
                .module(module)
                .title(request.getTitle())
                .slug(request.getSlug())
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "EASY")
                .estimatedMinutes(request.getEstimatedMinutes() != null ? request.getEstimatedMinutes() : 30)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : true)
                .build();

        Topic saved = topicRepository.save(topic);

        // Create content if provided
        if (request.getContent() != null) {
            TopicContentRequest cr = request.getContent();
            TopicContent content = TopicContent.builder()
                    .topic(saved)
                    .explanation(cr.getExplanation())
                    .simpleExplanation(cr.getSimpleExplanation())
                    .realWorldExample(cr.getRealWorldExample())
                    .syntaxExample(cr.getSyntaxExample())
                    .codeExample(cr.getCodeExample())
                    .codeLanguage(cr.getCodeLanguage() != null ? cr.getCodeLanguage() : "java")
                    .interviewPoints(cr.getInterviewPoints())
                    .commonMistakes(cr.getCommonMistakes())
                    .practiceQuestions(cr.getPracticeQuestions())
                    .build();
            contentRepository.save(content);
        }

        return toTopicDto(saved);
    }

    @Transactional
    public TopicDto updateTopic(Long topicId, TopicRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));

        if (request.getTitle() != null) topic.setTitle(request.getTitle());
        if (request.getSlug() != null) topic.setSlug(request.getSlug());
        if (request.getDifficulty() != null) topic.setDifficulty(request.getDifficulty());
        if (request.getEstimatedMinutes() != null) topic.setEstimatedMinutes(request.getEstimatedMinutes());
        if (request.getDisplayOrder() != null) topic.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsPublished() != null) topic.setIsPublished(request.getIsPublished());

        Topic saved = topicRepository.save(topic);

        // Update content if provided
        if (request.getContent() != null) {
            TopicContentRequest cr = request.getContent();
            TopicContent content = contentRepository.findByTopicId(topicId)
                    .orElse(TopicContent.builder().topic(saved).build());
            if (cr.getExplanation() != null) content.setExplanation(cr.getExplanation());
            if (cr.getSimpleExplanation() != null) content.setSimpleExplanation(cr.getSimpleExplanation());
            if (cr.getRealWorldExample() != null) content.setRealWorldExample(cr.getRealWorldExample());
            if (cr.getSyntaxExample() != null) content.setSyntaxExample(cr.getSyntaxExample());
            if (cr.getCodeExample() != null) content.setCodeExample(cr.getCodeExample());
            if (cr.getCodeLanguage() != null) content.setCodeLanguage(cr.getCodeLanguage());
            if (cr.getInterviewPoints() != null) content.setInterviewPoints(cr.getInterviewPoints());
            if (cr.getCommonMistakes() != null) content.setCommonMistakes(cr.getCommonMistakes());
            if (cr.getPracticeQuestions() != null) content.setPracticeQuestions(cr.getPracticeQuestions());
            contentRepository.save(content);
        }

        return toTopicDto(saved);
    }

    @Transactional
    public void deleteTopic(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("Topic not found: " + topicId);
        }
        topicRepository.deleteById(topicId);
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private TopicDto toTopicDto(Topic t) {
        return TopicDto.builder()
                .id(t.getId())
                .moduleId(t.getModule().getId())
                .moduleTitle(t.getModule().getTitle())
                .courseId(t.getModule().getCourse().getId())
                .courseTitle(t.getModule().getCourse().getTitle())
                .title(t.getTitle())
                .slug(t.getSlug())
                .difficulty(t.getDifficulty())
                .estimatedMinutes(t.getEstimatedMinutes())
                .displayOrder(t.getDisplayOrder())
                .isPublished(t.getIsPublished())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }

    private TopicDetailDto toTopicDetailDto(Topic t, TopicContent content, int questionCount) {
        TopicContentDto contentDto = null;
        if (content != null) {
            contentDto = TopicContentDto.builder()
                    .id(content.getId())
                    .topicId(t.getId())
                    .explanation(content.getExplanation())
                    .simpleExplanation(content.getSimpleExplanation())
                    .realWorldExample(content.getRealWorldExample())
                    .syntaxExample(content.getSyntaxExample())
                    .codeExample(content.getCodeExample())
                    .codeLanguage(content.getCodeLanguage())
                    .interviewPoints(content.getInterviewPoints())
                    .commonMistakes(content.getCommonMistakes())
                    .practiceQuestions(content.getPracticeQuestions())
                    .createdAt(content.getCreatedAt())
                    .updatedAt(content.getUpdatedAt())
                    .build();
        }

        return TopicDetailDto.builder()
                .id(t.getId())
                .moduleId(t.getModule().getId())
                .moduleTitle(t.getModule().getTitle())
                .courseId(t.getModule().getCourse().getId())
                .courseTitle(t.getModule().getCourse().getTitle())
                .title(t.getTitle())
                .slug(t.getSlug())
                .difficulty(t.getDifficulty())
                .estimatedMinutes(t.getEstimatedMinutes())
                .displayOrder(t.getDisplayOrder())
                .isPublished(t.getIsPublished())
                .questionCount(questionCount)
                .content(contentDto)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
