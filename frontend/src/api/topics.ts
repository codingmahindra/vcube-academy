import apiClient from './client';
import type { TopicDto, TopicDetailDto } from '../types/topic';

export const topicsApi = {
  /** Get all topics in a module */
  getByModule: (moduleId: number) =>
    apiClient.get<TopicDto[]>(`/topics/module/${moduleId}`).then((r) => r.data),

  /** Get full topic detail including content */
  getById: (id: number) =>
    apiClient.get<TopicDetailDto>(`/topics/${id}`).then((r) => r.data),

  /** Check if the authenticated student has completed a topic */
  checkCompletion: (id: number) =>
    apiClient
      .get<{ completed: boolean }>(`/topics/${id}/completion`)
      .then((r) => r.data.completed),

  /** Mark a topic as completed */
  markComplete: (id: number) =>
    apiClient.post<{ message: string }>(`/topics/${id}/complete`).then((r) => r.data),
};
