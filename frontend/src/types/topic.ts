// ─── Topic Content ────────────────────────────────────────────────────────────

export interface TopicContentDto {
  id: number;
  topicId: number;
  explanation?: string;
  simpleExplanation?: string;
  realWorldExample?: string;
  syntaxExample?: string;
  codeExample?: string;
  codeLanguage?: string;
  interviewPoints?: string;
  commonMistakes?: string;
  practiceQuestions?: string;
  createdAt: string;
  updatedAt: string;
}

// ─── Topic ────────────────────────────────────────────────────────────────────

export interface TopicDto {
  id: number;
  moduleId: number;
  moduleTitle: string;
  courseId: number;
  courseTitle: string;
  title: string;
  slug: string;
  difficulty: string;
  estimatedMinutes?: number;
  displayOrder: number;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

// ─── Topic Detail ─────────────────────────────────────────────────────────────

export interface TopicDetailDto extends TopicDto {
  questionCount: number;
  content?: TopicContentDto;
}

export interface TopicRequest {
  moduleId: number;
  title: string;
  slug: string;
  difficulty?: string;
  estimatedMinutes?: number;
  displayOrder?: number;
  isPublished?: boolean;
}

export interface TopicContentRequest {
  explanation?: string;
  simpleExplanation?: string;
  realWorldExample?: string;
  syntaxExample?: string;
  codeExample?: string;
  codeLanguage?: string;
  interviewPoints?: string;
  commonMistakes?: string;
  practiceQuestions?: string;
}
