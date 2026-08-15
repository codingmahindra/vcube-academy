package com.vcube.academy.service;

import com.vcube.academy.dto.interview.CompanyAdminRequest;
import com.vcube.academy.dto.interview.InterviewQuestionAdminRequest;
import com.vcube.academy.dto.interview.InterviewQuestionDetailDto;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.BadRequestException;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewTrainerAdminService {

    private final InterviewQuestionRepository questionRepository;
    private final InterviewTopicRepository topicRepository;
    private final CompanyRepository companyRepository;
    private final CompanyInterviewQuestionRepository companyQuestionRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final InterviewEvaluationRepository evaluationRepository;
    private final InterviewStudentProgressRepository progressRepository;

    @Transactional
    public InterviewQuestionDetailDto createQuestion(InterviewQuestionAdminRequest req) {
        InterviewTopic topic = topicRepository.findById(req.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + req.getTopicId()));

        InterviewQuestion question = InterviewQuestion.builder()
                .topic(topic)
                .questionText(req.getQuestionText())
                .questionType(req.getQuestionType() != null ? req.getQuestionType() : InterviewQuestionType.CONCEPTUAL)
                .difficulty(req.getDifficulty() != null ? req.getDifficulty() : InterviewDifficulty.INTERMEDIATE)
                .interviewRound(req.getInterviewRound() != null ? req.getInterviewRound() : InterviewRoundType.ROUND_3_TECHNICAL)
                .questionSource(req.getQuestionSource() != null ? req.getQuestionSource() : QuestionSource.PRACTICE_QUESTION)
                .sourceReference(req.getSourceReference())
                .expectedAnswer(req.getExpectedAnswer())
                .explanation(req.getExplanation())
                .interviewPoints(req.getInterviewPoints())
                .commonMistakes(req.getCommonMistakes())
                .followUpQuestions(req.getFollowUpQuestions())
                .realWorldExample(req.getRealWorldExample())
                .evaluationKeywords(req.getEvaluationKeywords())
                .isPublished(req.getIsPublished() != null ? req.getIsPublished() : true)
                .build();

        question = questionRepository.save(question);

        if (req.getCompanyIds() != null && !req.getCompanyIds().isEmpty()) {
            for (Long compId : req.getCompanyIds()) {
                Company comp = companyRepository.findById(compId).orElse(null);
                if (comp != null) {
                    CompanyInterviewQuestion cm = CompanyInterviewQuestion.builder()
                            .company(comp)
                            .question(question)
                            .frequency("HIGH")
                            .build();
                    companyQuestionRepository.save(cm);
                }
            }
        }

        return mapToDetailDto(question);
    }

    @Transactional
    public InterviewQuestionDetailDto updateQuestion(Long id, InterviewQuestionAdminRequest req) {
        InterviewQuestion q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found with id: " + id));

        if (req.getTopicId() != null) {
            InterviewTopic topic = topicRepository.findById(req.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + req.getTopicId()));
            q.setTopic(topic);
        }

        if (req.getQuestionText() != null) q.setQuestionText(req.getQuestionText());
        if (req.getQuestionType() != null) q.setQuestionType(req.getQuestionType());
        if (req.getDifficulty() != null) q.setDifficulty(req.getDifficulty());
        if (req.getInterviewRound() != null) q.setInterviewRound(req.getInterviewRound());
        if (req.getQuestionSource() != null) q.setQuestionSource(req.getQuestionSource());
        if (req.getSourceReference() != null) q.setSourceReference(req.getSourceReference());
        if (req.getExpectedAnswer() != null) q.setExpectedAnswer(req.getExpectedAnswer());
        if (req.getExplanation() != null) q.setExplanation(req.getExplanation());
        if (req.getInterviewPoints() != null) q.setInterviewPoints(req.getInterviewPoints());
        if (req.getCommonMistakes() != null) q.setCommonMistakes(req.getCommonMistakes());
        if (req.getFollowUpQuestions() != null) q.setFollowUpQuestions(req.getFollowUpQuestions());
        if (req.getRealWorldExample() != null) q.setRealWorldExample(req.getRealWorldExample());
        if (req.getEvaluationKeywords() != null) q.setEvaluationKeywords(req.getEvaluationKeywords());
        if (req.getIsPublished() != null) q.setIsPublished(req.getIsPublished());

        q = questionRepository.save(q);
        return mapToDetailDto(q);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }

    @Transactional
    public Company createCompany(CompanyAdminRequest req) {
        if (companyRepository.existsBySlug(req.getSlug())) {
            throw new BadRequestException("Company with slug '" + req.getSlug() + "' already exists");
        }

        Company company = Company.builder()
                .name(req.getName())
                .slug(req.getSlug())
                .logoUrl(req.getLogoUrl())
                .description(req.getDescription())
                .industry(req.getIndustry())
                .tier(req.getTier() != null ? req.getTier() : "TIER_1")
                .hiringRoundsInfo(req.getHiringRoundsInfo())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .build();

        return companyRepository.save(company);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuestions", questionRepository.count());
        stats.put("totalCompanies", companyRepository.count());
        stats.put("totalMockInterviews", mockInterviewRepository.count());
        stats.put("totalEvaluations", evaluationRepository.count());
        stats.put("completedProgressRecords", progressRepository.count());
        return stats;
    }

    private InterviewQuestionDetailDto mapToDetailDto(InterviewQuestion q) {
        List<String> companyNames = companyQuestionRepository.findByQuestionId(q.getId()).stream()
                .map(cm -> cm.getCompany().getName())
                .collect(Collectors.toList());

        return InterviewQuestionDetailDto.builder()
                .id(q.getId())
                .topicId(q.getTopic().getId())
                .topicName(q.getTopic().getName())
                .categoryId(q.getTopic().getCategory().getId())
                .categoryName(q.getTopic().getCategory().getName())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .difficulty(q.getDifficulty())
                .interviewRound(q.getInterviewRound())
                .questionSource(q.getQuestionSource())
                .sourceReference(q.getSourceReference())
                .expectedAnswer(q.getExpectedAnswer())
                .explanation(q.getExplanation())
                .interviewPoints(q.getInterviewPoints())
                .commonMistakes(q.getCommonMistakes())
                .followUpQuestions(q.getFollowUpQuestions())
                .realWorldExample(q.getRealWorldExample())
                .isCompleted(false)
                .lastScore(null)
                .companies(companyNames)
                .build();
    }
}
