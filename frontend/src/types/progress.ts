// ─── Weak Topic ───────────────────────────────────────────────────────────────

export interface WeakTopicDto {
  topicId: number;
  topicTitle: string;
  totalQuestions: number;
  correctCount: number;
  accuracyPct: number;
  lastAttemptedAt: string;
}

// ─── Course Progress ──────────────────────────────────────────────────────────

export interface ProgressDto {
  courseId: number;
  courseTitle: string;
  courseSlug: string;
  topicsCompleted: number;
  totalTopics: number;
  quizAttempts: number;
  totalCorrect: number;
  totalAttemptedQuestions: number;
  overallAccuracy: number;
  lastActivityAt?: string;
  weakTopics: WeakTopicDto[];
}

// ─── Student Overall Stats ────────────────────────────────────────────────────

export interface StudentStatsDto {
  totalCoursesEnrolled: number;
  totalTopicsCompleted: number;
  totalQuizAttempts: number;
  totalCorrectAnswers: number;
  totalAttemptedQuestions: number;
  overallAccuracy: number;
  courseProgress: ProgressDto[];
}
