import { apiClient } from './client';

export interface StudentNotification {
  id: number;
  title: string;
  message: string;
  notificationType: string;
  actionRoute?: string;
  isRead: boolean;
  createdAt: string;
}

export const notificationsApi = {
  list: async (unreadOnly = false): Promise<StudentNotification[]> => {
    const res = await apiClient.get<StudentNotification[]>('/student/notifications', {
      params: { unreadOnly },
    });
    return res.data;
  },

  getUnreadCount: async (): Promise<number> => {
    const res = await apiClient.get<{ unreadCount: number }>('/student/notifications/unread-count');
    return res.data.unreadCount;
  },

  markAsRead: async (id: number): Promise<void> => {
    await apiClient.patch(`/student/notifications/${id}/read`);
  },

  markAllAsRead: async (): Promise<void> => {
    await apiClient.patch('/student/notifications/read-all');
  },
};
