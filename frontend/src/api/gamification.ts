import { apiClient } from './client';

export interface Badge {
  id?: number;
  badgeCode: string;
  badgeName: string;
  description: string;
  iconName: string;
  category: string;
  earnedAt?: string;
  isUnlocked: boolean;
}

export interface GamificationSummary {
  studentId: number;
  currentStreakDays: number;
  longestStreakDays: number;
  totalXpPoints: number;
  unlockedBadgesCount: number;
  totalBadgesCount: number;
  badges: Badge[];
  nextMilestoneGoal: string;
}

export const gamificationApi = {
  getSummary: async (): Promise<GamificationSummary> => {
    const res = await apiClient.get<GamificationSummary>('/student/gamification/summary');
    return res.data;
  },
};
