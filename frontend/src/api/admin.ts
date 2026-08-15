import apiClient from './client';
import type { UserDto } from '../types';

export interface AdminDashboardData {
  totalStudents: number;
  totalTrainers: number;
  totalAdmins: number;
  totalCourses: number;
  totalTopics: number;
  totalQuestions: number;
  totalQuizAttempts: number;
  averageScorePercentage: number;
}

export const adminApi = {
  getDashboard: async (): Promise<AdminDashboardData> => {
    const { data } = await apiClient.get<AdminDashboardData>('/admin/dashboard');
    return data;
  },

  getUsers: async (): Promise<UserDto[]> => {
    const { data } = await apiClient.get<UserDto[]>('/admin/users');
    return data;
  },

  toggleUserStatus: async (userId: number): Promise<UserDto> => {
    const { data } = await apiClient.patch<UserDto>(`/admin/users/${userId}/toggle-status`);
    return data;
  },

  updateUserRole: async (userId: number, role: string): Promise<UserDto> => {
    const { data } = await apiClient.put<UserDto>(`/admin/users/${userId}/role`, null, {
      params: { role },
    });
    return data;
  },
};
