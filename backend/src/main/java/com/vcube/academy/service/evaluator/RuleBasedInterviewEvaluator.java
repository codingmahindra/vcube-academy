package com.vcube.academy.service.evaluator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcube.academy.entity.InterviewQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleBasedInterviewEvaluator implements InterviewAnswerEvaluator {

    private final ObjectMapper objectMapper;

    @Override
    public InterviewEvaluationResultDto evaluate(InterviewQuestion question, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return InterviewEvaluationResultDto.builder()
                    .score(0.0)
                    .technicalAccuracyScore(0.0)
                    .completenessScore(0.0)
                    .clarityScore(0.0)
                    .feedback("No answer was provided. Please articulate your response.")
                    .strengths(List.of())
                    .weaknesses(List.of("Answer was completely empty"))
                    .missingPoints(List.of("All key technical concepts"))
                    .improvedAnswer(question.getExpectedAnswer())
                    .build();
        }

        String answerLower = userAnswer.toLowerCase().trim();
        List<String> keywords = parseJsonList(question.getEvaluationKeywords());
        List<String> mistakes = parseJsonList(question.getCommonMistakes());

        // 1. Technical Accuracy / Keyword Hit Ratio
        int matchedKeywords = 0;
        List<String> missingKeywords = new ArrayList<>();
        for (String kw : keywords) {
            if (answerLower.contains(kw.toLowerCase().trim())) {
                matchedKeywords++;
            } else {
                missingKeywords.add(kw);
            }
        }

        double keywordRatio = keywords.isEmpty() ? 0.8 : (double) matchedKeywords / keywords.size();
        double technicalAccuracy = Math.min(100.0, keywordRatio * 100.0);

        // 2. Completeness (Word Count & Concept Depth)
        int wordCount = answerLower.split("\\s+").length;
        double completeness;
        if (wordCount < 10) {
            completeness = 25.0;
        } else if (wordCount < 30) {
            completeness = 55.0;
        } else if (wordCount < 75) {
            completeness = 85.0;
        } else {
            completeness = 95.0;
        }

        // 3. Clarity & Structure
        double clarity = 75.0;
        if (userAnswer.contains("\n") || userAnswer.contains("1.") || userAnswer.contains("-") || userAnswer.contains(":")) {
            clarity += 20.0;
        }
        if (wordCount > 5) {
            clarity = Math.min(100.0, clarity);
        }

        // 4. Penalty for Common Mistakes
        List<String> identifiedMistakes = new ArrayList<>();
        double mistakePenalty = 0.0;
        for (String mistake : mistakes) {
            String mLower = mistake.toLowerCase();
            // simple check if user phrased common mistake positively
            if (mLower.length() > 5 && answerLower.contains(mLower.substring(0, Math.min(mLower.length(), 20)))) {
                identifiedMistakes.add(mistake);
                mistakePenalty += 15.0;
            }
        }

        // Combined Final Score (0 - 100)
        double rawScore = (technicalAccuracy * 0.50) + (completeness * 0.30) + (clarity * 0.20) - mistakePenalty;
        double finalScore = Math.max(10.0, Math.min(100.0, Math.round(rawScore * 10.0) / 10.0));

        // Strengths & Weaknesses
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();

        if (matchedKeywords > 0) {
            strengths.add("Demonstrated core technical understanding (" + matchedKeywords + " primary concepts covered)");
        }
        if (wordCount >= 30) {
            strengths.add("Good elaboration and depth in response");
        }
        if (clarity >= 85.0) {
            strengths.add("Structured, articulate delivery");
        }

        if (missingKeywords.size() > 0 && missingKeywords.size() <= 4) {
            weaknesses.add("Missed mentioning: " + String.join(", ", missingKeywords));
        } else if (missingKeywords.size() > 4) {
            weaknesses.add("Omitted several critical technical keywords");
        }
        if (wordCount < 20) {
            weaknesses.add("Answer is too brief for a senior interview; expand on edge cases and trade-offs");
        }
        if (!identifiedMistakes.isEmpty()) {
            weaknesses.addAll(identifiedMistakes);
        }

        String feedback;
        if (finalScore >= 80.0) {
            feedback = "Excellent response! Clear technical precision, good terminology, and solid interview readiness.";
        } else if (finalScore >= 55.0) {
            feedback = "Good foundation, but lacks deeper technical nuance. Include architectural trade-offs or specific Java/SQL internals.";
        } else {
            feedback = "Needs significant preparation. Ensure you understand the underlying concepts, keywords, and real-world mechanisms.";
        }

        return InterviewEvaluationResultDto.builder()
                .score(finalScore)
                .technicalAccuracyScore(technicalAccuracy)
                .completenessScore(completeness)
                .clarityScore(clarity)
                .feedback(feedback)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .missingPoints(missingKeywords)
                .improvedAnswer(question.getExpectedAnswer())
                .build();
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
