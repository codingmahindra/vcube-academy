import apiClient from './client';
import type { CourseDto, CourseDetailDto, CourseRequest, CourseModuleRequest } from '../types/course';
import type { TopicDto, TopicDetailDto, TopicRequest, TopicContentRequest } from '../types/topic';

export interface TrainerDashboardData {
  totalStudents: number;
  totalCourses: number;
  totalTopics: number;
  totalQuestions: number;
  totalAttempts: number;
  averageScorePercentage: number;
  recentResults: Array<{
    id: number;
    studentName: string;
    studentEmail: string;
    totalQuestions: number;
    correctAnswers: number;
    scorePercentage: number;
    passed: boolean;
    createdAt: string;
  }>;
  studentProgress: Array<{
    id: number;
    studentName: string;
    courseTitle: string;
    completedTopicsCount: number;
    completionPercentage: number;
    quizAverageScore: number;
    lastActivityAt?: string;
  }>;
}

export interface QuestionOptionAdmin {
  id?: number;
  optionLabel: string;
  optionText: string;
  isCorrect: boolean;
  whyWrong?: string;
}

export interface QuestionAdmin {
  id: number;
  topicId?: number;
  topicTitle?: string;
  courseId?: number;
  courseTitle?: string;
  questionText: string;
  difficulty: string;
  explanation?: string;
  interviewPoint?: string;
  companyTags?: string;
  isActive: boolean;
  options: QuestionOptionAdmin[];
}

export interface QuestionRequestData {
  topicId?: number;
  courseId?: number;
  questionText: string;
  difficulty: string;
  explanation?: string;
  interviewPoint?: string;
  companyTags?: string;
  isActive?: boolean;
  options: Array<{
    id?: number;
    optionLabel: string;
    optionText: string;
    isCorrect: boolean;
    whyWrong?: string;
  }>;
}

export const trainerApi = {
  getDashboard: async (): Promise<TrainerDashboardData> => {
    const { data } = await apiClient.get<TrainerDashboardData>('/trainer/dashboard');
    return data;
  },

  // Courses
  createCourse: async (payload: CourseRequest): Promise<CourseDto> => {
    const { data } = await apiClient.post<CourseDto>('/courses', payload);
    return data;
  },

  updateCourse: async (id: number, payload: CourseRequest): Promise<CourseDto> => {
    const { data } = await apiClient.put<CourseDto>(`/courses/${id}`, payload);
    return data;
  },

  deleteCourse: async (id: number): Promise<void> => {
    await apiClient.delete(`/courses/${id}`);
  },

  addModule: async (courseId: number, payload: CourseModuleRequest) => {
    const { data } = await apiClient.post(`/courses/${courseId}/modules`, payload);
    return data;
  },

  deleteModule: async (moduleId: number) => {
    await apiClient.delete(`/courses/modules/${moduleId}`);
  },

  // Topics
  createTopic: async (payload: TopicRequest): Promise<TopicDto> => {
    const { data } = await apiClient.post<TopicDto>('/topics', payload);
    return data;
  },

  updateTopic: async (id: number, payload: TopicRequest): Promise<TopicDto> => {
    const { data } = await apiClient.put<TopicDto>(`/topics/${id}`, payload);
    return data;
  },

  deleteTopic: async (id: number): Promise<void> => {
    await apiClient.delete(`/topics/${id}`);
  },

  saveTopicContent: async (topicId: number, payload: TopicContentRequest) => {
    const { data } = await apiClient.post(`/topics/${topicId}/content`, payload);
    return data;
  },

  // Questions
  getQuestions: async (topicId?: number, courseId?: number): Promise<QuestionAdmin[]> => {
    const { data } = await apiClient.get<QuestionAdmin[]>('/questions', {
      params: { topicId, courseId },
    });
    return data;
  },

  createQuestion: async (payload: QuestionRequestData): Promise<QuestionAdmin> => {
    const { data } = await apiClient.post<QuestionAdmin>('/questions', payload);
    return data;
  },

  updateQuestion: async (id: number, payload: QuestionRequestData): Promise<QuestionAdmin> => {
    const { data } = await apiClient.put<QuestionAdmin>(`/questions/${id}`, payload);
    return data;
  },

  deleteQuestion: async (id: number): Promise<void> => {
    await apiClient.delete(`/questions/${id}`);
  },
};
