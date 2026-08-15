import { apiClient } from './client';

export type InterviewQuestionType =
  | 'CONCEPTUAL'
  | 'CODING'
  | 'OUTPUT_BASED'
  | 'SCENARIO_BASED'
  | 'DEBUGGING'
  | 'SYSTEM_DESIGN'
  | 'HR_BEHAVIORAL';

export type InterviewDifficulty = 'BASIC' | 'INTERMEDIATE' | 'ADVANCED';

export type InterviewRoundType =
  | 'ROUND_1_APTITUDE_ONLINE'
  | 'ROUND_2_CODING_TECHNICAL'
  | 'ROUND_3_TECHNICAL'
  | 'ROUND_4_MANAGERIAL_HR';

export type QuestionSource =
  | 'VERIFIED_COMPANY_QUESTION'
  | 'REPORTED_PLACEMENT_QUESTION'
  | 'PRACTICE_QUESTION'
  | 'AI_GENERATED_PRACTICE';

export type MockInterviewStatus = 'IN_PROGRESS' | 'COMPLETED' | 'ABANDONED';

export type InterviewReadiness = 'READY_FOR_INTERVIEW' | 'NEEDS_MORE_PREPARATION' | 'NOT_READY_YET';

export interface InterviewTopicDto {
  id: number;
  categoryId: number;
  categoryName: string;
  name: string;
  slug: string;
  description: string;
  displayOrder: number;
  totalQuestions: number;
  completedQuestions: number;
}

export interface InterviewCategoryDto {
  id: number;
  name: string;
  slug: string;
  description: string;
  icon?: string;
  displayOrder: number;
  totalTopics: number;
  totalQuestions: number;
  completedQuestions: number;
  topics: InterviewTopicDto[];
}

export interface CompanySummaryDto {
  id: number;
  name: string;
  slug: string;
  logoUrl?: string;
  description?: string;
  industry?: string;
  tier?: string;
  totalQuestions: number;
}

export interface InterviewQuestionSummaryDto {
  id: number;
  topicId: number;
  topicName: string;
  categoryId: number;
  categoryName: string;
  questionText: string;
  questionType: InterviewQuestionType;
  difficulty: InterviewDifficulty;
  interviewRound: InterviewRoundType;
  questionSource: QuestionSource;
  sourceReference?: string;
  isCompleted: boolean;
  lastScore?: number;
  companies?: string[];
}

export interface CompanyDetailDto extends CompanySummaryDto {
  hiringRoundsInfo?: string;
  questions: InterviewQuestionSummaryDto[];
}

export interface InterviewQuestionDetailDto extends InterviewQuestionSummaryDto {
  expectedAnswer: string;
  explanation: string;
  interviewPoints?: string;
  commonMistakes?: string;
  followUpQuestions?: string;
  realWorldExample?: string;
  practiceCount: number;
}

export interface InterviewEvaluationResponse {
  evaluationId: number;
  questionId: number;
  score: number;
  feedback: string;
  strengths: string[];
  weaknesses: string[];
  missingPoints: string[];
  improvedAnswer: string;
  expectedAnswer: string;
  explanation: string;
}

export interface MockInterviewStartRequest {
  roleTitle?: string;
  targetCompanyId?: number;
  interviewType?: string;
  difficulty?: string;
  totalQuestions?: number;
}

export interface MockInterviewQuestionDto {
  id: number;
  questionId: number;
  questionOrder: number;
  questionText: string;
  questionType: InterviewQuestionType;
  difficulty: InterviewDifficulty;
  topicName: string;
  categoryName: string;
  userAnswer?: string;
  timeTakenSeconds?: number;
  score?: number;
  feedback?: string;
  missingPoints?: string[];
  improvedAnswer?: string;
  expectedAnswer?: string;
  explanation?: string;
}

export interface MockInterviewResponse {
  id: number;
  title: string;
  roleTitle: string;
  targetCompanyId?: number;
  targetCompanyName?: string;
  interviewType: string;
  difficulty: string;
  totalQuestions: number;
  currentQuestionIndex: number;
  status: MockInterviewStatus;
  overallScore?: number;
  interviewReadinessPercentage?: number;
  recommendationStatus?: string;
  questions: MockInterviewQuestionDto[];
}

export interface MockInterviewResultDto {
  id: number;
  title: string;
  roleTitle: string;
  targetCompanyName: string;
  interviewType: string;
  difficulty: string;
  totalQuestions: number;
  overallScore: number;
  technicalScore: number;
  javaScore: number;
  sqlScore: number;
  springScore: number;
  dsaScore: number;
  hrScore: number;
  communicationScore: number;
  interviewReadinessPercentage: number;
  recommendationStatus: InterviewReadiness;
  feedbackSummary: string;
  strongAreas: string[];
  weakAreas: string[];
  recommendedRevisionTopics: string[];
  questionEvaluations: MockInterviewQuestionDto[];
  createdAt: string;
  completedAt: string;
}

export interface InterviewProgressSummaryDto {
  totalQuestions: number;
  completedQuestions: number;
  readinessPercentage: number;
  totalMockInterviews: number;
  completedMockInterviews: number;
  averageMockScore: number;
  categoryProgress: InterviewCategoryDto[];
  strongTopics: string[];
  weakTopics: string[];
}

export const interviewApi = {
  getCategories: async (): Promise<InterviewCategoryDto[]> => {
    const res = await apiClient.get('/interview/categories');
    return res.data;
  },

  getTopics: async (categoryId?: number): Promise<InterviewTopicDto[]> => {
    const res = await apiClient.get('/interview/topics', { params: { categoryId } });
    return res.data;
  },

  getCompanies: async (): Promise<CompanySummaryDto[]> => {
    const res = await apiClient.get('/interview/companies');
    return res.data;
  },

  getCompanyDetail: async (id: number): Promise<CompanyDetailDto> => {
    const res = await apiClient.get(`/interview/companies/${id}`);
    return res.data;
  },

  searchQuestions: async (params?: {
    topicId?: number;
    categoryId?: number;
    difficulty?: InterviewDifficulty;
    type?: InterviewQuestionType;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<{ content: InterviewQuestionSummaryDto[]; totalElements: number; totalPages: number }> => {
    const res = await apiClient.get('/interview/questions', { params });
    return res.data;
  },

  getQuestionDetail: async (id: number): Promise<InterviewQuestionDetailDto> => {
    const res = await apiClient.get(`/interview/questions/${id}`);
    return res.data;
  },

  evaluatePracticeAnswer: async (questionId: number, userAnswer: string): Promise<InterviewEvaluationResponse> => {
    const res = await apiClient.post(`/interview/questions/${questionId}/evaluate`, { userAnswer });
    return res.data;
  },

  startMockInterview: async (data: MockInterviewStartRequest): Promise<MockInterviewResponse> => {
    const res = await apiClient.post('/interview/mock/start', data);
    return res.data;
  },

  answerMockQuestion: async (
    mockId: number,
    questionOrder: number,
    userAnswer: string,
    timeTakenSeconds = 60
  ): Promise<MockInterviewQuestionDto> => {
    const res = await apiClient.post(`/interview/mock/${mockId}/answer`, {
      questionOrder,
      userAnswer,
      timeTakenSeconds,
    });
    return res.data;
  },

  completeMockInterview: async (mockId: number): Promise<MockInterviewResultDto> => {
    const res = await apiClient.post(`/interview/mock/${mockId}/complete`);
    return res.data;
  },

  getMockInterview: async (mockId: number): Promise<MockInterviewResponse> => {
    const res = await apiClient.get(`/interview/mock/${mockId}`);
    return res.data;
  },

  getUserMockInterviews: async (page = 0, size = 10): Promise<{ content: MockInterviewResponse[]; totalElements: number; totalPages: number }> => {
    const res = await apiClient.get('/interview/mock', { params: { page, size } });
    return res.data;
  },

  getProgress: async (): Promise<InterviewProgressSummaryDto> => {
    const res = await apiClient.get('/interview/progress');
    return res.data;
  },

  getRecommendations: async (): Promise<{
    recommendedQuestions: InterviewQuestionSummaryDto[];
    recommendedRevisionTopics: string[];
    targetCompanies: string[];
  }> => {
    const res = await apiClient.get('/interview/recommendations');
    return res.data;
  },

  // Trainer / Admin
  createQuestion: async (data: any): Promise<InterviewQuestionDetailDto> => {
    const res = await apiClient.post('/trainer/interview/questions', data);
    return res.data;
  },

  updateQuestion: async (id: number, data: any): Promise<InterviewQuestionDetailDto> => {
    const res = await apiClient.put(`/trainer/interview/questions/${id}`, data);
    return res.data;
  },

  deleteQuestion: async (id: number): Promise<{ message: string }> => {
    const res = await apiClient.delete(`/trainer/interview/questions/${id}`);
    return res.data;
  },

  createCompany: async (data: any): Promise<any> => {
    const res = await apiClient.post('/admin/interview/companies', data);
    return res.data;
  },

  deleteCompany: async (id: number): Promise<{ message: string }> => {
    const res = await apiClient.delete(`/admin/interview/companies/${id}`);
    return res.data;
  },

  getAdminStats: async (): Promise<any> => {
    const res = await apiClient.get('/admin/interview/dashboard');
    return res.data;
  },
};
