import apiClient from './client';
import type { StudentStatsDto, ProgressDto, WeakTopicDto } from '../types/progress';

export const progressApi = {
  /** Get overall student statistics */
  getStats: () =>
    apiClient.get<StudentStatsDto>('/progress/stats').then((r) => r.data),

  /** Get per-course progress list */
  getCourseProgress: () =>
    apiClient.get<ProgressDto[]>('/progress/courses').then((r) => r.data),

  /** Get list of weak topics (accuracy < 60%) */
  getWeakTopics: () =>
    apiClient.get<WeakTopicDto[]>('/progress/weak-topics').then((r) => r.data),
};
