import api from './client';

export type ResumeTemplate = 'ATS_CLASSIC' | 'ATS_MODERN' | 'JAVA_FULLSTACK' | 'BACKEND_DEVELOPER' | 'FRESHER';

export interface ResumeExperienceDto {
  id?: number;
  companyName: string;
  roleTitle: string;
  location?: string;
  startDate?: string;
  endDate?: string;
  isCurrent?: boolean;
  description?: string;
  bulletPoints?: string[];
  displayOrder?: number;
}

export interface ResumeEducationDto {
  id?: number;
  institution: string;
  degree: string;
  fieldOfStudy?: string;
  startYear?: string;
  endYear?: string;
  scoreOrCgpa?: string;
  displayOrder?: number;
}

export interface ResumeProjectDto {
  id?: number;
  title: string;
  techStack?: string;
  liveUrl?: string;
  githubUrl?: string;
  description?: string;
  bulletPoints?: string[];
  displayOrder?: number;
}

export interface ResumeCertificationDto {
  id?: number;
  name: string;
  issuingOrganization?: string;
  issueDate?: string;
  credentialUrl?: string;
  displayOrder?: number;
}

export interface ResumeKeywordDto {
  id?: number;
  keywordName: string;
  category: string;
  matchStatus: 'MATCHED' | 'PARTIAL_MATCH' | 'MISSING';
  importance: string;
  occurrenceCount: number;
}

export interface ResumeMissingSkillDto {
  id?: number;
  skillName: string;
  category: string;
  importance: string;
  whyItMatters: string;
  recommendedCourse?: {
    id: number;
    title: string;
    slug: string;
    difficulty: string;
  };
  recommendedDsaProblem?: {
    id: number;
    title: string;
    slug: string;
    difficulty: string;
    category: string;
  };
  recommendedInterviewQuestion?: {
    id: number;
    questionText: string;
    difficulty: string;
    round: string;
  };
  recommendedMockRole?: string;
}

export interface ResumeRecommendationDto {
  id?: number;
  sectionType: string;
  severity: 'CRITICAL' | 'WARNING' | 'SUGGESTION';
  title: string;
  message: string;
  actionableFix: string;
}

export interface ResumeAnalysisDto {
  id?: number;
  versionId: number;
  jobId?: number;
  targetJobTitle?: string;
  targetCompanyName?: string;
  overallAtsScore: number;
  keywordMatchScore: number;
  skillsMatchScore: number;
  experienceMatchScore: number;
  projectMatchScore: number;
  educationMatchScore: number;
  structureScore: number;
  aiProvider?: string;
  summaryFeedback?: string;
  matchedKeywords: ResumeKeywordDto[];
  missingKeywords: ResumeKeywordDto[];
  partialMatchedKeywords: ResumeKeywordDto[];
  criticalMissingSkills: ResumeMissingSkillDto[];
  recommendations: ResumeRecommendationDto[];
  createdAt?: string;
}

export interface ResumeVersionSummaryDto {
  id: number;
  profileId: number;
  jobId?: number;
  versionTitle: string;
  targetRole?: string;
  targetCompany?: string;
  template: ResumeTemplate;
  latestAtsScore: number;
  isPrimary: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ResumeVersionDetailDto {
  id: number;
  profileId: number;
  fullName: string;
  email: string;
  phone?: string;
  location?: string;
  linkedinUrl?: string;
  githubUrl?: string;
  portfolioUrl?: string;
  professionalSummary?: string;
  jobId?: number;
  targetJobTitle?: string;
  targetCompany?: string;
  versionTitle: string;
  template: ResumeTemplate;
  rawResumeText?: string;
  latestAtsScore: number;
  isPrimary: boolean;
  technicalSkills?: string[];
  experiences: ResumeExperienceDto[];
  educations: ResumeEducationDto[];
  projects: ResumeProjectDto[];
  certifications: ResumeCertificationDto[];
  latestAnalysis?: ResumeAnalysisDto;
  scoreHistories?: Array<{
    scoreBefore?: number;
    scoreAfter: number;
    changeSummary?: string;
    analyzedAt: string;
  }>;
  createdAt: string;
  updatedAt: string;
}

export interface ResumeDataRequest {
  fullName: string;
  email: string;
  phone?: string;
  location?: string;
  linkedinUrl?: string;
  githubUrl?: string;
  portfolioUrl?: string;
  professionalSummary?: string;
  versionTitle?: string;
  jobId?: number;
  targetRole?: string;
  targetCompany?: string;
  template?: ResumeTemplate;
  rawResumeText?: string;
  technicalSkills?: string[];
  experiences?: ResumeExperienceDto[];
  educations?: ResumeEducationDto[];
  projects?: ResumeProjectDto[];
  certifications?: ResumeCertificationDto[];
  isPrimary?: boolean;
}

export interface ResumeAnalyzeRequest {
  versionId?: number;
  resumeText?: string;
  jobId?: number;
  jobDescriptionText?: string;
  targetRole?: string;
  targetCompany?: string;
}

export interface ResumeOptimizationDto {
  optimizedSummary: string;
  optimizedBulletPoints: Array<{
    original: string;
    improvedActionOriented: string;
  }>;
  recommendedActionVerbs: string[];
  detectedWeaknesses: string[];
  suggestedCertifications: string[];
  rationale: string;
}

export const resumeApi = {
  getProfile: async () => {
    const res = await api.get('/student/resume/profile');
    return res.data;
  },

  listVersions: async (): Promise<ResumeVersionSummaryDto[]> => {
    const res = await api.get('/student/resume/versions');
    return res.data;
  },

  getVersionDetail: async (id: number): Promise<ResumeVersionDetailDto> => {
    const res = await api.get(`/student/resume/versions/${id}`);
    return res.data;
  },

  createVersion: async (data: ResumeDataRequest): Promise<ResumeVersionDetailDto> => {
    const res = await api.post('/student/resume/versions', data);
    return res.data;
  },

  updateVersion: async (id: number, data: ResumeDataRequest): Promise<ResumeVersionDetailDto> => {
    const res = await api.put(`/student/resume/versions/${id}`, data);
    return res.data;
  },

  deleteVersion: async (id: number): Promise<void> => {
    await api.delete(`/student/resume/versions/${id}`);
  },

  uploadAndExtract: async (file: File): Promise<{ extractedText: string }> => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await api.post('/student/resume/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },

  analyzeResume: async (data: ResumeAnalyzeRequest): Promise<ResumeAnalysisDto> => {
    const res = await api.post('/student/resume/analyze', data);
    return res.data;
  },

  optimizeResume: async (id: number): Promise<ResumeOptimizationDto> => {
    const res = await api.get(`/student/resume/versions/${id}/optimize`);
    return res.data;
  },

  getPdfUrl: (id: number) => `/api/student/resume/versions/${id}/pdf`,

  getTrainerStats: async () => {
    const res = await api.get('/trainer/resume/stats');
    return res.data;
  },

  getAdminAnalytics: async () => {
    const res = await api.get('/admin/resume/analytics');
    return res.data;
  },
};
