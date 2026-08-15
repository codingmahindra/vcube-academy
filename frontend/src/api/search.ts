import { apiClient } from './client';

export interface GlobalSearchResult {
  id: string;
  title: string;
  description: string;
  category: string;
  categoryLabel: string;
  route: string;
  badge?: string;
}

export const searchApi = {
  search: async (query: string): Promise<GlobalSearchResult[]> => {
    const res = await apiClient.get<GlobalSearchResult[]>('/search', {
      params: { q: query },
    });
    return res.data;
  },
};
