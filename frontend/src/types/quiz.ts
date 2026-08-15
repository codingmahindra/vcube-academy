// ─── Quiz Option ─────────────────────────────────────────────────────────────

export interface QuestionOptionDto {
  id: number;
  optionLabel: string; // A, B, C, D
  optionText: string;
  // isCorrect is intentionally never sent before submission
}

// ─── Question (during quiz) ───────────────────────────────────────────────────

export interface QuestionDto {
  id: number;
  questionText: string;
  difficulty: string;
  options: QuestionOptionDto[];
}

// ─── Quiz Attempt ─────────────────────────────────────────────────────────────

export interface QuizAttemptDto {
  attemptId: number;
  quizType: string;
  topicId?: number;
  topicTitle?: string;
  courseId?: number;
  courseTitle?: string;
  difficulty?: string;
  status: string;
  totalQuestions: number;
  currentIndex: number;
  startedAt: string;
  currentQuestion: QuestionDto;
}

// ─── Answer Feedback (after submitting one answer) ────────────────────────────

export interface AnswerFeedbackDto {
  questionId: number;
  selectedOptionId: number;
  selectedOptionLabel: string;
  correctOptionId: number;
  correctOptionLabel: string;
  isCorrect: boolean;
  explanation?: string;
  interviewPoint?: string;
  currentIndex: number;
  totalQuestions: number;
  isLastQuestion: boolean;
  attemptId: number;
}

// ─── Quiz Result ─────────────────────────────────────────────────────────────

export interface AnswerReviewDto {
  questionId: number;
  questionText: string;
  difficulty: string;
  selectedOptionId?: number;
  selectedOptionLabel?: string;
  selectedOptionText?: string;
  correctOptionId?: number;
  correctOptionLabel?: string;
  correctOptionText?: string;
  isCorrect: boolean;
  explanation?: string;
}

export interface QuizResultDto {
  resultId: number;
  attemptId: number;
  quizType: string;
  topicId?: number;
  topicTitle?: string;
  courseId?: number;
  courseTitle?: string;
  totalQuestions: number;
  attemptedCount: number;
  correctCount: number;
  wrongCount: number;
  skippedCount: number;
  scorePercentage: number;
  timeTakenSeconds: number;
  grade: string;
  completedAt: string;
  answers: AnswerReviewDto[];
}

// ─── Start Quiz Request ───────────────────────────────────────────────────────

export interface StartQuizRequest {
  quizType: 'TOPIC_QUIZ' | 'COURSE_QUIZ' | 'RANDOM_QUIZ';
  topicId?: number;
  courseId?: number;
  difficulty?: string;
}

// ─── Submit Answer Request ────────────────────────────────────────────────────

export interface SubmitAnswerRequest {
  questionId: number;
  selectedOptionId: number;
}
