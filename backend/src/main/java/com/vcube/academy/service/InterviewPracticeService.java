package com.vcube.academy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.dto.interview.InterviewEvaluationRequest;
import com.vcube.academy.dto.interview.InterviewEvaluationResponse;
import com.vcube.academy.entity.InterviewEvaluation;
import com.vcube.academy.entity.InterviewQuestion;
import com.vcube.academy.entity.InterviewStudentProgress;
import com.vcube.academy.entity.User;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.InterviewEvaluationRepository;
import com.vcube.academy.repository.InterviewQuestionRepository;
import com.vcube.academy.repository.InterviewStudentProgressRepository;
import com.vcube.academy.repository.UserRepository;
import com.vcube.academy.service.evaluator.InterviewAnswerEvaluator;
import com.vcube.academy.service.evaluator.InterviewEvaluationResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewPracticeService {

    private final InterviewQuestionRepository questionRepository;
    private final InterviewEvaluationRepository evaluationRepository;
    private final InterviewStudentProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final InterviewAnswerEvaluator answerEvaluator;
    private final ObjectMapper objectMapper;

    @Transactional
    public InterviewEvaluationResponse evaluateAndSavePracticeAnswer(Long questionId, InterviewEvaluationRequest request, Long userId) {
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found with id: " + questionId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Evaluate answer
        InterviewEvaluationResultDto evalResult = answerEvaluator.evaluate(question, request.getUserAnswer());

        // Save Evaluation record
        InterviewEvaluation eval = InterviewEvaluation.builder()
                .user(user)
                .question(question)
                .userAnswer(request.getUserAnswer())
                .score(evalResult.getScore())
                .feedback(evalResult.getFeedback())
                .strengths(writeJson(evalResult.getStrengths()))
                .weaknesses(writeJson(evalResult.getWeaknesses()))
                .missingPoints(writeJson(evalResult.getMissingPoints()))
                .improvedAnswer(evalResult.getImprovedAnswer())
                .evaluatedAt(Instant.now())
                .build();
        eval = evaluationRepository.save(eval);

        // Update Student Progress
        InterviewStudentProgress progress = progressRepository.findByUserIdAndQuestionId(userId, questionId)
                .orElseGet(() -> InterviewStudentProgress.builder()
                        .user(user)
                        .question(question)
                        .practiceCount(0)
                        .build());

        progress.setPracticeCount(progress.getPracticeCount() + 1);
        progress.setLastScore(evalResult.getScore());
        progress.setLastPracticedAt(Instant.now());
        if (evalResult.getScore() >= 60.0) {
            progress.setIsCompleted(true);
        }
        progressRepository.save(progress);

        return InterviewEvaluationResponse.builder()
                .evaluationId(eval.getId())
                .questionId(question.getId())
                .score(evalResult.getScore())
                .feedback(evalResult.getFeedback())
                .strengths(evalResult.getStrengths())
                .weaknesses(evalResult.getWeaknesses())
                .missingPoints(evalResult.getMissingPoints())
                .improvedAnswer(evalResult.getImprovedAnswer())
                .expectedAnswer(question.getExpectedAnswer())
                .explanation(question.getExplanation())
                .build();
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }
}
