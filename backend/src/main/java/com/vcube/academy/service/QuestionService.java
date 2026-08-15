package com.vcube.academy.service;

import com.vcube.academy.dto.quiz.QuestionAdminDto;
import com.vcube.academy.dto.quiz.QuestionOptionAdminDto;
import com.vcube.academy.dto.quiz.QuestionRequest;
import com.vcube.academy.entity.Course;
import com.vcube.academy.entity.Question;
import com.vcube.academy.entity.QuestionOption;
import com.vcube.academy.entity.Topic;
import com.vcube.academy.entity.User;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.CourseRepository;
import com.vcube.academy.repository.QuestionRepository;
import com.vcube.academy.repository.TopicRepository;
import com.vcube.academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<QuestionAdminDto> getQuestions(Long topicId, Long courseId) {
        List<Question> list;
        if (topicId != null) {
            list = questionRepository.findByTopicIdOrderByIdAsc(topicId);
        } else if (courseId != null) {
            list = questionRepository.findByCourseIdOrderByIdAsc(courseId);
        } else {
            list = questionRepository.findAllByOrderByIdDesc();
        }
        return list.stream().map(this::toAdminDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuestionAdminDto getQuestionById(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + id));
        return toAdminDto(q);
    }

    @Transactional
    public QuestionAdminDto createQuestion(QuestionRequest request, Long userId) {
        Topic topic = null;
        if (request.getTopicId() != null) {
            topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + request.getTopicId()));
        }

        Course course = null;
        if (request.getCourseId() != null) {
            course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + request.getCourseId()));
        } else if (topic != null && topic.getModule() != null) {
            course = topic.getModule().getCourse();
        }

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        Question question = Question.builder()
                .topic(topic)
                .course(course)
                .questionText(request.getQuestionText())
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "MEDIUM")
                .explanation(request.getExplanation())
                .interviewPoint(request.getInterviewPoint())
                .companyTags(request.getCompanyTags())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .createdBy(user)
                .options(new ArrayList<>())
                .build();

        if (request.getOptions() != null) {
            for (var optReq : request.getOptions()) {
                QuestionOption opt = QuestionOption.builder()
                        .question(question)
                        .optionLabel(optReq.getOptionLabel())
                        .optionText(optReq.getOptionText())
                        .isCorrect(optReq.getIsCorrect() != null ? optReq.getIsCorrect() : false)
                        .whyWrong(optReq.getWhyWrong())
                        .build();
                question.getOptions().add(opt);
            }
        }

        Question saved = questionRepository.save(question);
        return toAdminDto(saved);
    }

    @Transactional
    public QuestionAdminDto updateQuestion(Long id, QuestionRequest request) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + id));

        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + request.getTopicId()));
            q.setTopic(topic);
        }

        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + request.getCourseId()));
            q.setCourse(course);
        }

        q.setQuestionText(request.getQuestionText());
        if (request.getDifficulty() != null) q.setDifficulty(request.getDifficulty());
        q.setExplanation(request.getExplanation());
        q.setInterviewPoint(request.getInterviewPoint());
        q.setCompanyTags(request.getCompanyTags());
        if (request.getIsActive() != null) q.setIsActive(request.getIsActive());

        if (request.getOptions() != null) {
            q.getOptions().clear();
            for (var optReq : request.getOptions()) {
                QuestionOption opt = QuestionOption.builder()
                        .question(q)
                        .optionLabel(optReq.getOptionLabel())
                        .optionText(optReq.getOptionText())
                        .isCorrect(optReq.getIsCorrect() != null ? optReq.getIsCorrect() : false)
                        .whyWrong(optReq.getWhyWrong())
                        .build();
                q.getOptions().add(opt);
            }
        }

        Question updated = questionRepository.save(q);
        return toAdminDto(updated);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with ID: " + id);
        }
        questionRepository.deleteById(id);
    }

    private QuestionAdminDto toAdminDto(Question q) {
        List<QuestionOptionAdminDto> options = q.getOptions() != null
                ? q.getOptions().stream().map(o -> QuestionOptionAdminDto.builder()
                        .id(o.getId())
                        .optionLabel(o.getOptionLabel())
                        .optionText(o.getOptionText())
                        .isCorrect(o.getIsCorrect())
                        .whyWrong(o.getWhyWrong())
                        .build()).collect(Collectors.toList())
                : List.of();

        return QuestionAdminDto.builder()
                .id(q.getId())
                .topicId(q.getTopic() != null ? q.getTopic().getId() : null)
                .topicTitle(q.getTopic() != null ? q.getTopic().getTitle() : null)
                .courseId(q.getCourse() != null ? q.getCourse().getId() : null)
                .courseTitle(q.getCourse() != null ? q.getCourse().getTitle() : null)
                .questionText(q.getQuestionText())
                .difficulty(q.getDifficulty())
                .explanation(q.getExplanation())
                .interviewPoint(q.getInterviewPoint())
                .companyTags(q.getCompanyTags())
                .isActive(q.getIsActive())
                .options(options)
                .build();
    }
}
