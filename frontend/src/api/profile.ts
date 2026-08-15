import { apiClient } from './client';

export interface StudentProfile {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  college?: string;
  degree?: string;
  graduationYear?: string;
  cgpa?: number;
  bio?: string;
  linkedinUrl?: string;
  githubUrl?: string;
  portfolioUrl?: string;
  technicalSkills?: string[];
  targetRoles?: string[];
  preferredLocations?: string[];
  includeInResume: boolean;
  includeInAtsAnalysis: boolean;
  includeInCopilot: boolean;
}

export interface StudentProfileUpdatePayload {
  fullName: string;
  phone?: string;
  college?: string;
  degree?: string;
  graduationYear?: string;
  cgpa?: number;
  bio?: string;
  linkedinUrl?: string;
  githubUrl?: string;
  portfolioUrl?: string;
  technicalSkills?: string[];
  targetRoles?: string[];
  preferredLocations?: string[];
  includeInResume?: boolean;
  includeInAtsAnalysis?: boolean;
  includeInCopilot?: boolean;
}

export const profileApi = {
  getProfile: async (): Promise<StudentProfile> => {
    const res = await apiClient.get<StudentProfile>('/student/profile');
    return res.data;
  },

  updateProfile: async (payload: StudentProfileUpdatePayload): Promise<StudentProfile> => {
    const res = await apiClient.put<StudentProfile>('/student/profile', payload);
    return res.data;
  },
};
