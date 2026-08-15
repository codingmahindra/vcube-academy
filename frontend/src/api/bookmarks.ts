import { apiClient } from './client';

export type BookmarkItemType =
  | 'TOPIC'
  | 'MCQ'
  | 'DSA_PROBLEM'
  | 'INTERVIEW_QUESTION'
  | 'JOB'
  | 'PLACEMENT_PAPER';

export interface Bookmark {
  id: number;
  itemType: BookmarkItemType;
  itemId: number;
  itemTitle: string;
  itemSubtitle?: string;
  itemRoute: string;
  createdAt: string;
}

export interface BookmarkCreatePayload {
  itemType: BookmarkItemType;
  itemId: number;
  itemTitle: string;
  itemSubtitle?: string;
  itemRoute: string;
}

export const bookmarksApi = {
  list: async (itemType?: BookmarkItemType): Promise<Bookmark[]> => {
    const res = await apiClient.get<Bookmark[]>('/student/bookmarks', {
      params: itemType ? { itemType } : {},
    });
    return res.data;
  },

  add: async (payload: BookmarkCreatePayload): Promise<Bookmark> => {
    const res = await apiClient.post<Bookmark>('/student/bookmarks', payload);
    return res.data;
  },

  remove: async (itemType: BookmarkItemType, itemId: number): Promise<void> => {
    await apiClient.delete(`/student/bookmarks/${itemType}/${itemId}`);
  },

  check: async (itemType: BookmarkItemType, itemId: number): Promise<boolean> => {
    const res = await apiClient.get<boolean>(`/student/bookmarks/check/${itemType}/${itemId}`);
    return res.data;
  },
};
