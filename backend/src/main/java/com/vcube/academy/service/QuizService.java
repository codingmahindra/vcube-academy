package com.vcube.academy.service;

import com.vcube.academy.dto.quiz.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private final QuizAttemptRepository attemptRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;
    private final QuizAnswerRepository answerRepository;
    private final QuizResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StudentProgressRepository progressRepository;
    private final WeakTopicRepository weakTopicRepository;

    // ─── Start Quiz ───────────────────────────────────────────────────────────

    @Transactional
    public QuizAttemptDto startQuiz(Long studentId, StartQuizRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        List<Question> questions = fetchQuestions(request);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("No questions available for the selected quiz configuration.");
        }

        // Build the attempt
        QuizAttempt attempt = QuizAttempt.builder()
                .student(student)
                .quizType(request.getQuizType())
                .difficulty(request.getDifficulty())
                .status("IN_PROGRESS")
                .totalQuestions(questions.size())
                .currentIndex(0)
                .build();

        if ("TOPIC_QUIZ".equals(request.getQuizType()) && request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + request.getTopicId()));
            attempt.setTopic(topic);
        } else if ("COURSE_QUIZ".equals(request.getQuizType()) && request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + request.getCourseId()));
            attempt.setCourse(course);
        }

        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        // Save the ordered question list
        for (int i = 0; i < questions.size(); i++) {
            AttemptQuestion aq = AttemptQuestion.builder()
                    .attempt(savedAttempt)
                    .question(questions.get(i))
                    .displayOrder(i)
                    .build();
            attemptQuestionRepository.save(aq);
        }

        // Return attempt with first question
        QuestionDto firstQuestion = toQuestionDto(questions.get(0));

        return QuizAttemptDto.builder()
                .attemptId(savedAttempt.getId())
                .quizType(savedAttempt.getQuizType())
                .topicId(attempt.getTopic() != null ? attempt.getTopic().getId() : null)
                .topicTitle(attempt.getTopic() != null ? attempt.getTopic().getTitle() : null)
                .courseId(attempt.getCourse() != null ? attempt.getCourse().getId() : null)
                .courseTitle(attempt.getCourse() != null ? attempt.getCourse().getTitle() : null)
                .difficulty(savedAttempt.getDifficulty())
                .status(savedAttempt.getStatus())
                .totalQuestions(savedAttempt.getTotalQuestions())
                .currentIndex(0)
                .startedAt(savedAttempt.getStartedAt())
                .currentQuestion(firstQuestion)
                .build();
    }

    // ─── Get Current Question ─────────────────────────────────────────────────

    public QuestionDto getCurrentQuestion(Long attemptId, Long studentId) {
        QuizAttempt attempt = getAttemptSecure(attemptId, studentId);

        if ("COMPLETED".equals(attempt.getStatus())) {
            throw new IllegalStateException("This quiz is already completed.");
        }

        List<AttemptQuestion> aqs = attemptQuestionRepository.findByAttemptIdOrdered(attemptId);
        int idx = attempt.getCurrentIndex();

        if (idx >= aqs.size()) {
            throw new IllegalStateException("All questions have been answered. Complete the quiz.");
        }

        return toQuestionDto(aqs.get(idx).getQuestion());
    }

    // ─── Submit Answer ────────────────────────────────────────────────────────

    @Transactional
    public AnswerFeedbackDto submitAnswer(Long attemptId, Long studentId, SubmitAnswerRequest request) {
        QuizAttempt attempt = getAttemptSecure(attemptId, studentId);

        if ("COMPLETED".equals(attempt.getStatus())) {
            throw new IllegalStateException("Quiz is already completed.");
        }

        // Validate the question belongs to this attempt
        List<AttemptQuestion> aqs = attemptQuestionRepository.findByAttemptIdOrdered(attemptId);
        int idx = attempt.getCurrentIndex();
        if (idx >= aqs.size()) {
            throw new IllegalStateException("No more questions to answer.");
        }

        Question currentQuestion = aqs.get(idx).getQuestion();
        if (!currentQuestion.getId().equals(request.getQuestionId())) {
            throw new IllegalArgumentException("Question ID does not match current question in sequence.");
        }

        // Check if already answered
        Optional<QuizAnswer> existingAnswer = answerRepository.findByAttemptIdAndQuestionId(attemptId, request.getQuestionId());
        if (existingAnswer.isPresent()) {
            throw new IllegalStateException("This question has already been answered.");
        }

        // Find selected option
        QuestionOption selectedOption = optionRepository.findById(request.getSelectedOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Option not found: " + request.getSelectedOptionId()));

        if (!selectedOption.getQuestion().getId().equals(currentQuestion.getId())) {
            throw new IllegalArgumentException("Selected option does not belong to this question.");
        }

        // Find correct option
        QuestionOption correctOption = currentQuestion.getOptions().stream()
                .filter(QuestionOption::getIsCorrect)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No correct option found for question " + currentQuestion.getId()));

        boolean isCorrect = selectedOption.getId().equals(correctOption.getId());

        // Save answer
        QuizAnswer answer = QuizAnswer.builder()
                .attempt(attempt)
                .question(currentQuestion)
                .selectedOption(selectedOption)
                .isCorrect(isCorrect)
                .build();
        answerRepository.save(answer);

        // Advance the index
        attempt.setCurrentIndex(idx + 1);
        attemptRepository.save(attempt);

        boolean isLast = (idx + 1) >= aqs.size();

        return AnswerFeedbackDto.builder()
                .questionId(currentQuestion.getId())
                .selectedOptionId(selectedOption.getId())
                .selectedOptionLabel(selectedOption.getOptionLabel())
                .correctOptionId(correctOption.getId())
                .correctOptionLabel(correctOption.getOptionLabel())
                .isCorrect(isCorrect)
                .explanation(currentQuestion.getExplanation())
                .interviewPoint(currentQuestion.getInterviewPoint())
                .currentIndex(idx)
                .totalQuestions(aqs.size())
                .isLastQuestion(isLast)
                .attemptId(attemptId)
                .build();
    }

    // ─── Complete Quiz ────────────────────────────────────────────────────────

    @Transactional
    public QuizResultDto completeQuiz(Long attemptId, Long studentId) {
        QuizAttempt attempt = getAttemptSecure(attemptId, studentId);

        if ("COMPLETED".equals(attempt.getStatus())) {
            // Already completed — just return the result
            return getQuizResult(attemptId, studentId);
        }

        attempt.setStatus("COMPLETED");
        attempt.setCompletedAt(Instant.now());
        attemptRepository.save(attempt);

        // Calculate result
        List<QuizAnswer> answers = answerRepository.findByAttemptId(attemptId);
        int total = attempt.getTotalQuestions();
        int attempted = answers.size();
        int correct = (int) answers.stream().filter(QuizAnswer::getIsCorrect).count();
        int wrong = attempted - correct;
        int skipped = total - attempted;

        long timeTaken = attempt.getStartedAt() != null
                ? ChronoUnit.SECONDS.between(attempt.getStartedAt(), attempt.getCompletedAt())
                : 0;

        BigDecimal scorePercentage = total > 0
                ? BigDecimal.valueOf((double) correct / total * 100).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Save result entity
        QuizResult result = QuizResult.builder()
                .attempt(attempt)
                .student(attempt.getStudent())
                .totalQuestions(total)
                .attemptedCount(attempted)
                .correctCount(correct)
                .wrongCount(wrong)
                .scorePercentage(scorePercentage)
                .timeTakenSeconds((int) timeTaken)
                .build();
        resultRepository.save(result);

        // Update student progress
        updateProgressAfterQuiz(attempt, correct, attempted);

        return buildResultDto(result, attempt, answers, skipped);
    }

    public QuizResultDto getQuizResult(Long attemptId, Long studentId) {
        QuizAttempt attempt = getAttemptSecure(attemptId, studentId);

        QuizResult result = resultRepository.findByAttemptId(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found for attempt: " + attemptId));

        List<QuizAnswer> answers = answerRepository.findByAttemptId(attemptId);
        int skipped = attempt.getTotalQuestions() - answers.size();

        return buildResultDto(result, attempt, answers, skipped);
    }

    // ─── Internal helpers ─────────────────────────────────────────────────────

    private List<Question> fetchQuestions(StartQuizRequest request) {
        int limit = 10; // default questions per quiz

        return switch (request.getQuizType()) {
            case "TOPIC_QUIZ" -> {
                if (request.getTopicId() == null) throw new IllegalArgumentException("topicId required for TOPIC_QUIZ");
                if (request.getDifficulty() != null) {
                    yield questionRepository.findRandomByTopicId(request.getTopicId(), limit);
                } else {
                    yield questionRepository.findRandomByTopicId(request.getTopicId(), limit);
                }
            }
            case "COURSE_QUIZ" -> {
                if (request.getCourseId() == null) throw new IllegalArgumentException("courseId required for COURSE_QUIZ");
                if (request.getDifficulty() != null) {
                    yield questionRepository.findRandomByCourseIdAndDifficulty(request.getCourseId(), request.getDifficulty(), limit);
                } else {
                    yield questionRepository.findRandomByCourseId(request.getCourseId(), limit);
                }
            }
            default -> questionRepository.findRandom(limit);
        };
    }

    private QuizAttempt getAttemptSecure(Long attemptId, Long studentId) {
        return attemptRepository.findByIdAndStudentId(attemptId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found: " + attemptId));
    }

    private QuestionDto toQuestionDto(Question q) {
        List<QuestionDto.OptionDto> optionDtos = q.getOptions().stream()
                .map(opt -> QuestionDto.OptionDto.builder()
                        .id(opt.getId())
                        .optionLabel(opt.getOptionLabel())
                        .optionText(opt.getOptionText())
                        // isCorrect intentionally omitted
                        .build())
                .toList();

        return QuestionDto.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .difficulty(q.getDifficulty())
                .options(optionDtos)
                .build();
    }

    private String calculateGrade(BigDecimal pct) {
        double d = pct.doubleValue();
        if (d >= 90) return "A";
        if (d >= 80) return "B";
        if (d >= 70) return "C";
        if (d >= 60) return "D";
        return "F";
    }

    private QuizResultDto buildResultDto(QuizResult result, QuizAttempt attempt, List<QuizAnswer> answers, int skipped) {
        List<QuizResultDto.AnswerReviewDto> reviews = answers.stream()
                .map(a -> {
                    QuestionOption correct = a.getQuestion().getOptions().stream()
                            .filter(QuestionOption::getIsCorrect)
                            .findFirst().orElse(null);
                    return QuizResultDto.AnswerReviewDto.builder()
                            .questionId(a.getQuestion().getId())
                            .questionText(a.getQuestion().getQuestionText())
                            .difficulty(a.getQuestion().getDifficulty())
                            .selectedOptionId(a.getSelectedOption() != null ? a.getSelectedOption().getId() : null)
                            .selectedOptionLabel(a.getSelectedOption() != null ? a.getSelectedOption().getOptionLabel() : null)
                            .selectedOptionText(a.getSelectedOption() != null ? a.getSelectedOption().getOptionText() : null)
                            .correctOptionId(correct != null ? correct.getId() : null)
                            .correctOptionLabel(correct != null ? correct.getOptionLabel() : null)
                            .correctOptionText(correct != null ? correct.getOptionText() : null)
                            .isCorrect(a.getIsCorrect())
                            .explanation(a.getQuestion().getExplanation())
                            .build();
                })
                .toList();

        return QuizResultDto.builder()
                .resultId(result.getId())
                .attemptId(attempt.getId())
                .quizType(attempt.getQuizType())
                .topicId(attempt.getTopic() != null ? attempt.getTopic().getId() : null)
                .topicTitle(attempt.getTopic() != null ? attempt.getTopic().getTitle() : null)
                .courseId(attempt.getCourse() != null ? attempt.getCourse().getId() : null)
                .courseTitle(attempt.getCourse() != null ? attempt.getCourse().getTitle() : null)
                .totalQuestions(result.getTotalQuestions())
                .attemptedCount(result.getAttemptedCount())
                .correctCount(result.getCorrectCount())
                .wrongCount(result.getWrongCount())
                .skippedCount(skipped)
                .scorePercentage(result.getScorePercentage())
                .timeTakenSeconds(result.getTimeTakenSeconds())
                .grade(calculateGrade(result.getScorePercentage()))
                .completedAt(attempt.getCompletedAt())
                .answers(reviews)
                .build();
    }

    @Transactional
    private void updateProgressAfterQuiz(QuizAttempt attempt, int correct, int attempted) {
        Long studentId = attempt.getStudent().getId();

        // Determine the course
        Long courseId = null;
        if (attempt.getCourse() != null) {
            courseId = attempt.getCourse().getId();
        } else if (attempt.getTopic() != null) {
            courseId = attempt.getTopic().getModule().getCourse().getId();
        }

        if (courseId == null) return;

        Course course = attempt.getCourse() != null
                ? attempt.getCourse()
                : attempt.getTopic().getModule().getCourse();

        Optional<StudentProgress> progressOpt = progressRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (progressOpt.isPresent()) {
            StudentProgress progress = progressOpt.get();
            progress.setQuizAttempts(progress.getQuizAttempts() + 1);
            progress.setTotalCorrect(progress.getTotalCorrect() + correct);
            progress.setTotalAttemptedQuestions(progress.getTotalAttemptedQuestions() + attempted);
            progress.setLastActivityAt(Instant.now());
            progressRepository.save(progress);
        } else {
            long totalTopics = topicRepository.countPublishedByCourseId(courseId);
            StudentProgress progress = StudentProgress.builder()
                    .student(attempt.getStudent())
                    .course(course)
                    .quizAttempts(1)
                    .totalCorrect(correct)
                    .totalAttemptedQuestions(attempted)
                    .totalTopics((int) totalTopics)
                    .lastActivityAt(Instant.now())
                    .build();
            progressRepository.save(progress);
        }

        // Update weak topics (if topic quiz)
        if (attempt.getTopic() != null) {
            Long topicId = attempt.getTopic().getId();
            Optional<WeakTopic> weakOpt = weakTopicRepository.findByStudentIdAndTopicId(studentId, topicId);
            int totalQuestionsForTopic = attempted;
            int correctForTopic = correct;

            if (weakOpt.isPresent()) {
                WeakTopic wt = weakOpt.get();
                int newTotal = wt.getTotalQuestions() + totalQuestionsForTopic;
                int newCorrect = wt.getCorrectCount() + correctForTopic;
                BigDecimal accuracy = newTotal > 0
                        ? BigDecimal.valueOf((double) newCorrect / newTotal * 100).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                wt.setTotalQuestions(newTotal);
                wt.setCorrectCount(newCorrect);
                wt.setAccuracyPct(accuracy);
                wt.setLastAttemptedAt(Instant.now());
                weakTopicRepository.save(wt);
            } else {
                BigDecimal accuracy = totalQuestionsForTopic > 0
                        ? BigDecimal.valueOf((double) correctForTopic / totalQuestionsForTopic * 100).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                WeakTopic wt = WeakTopic.builder()
                        .student(attempt.getStudent())
                        .topic(attempt.getTopic())
                        .totalQuestions(totalQuestionsForTopic)
                        .correctCount(correctForTopic)
                        .accuracyPct(accuracy)
                        .lastAttemptedAt(Instant.now())
                        .build();
                weakTopicRepository.save(wt);
            }
        }
    }
}
