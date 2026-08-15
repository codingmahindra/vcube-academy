package com.vcube.academy.service;

import com.vcube.academy.dto.career.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlacementPaperService {

    private final PlacementPaperRepository paperRepository;
    private final PlacementPaperQuestionRepository questionRepository;
    private final PlacementPaperAttemptRepository attemptRepository;
    private final PlacementPaperAnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public List<PlacementPaperSummaryDto> listPapers(User student) {
        List<PlacementPaper> papers = paperRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        List<PlacementPaperAttempt> userAttempts = attemptRepository.findByUserIdOrderByCreatedAtDesc(student.getId());

        Map<Long, List<PlacementPaperAttempt>> attemptsByPaper = userAttempts.stream()
                .collect(Collectors.groupingBy(a -> a.getPaper().getId()));

        return papers.stream().map(p -> {
            var attempts = attemptsByPaper.get(p.getId());
            boolean isAttempted = attempts != null && !attempts.isEmpty();
            Integer bestScore = attempts != null
                    ? attempts.stream().map(a -> a.getScoreObtained().intValue()).max(Integer::compareTo).orElse(null)
                    : null;

            return PlacementPaperSummaryDto.builder()
                    .id(p.getId())
                    .companyId(p.getCompany() != null ? p.getCompany().getId() : null)
                    .companyName(p.getCompany() != null ? p.getCompany().getName() : "General Assessment")
                    .title(p.getTitle())
                    .slug(p.getSlug())
                    .year(p.getYear())
                    .targetRole(p.getTargetRole())
                    .roundName(p.getRoundName())
                    .durationMinutes(p.getDurationMinutes())
                    .totalMarks(p.getTotalMarks())
                    .passingMarks(p.getPassingMarks())
                    .difficulty(p.getDifficulty())
                    .paperSource(p.getPaperSource())
                    .questionCount(p.getQuestions().size())
                    .isAttempted(isAttempted)
                    .bestScore(bestScore)
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public PlacementPaperDetailDto getPaperDetail(Long id) {
        PlacementPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement paper not found with ID: " + id));

        List<PlacementPaperQuestionDto> questions = paper.getQuestions().stream().map(q ->
                PlacementPaperQuestionDto.builder()
                        .id(q.getId())
                        .sectionName(q.getSectionName())
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .marks(q.getMarks())
                        .negativeMarks(q.getNegativeMarks())
                        .displayOrder(q.getDisplayOrder())
                        .build()
        ).toList();

        return PlacementPaperDetailDto.builder()
                .id(paper.getId())
                .companyId(paper.getCompany() != null ? paper.getCompany().getId() : null)
                .companyName(paper.getCompany() != null ? paper.getCompany().getName() : "General Assessment")
                .title(paper.getTitle())
                .slug(paper.getSlug())
                .year(paper.getYear())
                .targetRole(paper.getTargetRole())
                .roundName(paper.getRoundName())
                .durationMinutes(paper.getDurationMinutes())
                .totalMarks(paper.getTotalMarks())
                .passingMarks(paper.getPassingMarks())
                .difficulty(paper.getDifficulty())
                .paperSource(paper.getPaperSource())
                .instructions(paper.getInstructions())
                .questions(questions)
                .build();
    }

    public PlacementPaperAttemptDto startAttempt(Long paperId, User student) {
        PlacementPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Placement paper not found: " + paperId));

        PlacementPaperAttempt attempt = PlacementPaperAttempt.builder()
                .paper(paper)
                .user(student)
                .totalQuestions(paper.getQuestions().size())
                .status("IN_PROGRESS")
                .build();

        attempt = attemptRepository.save(attempt);

        List<PlacementPaperQuestionDto> questionDtos = paper.getQuestions().stream().map(q ->
                PlacementPaperQuestionDto.builder()
                        .id(q.getId())
                        .sectionName(q.getSectionName())
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .marks(q.getMarks())
                        .displayOrder(q.getDisplayOrder())
                        .build()
        ).toList();

        return PlacementPaperAttemptDto.builder()
                .id(attempt.getId())
                .paperId(paper.getId())
                .paperTitle(paper.getTitle())
                .companyName(paper.getCompany() != null ? paper.getCompany().getName() : "General")
                .durationMinutes(paper.getDurationMinutes())
                .startTime(attempt.getStartTime())
                .status(attempt.getStatus())
                .questions(questionDtos)
                .build();
    }

    public void submitAnswer(PlacementPaperAnswerRequest req, User student) {
        PlacementPaperAttempt attempt = attemptRepository.findByIdAndUserId(req.getAttemptId(), student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found: " + req.getAttemptId()));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException("Attempt is already completed or timed out");
        }

        PlacementPaperQuestion question = questionRepository.findById(req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + req.getQuestionId()));

        boolean isCorrect = question.getCorrectOption().equalsIgnoreCase(req.getSelectedOption());

        PlacementPaperAnswer answer = answerRepository.findByAttemptIdAndQuestionId(attempt.getId(), question.getId())
                .orElse(PlacementPaperAnswer.builder()
                        .attempt(attempt)
                        .question(question)
                        .build());

        answer.setSelectedOption(req.getSelectedOption());
        answer.setIsCorrect(isCorrect);
        answer.setTimeTakenSeconds(req.getTimeTakenSeconds());
        answerRepository.save(answer);
    }

    public PlacementPaperResultDto completeAttempt(Long attemptId, User student) {
        PlacementPaperAttempt attempt = attemptRepository.findByIdAndUserId(attemptId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found: " + attemptId));

        List<PlacementPaperAnswer> answers = answerRepository.findByAttemptId(attempt.getId());
        Map<Long, PlacementPaperAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        List<PlacementPaperQuestion> questions = attempt.getPaper().getQuestions();
        int correct = 0;
        int wrong = 0;
        int unanswered = 0;
        BigDecimal totalScore = BigDecimal.ZERO;

        Map<String, int[]> sectionStats = new HashMap<>(); // section -> [total, correct]

        List<PlacementPaperQuestionDto> questionDtos = new ArrayList<>();

        for (PlacementPaperQuestion q : questions) {
            sectionStats.putIfAbsent(q.getSectionName(), new int[]{0, 0});
            sectionStats.get(q.getSectionName())[0]++;

            PlacementPaperAnswer ans = answerMap.get(q.getId());
            String selected = ans != null ? ans.getSelectedOption() : null;

            if (selected == null || selected.trim().isEmpty()) {
                unanswered++;
            } else if (q.getCorrectOption().equalsIgnoreCase(selected)) {
                correct++;
                totalScore = totalScore.add(BigDecimal.valueOf(q.getMarks()));
                sectionStats.get(q.getSectionName())[1]++;
            } else {
                wrong++;
                if (q.getNegativeMarks() != null && q.getNegativeMarks().compareTo(BigDecimal.ZERO) > 0) {
                    totalScore = totalScore.subtract(q.getNegativeMarks());
                }
            }

            questionDtos.add(PlacementPaperQuestionDto.builder()
                    .id(q.getId())
                    .sectionName(q.getSectionName())
                    .questionText(q.getQuestionText())
                    .optionA(q.getOptionA())
                    .optionB(q.getOptionB())
                    .optionC(q.getOptionC())
                    .optionD(q.getOptionD())
                    .correctOption(q.getCorrectOption())
                    .explanation(q.getExplanation())
                    .selectedOption(selected)
                    .marks(q.getMarks())
                    .displayOrder(q.getDisplayOrder())
                    .build());
        }

        BigDecimal percentage = questions.isEmpty() ? BigDecimal.ZERO :
                totalScore.divide(BigDecimal.valueOf(attempt.getPaper().getTotalMarks()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

        boolean isPassed = totalScore.compareTo(BigDecimal.valueOf(attempt.getPaper().getPassingMarks())) >= 0;

        attempt.setEndTime(Instant.now());
        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(wrong);
        attempt.setUnanswered(unanswered);
        attempt.setScoreObtained(totalScore);
        attempt.setPercentage(percentage);
        attempt.setIsPassed(isPassed);
        attempt.setStatus("COMPLETED");
        attemptRepository.save(attempt);

        List<PlacementPaperResultDto.SectionScore> sectionScores = sectionStats.entrySet().stream()
                .map(e -> {
                    int secTotal = e.getValue()[0];
                    int secCorrect = e.getValue()[1];
                    BigDecimal secAcc = secTotal > 0
                            ? BigDecimal.valueOf(secCorrect).divide(BigDecimal.valueOf(secTotal), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                            : BigDecimal.ZERO;
                    return PlacementPaperResultDto.SectionScore.builder()
                            .sectionName(e.getKey())
                            .total(secTotal)
                            .correct(secCorrect)
                            .accuracy(secAcc)
                            .build();
                }).toList();

        return PlacementPaperResultDto.builder()
                .attemptId(attempt.getId())
                .paperId(attempt.getPaper().getId())
                .paperTitle(attempt.getPaper().getTitle())
                .companyName(attempt.getPaper().getCompany() != null ? attempt.getPaper().getCompany().getName() : "General")
                .totalQuestions(questions.size())
                .correctAnswers(correct)
                .wrongAnswers(wrong)
                .unanswered(unanswered)
                .scoreObtained(totalScore)
                .percentage(percentage)
                .isPassed(isPassed)
                .passingMarks(attempt.getPaper().getPassingMarks())
                .totalMarks(attempt.getPaper().getTotalMarks())
                .startTime(attempt.getStartTime())
                .endTime(attempt.getEndTime())
                .sectionScores(sectionScores)
                .questionsWithAnswers(questionDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public PlacementPaperResultDto getAttemptResult(Long attemptId, User student) {
        PlacementPaperAttempt attempt = attemptRepository.findByIdAndUserId(attemptId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found: " + attemptId));

        return completeAttempt(attempt.getId(), student);
    }
}
