import { apiClient } from './client';

export type DsaDifficulty = 'EASY' | 'MEDIUM' | 'HARD';
export type SubmissionStatus =
  | 'PENDING'
  | 'RUNNING'
  | 'ACCEPTED'
  | 'WRONG_ANSWER'
  | 'COMPILATION_ERROR'
  | 'RUNTIME_ERROR'
  | 'TIME_LIMIT_EXCEEDED'
  | 'MEMORY_LIMIT_EXCEEDED'
  | 'SYSTEM_ERROR';

export interface DsaCategory {
  id: number;
  name: string;
  slug: string;
  description: string;
  icon?: string;
  displayOrder: number;
  totalProblems: number;
  solvedProblems: number;
}

export interface DsaProblemSummary {
  id: number;
  title: string;
  slug: string;
  difficulty: DsaDifficulty;
  categoryId: number;
  categoryName: string;
  subtopic?: string;
  companyTags?: string;
  isSolved: boolean;
  isAttempted: boolean;
}

export interface DsaTestCase {
  id: number;
  input: string;
  expectedOutput: string;
  isSample: boolean;
  isHidden: boolean;
  explanation?: string;
}

export interface DsaProblemDetail {
  id: number;
  title: string;
  slug: string;
  description: string;
  difficulty: DsaDifficulty;
  categoryId: number;
  categoryName: string;
  subtopic?: string;
  constraints?: string;
  inputFormat?: string;
  outputFormat?: string;
  expectedApproach?: string;
  timeComplexity?: string;
  spaceComplexity?: string;
  hints?: string;
  interviewPoints?: string;
  companyTags?: string;
  javaStarterCode: string;
  solutionExplanation?: string;
  solutionJavaCode?: string;
  sampleTestCases: DsaTestCase[];
  isSolved: boolean;
}

export interface TestCaseResult {
  testCaseId?: number;
  isSample: boolean;
  isHidden: boolean;
  input: string;
  expectedOutput: string;
  actualOutput: string;
  passed: boolean;
  error?: string;
}

export interface CodeExecutionResult {
  status: SubmissionStatus;
  executionTimeMs: number;
  memoryUsedKb: number;
  passedTestCases: number;
  totalTestCases: number;
  errorOutput?: string;
  testCaseResults: TestCaseResult[];
}

export interface DsaSubmissionResponse {
  id: number;
  problemId: number;
  problemTitle: string;
  language: string;
  sourceCode: string;
  status: SubmissionStatus;
  executionTimeMs?: number;
  memoryUsedKb?: number;
  passedTestCases: number;
  totalTestCases: number;
  errorOutput?: string;
  submittedAt: string;
  testCaseResults?: TestCaseResult[];
}

export interface DsaProgressSummary {
  totalProblems: number;
  solvedProblems: number;
  attemptedProblems: number;
  easySolved: number;
  easyTotal: number;
  mediumSolved: number;
  mediumTotal: number;
  hardSolved: number;
  hardTotal: number;
  successRate: number;
  totalSubmissions: number;
  categoryProgress: DsaCategory[];
}

export interface DsaProblemFilter {
  categoryId?: number;
  difficulty?: DsaDifficulty;
  search?: string;
  statusFilter?: 'ALL' | 'SOLVED' | 'UNSOLVED';
  page?: number;
  size?: number;
}

export const dsaApi = {
  getCategories: async (): Promise<DsaCategory[]> => {
    const res = await apiClient.get('/dsa/categories');
    return res.data;
  },

  getProblems: async (params?: DsaProblemFilter): Promise<{ content: DsaProblemSummary[]; totalElements: number; totalPages: number }> => {
    const res = await apiClient.get('/dsa/problems', {
      params: {
        categoryId: params?.categoryId,
        difficulty: params?.difficulty,
        search: params?.search,
        statusFilter: params?.statusFilter === 'ALL' ? undefined : params?.statusFilter,
        page: params?.page ?? 0,
        size: params?.size ?? 15,
      },
    });
    return res.data;
  },

  getProblemDetail: async (id: number): Promise<DsaProblemDetail> => {
    const res = await apiClient.get(`/dsa/problems/${id}`);
    return res.data;
  },

  runCode: async (problemId: number, sourceCode: string, language = 'JAVA'): Promise<CodeExecutionResult> => {
    const res = await apiClient.post(`/dsa/problems/${problemId}/run`, { sourceCode, language });
    return res.data;
  },

  submitCode: async (problemId: number, sourceCode: string, language = 'JAVA'): Promise<DsaSubmissionResponse> => {
    const res = await apiClient.post(`/dsa/problems/${problemId}/submit`, { sourceCode, language });
    return res.data;
  },

  getUserSubmissions: async (page = 0, size = 15): Promise<{ content: DsaSubmissionResponse[]; totalElements: number; totalPages: number }> => {
    const res = await apiClient.get('/dsa/submissions', { params: { page, size } });
    return res.data;
  },

  getProblemSubmissions: async (problemId: number, page = 0, size = 10): Promise<{ content: DsaSubmissionResponse[]; totalElements: number; totalPages: number }> => {
    const res = await apiClient.get(`/dsa/problems/${problemId}/submissions`, { params: { page, size } });
    return res.data;
  },

  getProgress: async (): Promise<DsaProgressSummary> => {
    const res = await apiClient.get('/dsa/progress');
    return res.data;
  },

  getHints: async (problemId: number): Promise<{ hints: string }> => {
    const res = await apiClient.get(`/dsa/problems/${problemId}/hints`);
    return res.data;
  },

  getSolution: async (problemId: number): Promise<{
    title: string;
    expectedApproach: string;
    timeComplexity: string;
    spaceComplexity: string;
    explanation: string;
    solutionJavaCode: string;
  }> => {
    const res = await apiClient.get(`/dsa/problems/${problemId}/solution`);
    return res.data;
  },

  // Trainer & Admin APIs
  createProblem: async (data: any): Promise<DsaProblemDetail> => {
    const res = await apiClient.post('/trainer/dsa/problems', data);
    return res.data;
  },

  updateProblem: async (id: number, data: any): Promise<DsaProblemDetail> => {
    const res = await apiClient.put(`/trainer/dsa/problems/${id}`, data);
    return res.data;
  },

  deleteProblem: async (id: number): Promise<{ message: string }> => {
    const res = await apiClient.delete(`/trainer/dsa/problems/${id}`);
    return res.data;
  },

  getAdminStats: async (): Promise<any> => {
    const res = await apiClient.get('/admin/dsa/dashboard');
    return res.data;
  },
};
