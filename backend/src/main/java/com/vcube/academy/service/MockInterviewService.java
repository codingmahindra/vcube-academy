package com.vcube.academy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.interview.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.BadRequestException;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import com.vcube.academy.service.evaluator.InterviewAnswerEvaluator;
import com.vcube.academy.service.evaluator.InterviewEvaluationResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockInterviewService {

    private final MockInterviewRepository mockInterviewRepository;
    private final MockInterviewQuestionRepository mockQuestionRepository;
    private final InterviewQuestionRepository questionRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final InterviewAnswerEvaluator answerEvaluator;
    private final ObjectMapper objectMapper;

    @Transactional
    public MockInterviewResponse startMockInterview(MockInterviewStartRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Company company = null;
        if (request.getTargetCompanyId() != null) {
            company = companyRepository.findById(request.getTargetCompanyId()).orElse(null);
        }

        int totalQuestions = (request.getTotalQuestions() != null && request.getTotalQuestions() > 0)
                ? Math.min(request.getTotalQuestions(), 15)
                : 5;

        // Select candidate questions
        List<InterviewQuestion> candidateQuestions;
        if (company != null) {
            candidateQuestions = questionRepository.findByCompanyId(company.getId());
            if (candidateQuestions.size() < totalQuestions) {
                // Fallback to all published questions to reach required count
                List<InterviewQuestion> allPub = questionRepository.findAll().stream()
                        .filter(InterviewQuestion::getIsPublished)
                        .collect(Collectors.toList());
                candidateQuestions.addAll(allPub);
            }
        } else {
            candidateQuestions = questionRepository.findAll().stream()
                    .filter(InterviewQuestion::getIsPublished)
                    .collect(Collectors.toList());
        }

        if (candidateQuestions.isEmpty()) {
            throw new BadRequestException("No published interview questions available to generate mock interview.");
        }

        Collections.shuffle(candidateQuestions);
        List<InterviewQuestion> selectedQuestions = candidateQuestions.stream()
                .distinct()
                .limit(totalQuestions)
                .collect(Collectors.toList());

        String title = (company != null ? company.getName() + " " : "") +
                (request.getRoleTitle() != null ? request.getRoleTitle() : "Java Full Stack") +
                " Mock Interview (" + selectedQuestions.size() + " Questions)";

        MockInterview mockInterview = MockInterview.builder()
                .user(user)
                .title(title)
                .roleTitle(request.getRoleTitle() != null ? request.getRoleTitle() : "Java Full Stack Developer")
                .targetCompany(company)
                .interviewType(request.getInterviewType() != null ? request.getInterviewType() : "TECHNICAL")
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "INTERMEDIATE")
                .totalQuestions(selectedQuestions.size())
                .status(MockInterviewStatus.IN_PROGRESS)
                .build();

        mockInterview = mockInterviewRepository.save(mockInterview);

        List<MockInterviewQuestion> mockQuestions = new ArrayList<>();
        int order = 1;
        for (InterviewQuestion q : selectedQuestions) {
            MockInterviewQuestion mq = MockInterviewQuestion.builder()
                    .mockInterview(mockInterview)
                    .question(q)
                    .questionOrder(order++)
                    .timeTakenSeconds(0)
                    .build();
            mockQuestions.add(mq);
        }
        mockQuestionRepository.saveAll(mockQuestions);
        mockInterview.setQuestions(mockQuestions);

        return mapToResponse(mockInterview, 1, false);
    }

    @Transactional
    public MockInterviewQuestionDto answerQuestion(Long mockInterviewId, MockInterviewAnswerRequest request, Long userId) {
        MockInterview mock = mockInterviewRepository.findByIdAndUserId(mockInterviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock interview not found"));

        if (mock.getStatus() == MockInterviewStatus.COMPLETED) {
            throw new BadRequestException("Mock interview is already completed.");
        }

        MockInterviewQuestion mq = mockQuestionRepository.findByMockInterviewIdAndQuestionOrder(mockInterviewId, request.getQuestionOrder())
                .orElseThrow(() -> new ResourceNotFoundException("Mock question not found for order: " + request.getQuestionOrder()));

        InterviewQuestion question = mq.getQuestion();
        InterviewEvaluationResultDto evalResult = answerEvaluator.evaluate(question, request.getUserAnswer());

        mq.setUserAnswer(request.getUserAnswer());
        mq.setTimeTakenSeconds(request.getTimeTakenSeconds() != null ? request.getTimeTakenSeconds() : 60);
        mq.setScore(evalResult.getScore());
        mq.setTechnicalAccuracyScore(evalResult.getTechnicalAccuracyScore());
        mq.setCompletenessScore(evalResult.getCompletenessScore());
        mq.setClarityScore(evalResult.getClarityScore());
        mq.setFeedback(evalResult.getFeedback());
        mq.setMissingPoints(writeJson(evalResult.getMissingPoints()));
        mq.setImprovedAnswer(evalResult.getImprovedAnswer());
        mq.setEvaluatedAt(Instant.now());

        mq = mockQuestionRepository.save(mq);

        return mapToQuestionDto(mq, true);
    }

    @Transactional
    public MockInterviewResultDto completeMockInterview(Long mockInterviewId, Long userId) {
        MockInterview mock = mockInterviewRepository.findByIdAndUserId(mockInterviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock interview not found"));

        List<MockInterviewQuestion> questions = mockQuestionRepository.findByMockInterviewIdOrderByQuestionOrderAsc(mockInterviewId);

        double totalScoreSum = 0.0;
        double javaScoreSum = 0.0; int javaCount = 0;
        double sqlScoreSum = 0.0; int sqlCount = 0;
        double springScoreSum = 0.0; int springCount = 0;
        double dsaScoreSum = 0.0; int dsaCount = 0;
        double hrScoreSum = 0.0; int hrCount = 0;
        double techScoreSum = 0.0; int techCount = 0;
        double claritySum = 0.0;

        List<String> weakAreas = new ArrayList<>();
        List<String> strongAreas = new ArrayList<>();
        List<String> revisionTopics = new ArrayList<>();

        for (MockInterviewQuestion mq : questions) {
            double s = mq.getScore() != null ? mq.getScore() : 0.0;
            totalScoreSum += s;
            claritySum += (mq.getClarityScore() != null ? mq.getClarityScore() : 60.0);

            String catSlug = mq.getQuestion().getTopic().getCategory().getSlug().toLowerCase();
            String topicName = mq.getQuestion().getTopic().getName();

            if (catSlug.contains("java")) {
                javaScoreSum += s; javaCount++;
            } else if (catSlug.contains("sql") || catSlug.contains("database")) {
                sqlScoreSum += s; sqlCount++;
            } else if (catSlug.contains("spring") || catSlug.contains("hibernate")) {
                springScoreSum += s; springCount++;
            } else if (catSlug.contains("dsa")) {
                dsaScoreSum += s; dsaCount++;
            } else if (catSlug.contains("behavioral") || catSlug.contains("hr")) {
                hrScoreSum += s; hrCount++;
            }

            if (!catSlug.contains("hr")) {
                techScoreSum += s;
                techCount++;
            }

            if (s >= 75.0) {
                strongAreas.add(topicName);
            } else {
                weakAreas.add(topicName);
                revisionTopics.add(topicName + " (" + mq.getQuestion().getTopic().getCategory().getName() + ")");
            }
        }

        int count = Math.max(1, questions.size());
        double overallScore = Math.round((totalScoreSum / count) * 10.0) / 10.0;
        double javaScore = javaCount > 0 ? Math.round((javaScoreSum / javaCount) * 10.0) / 10.0 : overallScore;
        double sqlScore = sqlCount > 0 ? Math.round((sqlScoreSum / sqlCount) * 10.0) / 10.0 : overallScore;
        double springScore = springCount > 0 ? Math.round((springScoreSum / springCount) * 10.0) / 10.0 : overallScore;
        double dsaScore = dsaCount > 0 ? Math.round((dsaScoreSum / dsaCount) * 10.0) / 10.0 : overallScore;
        double hrScore = hrCount > 0 ? Math.round((hrScoreSum / hrCount) * 10.0) / 10.0 : overallScore;
        double technicalScore = techCount > 0 ? Math.round((techScoreSum / techCount) * 10.0) / 10.0 : overallScore;
        double communicationScore = Math.round((claritySum / count) * 10.0) / 10.0;

        int readinessPct = (int) Math.round(overallScore);
        InterviewReadiness readiness;
        if (readinessPct >= 75) {
            readiness = InterviewReadiness.READY_FOR_INTERVIEW;
        } else if (readinessPct >= 50) {
            readiness = InterviewReadiness.NEEDS_MORE_PREPARATION;
        } else {
            readiness = InterviewReadiness.NOT_READY_YET;
        }

        String feedbackSummary = generateFeedbackSummary(readiness, overallScore, strongAreas, weakAreas);

        mock.setStatus(MockInterviewStatus.COMPLETED);
        mock.setOverallScore(overallScore);
        mock.setTechnicalScore(technicalScore);
        mock.setJavaScore(javaScore);
        mock.setSqlScore(sqlScore);
        mock.setSpringScore(springScore);
        mock.setDsaScore(dsaScore);
        mock.setHrScore(hrScore);
        mock.setCommunicationScore(communicationScore);
        mock.setInterviewReadinessPercentage(readinessPct);
        mock.setRecommendationStatus(readiness);
        mock.setFeedbackSummary(feedbackSummary);
        mock.setCompletedAt(Instant.now());

        mock = mockInterviewRepository.save(mock);

        List<MockInterviewQuestionDto> qDtos = questions.stream()
                .map(q -> mapToQuestionDto(q, true))
                .collect(Collectors.toList());

        return MockInterviewResultDto.builder()
                .id(mock.getId())
                .title(mock.getTitle())
                .roleTitle(mock.getRoleTitle())
                .targetCompanyName(mock.getTargetCompany() != null ? mock.getTargetCompany().getName() : "General Practice")
                .interviewType(mock.getInterviewType())
                .difficulty(mock.getDifficulty())
                .totalQuestions(mock.getTotalQuestions())
                .overallScore(overallScore)
                .technicalScore(technicalScore)
                .javaScore(javaScore)
                .sqlScore(sqlScore)
                .springScore(springScore)
                .dsaScore(dsaScore)
                .hrScore(hrScore)
                .communicationScore(communicationScore)
                .interviewReadinessPercentage(readinessPct)
                .recommendationStatus(readiness)
                .feedbackSummary(feedbackSummary)
                .strongAreas(strongAreas.stream().distinct().collect(Collectors.toList()))
                .weakAreas(weakAreas.stream().distinct().collect(Collectors.toList()))
                .recommendedRevisionTopics(revisionTopics.stream().distinct().collect(Collectors.toList()))
                .questionEvaluations(qDtos)
                .createdAt(mock.getCreatedAt())
                .completedAt(mock.getCompletedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public MockInterviewResponse getMockInterview(Long mockInterviewId, Long userId) {
        MockInterview mock = mockInterviewRepository.findByIdAndUserId(mockInterviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock interview not found"));

        boolean isCompleted = mock.getStatus() == MockInterviewStatus.COMPLETED;
        return mapToResponse(mock, 1, isCompleted);
    }

    @Transactional(readOnly = true)
    public Page<MockInterviewResponse> getUserMockInterviews(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mockInterviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(m -> mapToResponse(m, 1, m.getStatus() == MockInterviewStatus.COMPLETED));
    }

    private MockInterviewResponse mapToResponse(MockInterview m, int currentIndex, boolean includeAnswers) {
        List<MockInterviewQuestionDto> qDtos = m.getQuestions().stream()
                .map(q -> mapToQuestionDto(q, includeAnswers))
                .collect(Collectors.toList());

        return MockInterviewResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .roleTitle(m.getRoleTitle())
                .targetCompanyId(m.getTargetCompany() != null ? m.getTargetCompany().getId() : null)
                .targetCompanyName(m.getTargetCompany() != null ? m.getTargetCompany().getName() : null)
                .interviewType(m.getInterviewType())
                .difficulty(m.getDifficulty())
                .totalQuestions(m.getTotalQuestions())
                .currentQuestionIndex(currentIndex)
                .status(m.getStatus())
                .overallScore(m.getOverallScore())
                .interviewReadinessPercentage(m.getInterviewReadinessPercentage())
                .recommendationStatus(m.getRecommendationStatus() != null ? m.getRecommendationStatus().name() : null)
                .questions(qDtos)
                .build();
    }

    private MockInterviewQuestionDto mapToQuestionDto(MockInterviewQuestion mq, boolean includeAnswers) {
        InterviewQuestion q = mq.getQuestion();
        List<String> missing = parseJsonList(mq.getMissingPoints());

        return MockInterviewQuestionDto.builder()
                .id(mq.getId())
                .questionId(q.getId())
                .questionOrder(mq.getQuestionOrder())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .difficulty(q.getDifficulty())
                .topicName(q.getTopic().getName())
                .categoryName(q.getTopic().getCategory().getName())
                .userAnswer(mq.getUserAnswer())
                .timeTakenSeconds(mq.getTimeTakenSeconds())
                .score(mq.getScore())
                .feedback(mq.getFeedback())
                .missingPoints(missing)
                .improvedAnswer(includeAnswers ? mq.getImprovedAnswer() : null)
                .expectedAnswer(includeAnswers ? q.getExpectedAnswer() : null)
                .explanation(includeAnswers ? q.getExplanation() : null)
                .build();
    }

    private String generateFeedbackSummary(InterviewReadiness readiness, double score, List<String> strong, List<String> weak) {
        StringBuilder sb = new StringBuilder();
        if (readiness == InterviewReadiness.READY_FOR_INTERVIEW) {
            sb.append("Outstanding performance! Score: ").append(score).append("/100. Candidate demonstrates solid grasp of core and architectural principles. ");
        } else if (readiness == InterviewReadiness.NEEDS_MORE_PREPARATION) {
            sb.append("Good effort! Score: ").append(score).append("/100. Core concepts are in place, but requires more practice in structured explanation and depth. ");
        } else {
            sb.append("Requires focused study. Score: ").append(score).append("/100. Revise fundamental topics and practice articulating concepts before scheduling live interviews. ");
        }
        if (!strong.isEmpty()) {
            sb.append("Strong in: ").append(String.join(", ", strong.stream().distinct().limit(3).collect(Collectors.toList()))).append(". ");
        }
        if (!weak.isEmpty()) {
            sb.append("Focus revision on: ").append(String.join(", ", weak.stream().distinct().limit(3).collect(Collectors.toList()))).append(".");
        }
        return sb.toString();
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.singletonList(json);
        }
    }
}
