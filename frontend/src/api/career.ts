import { apiClient } from './client';

export interface CareerDashboard {
  userId: number;
  studentName: string;
  enrolledCoursesCount: number;
  completedTopicsCount: number;
  mcqQuizzesAttempted: number;
  mcqAccuracyPercentage: number;
  dsaProblemsSolved: number;
  mockInterviewsCompleted: number;
  averageMockScore: number;
  primaryResumeAtsScore: number;
  jobApplicationsSubmitted: number;
  placementDrivesAttended: number;
  placementPapersAttempted: number;
  profileCompletionPercentage: number;
  interviewReadinessStatus: string;
  currentRoadmapStage: string;
  weakAreasCount: number;
}

export interface CareerRoadmapStage {
  stage: string;
  stageName: string;
  description: string;
  status: 'COMPLETED' | 'IN_PROGRESS' | 'LOCKED';
  progressPercentage: number;
  metricsSummary: string;
  recommendedAction: string;
  actionRoute: string;
}

export interface CareerRoadmap {
  userId: number;
  targetRole: string;
  overallReadinessPercentage: number;
  currentStage: string;
  stages: CareerRoadmapStage[];
}

export interface ActionRecommendation {
  label: string;
  actionType: string;
  link: string;
}

export interface CopilotChatResponse {
  conversationId: number;
  responseText: string;
  aiProvider: string;
  recommendedActions: ActionRecommendation[];
  createdAt: string;
}

export interface DailyPlanItem {
  id: number;
  title: string;
  category: string;
  description: string;
  targetCount: number;
  completedCount: number;
  isCompleted: boolean;
  actionRoute: string;
  actionLabel: string;
}

export interface DailyPlan {
  id: number;
  planDate: string;
  totalTasks: number;
  completedTasks: number;
  completionPercentage: number;
  items: DailyPlanItem[];
}

export interface WeakArea {
  id: number;
  skillName: string;
  category: string;
  severity: 'CRITICAL' | 'MODERATE' | 'MILD';
  accuracyRate: number;
  recommendedTopicId?: number;
  recommendedTopicTitle?: string;
  recommendedDsaSlug?: string;
  actionPlan: string;
}

export interface PlacementPaperSummary {
  id: number;
  companyId?: number;
  companyName: string;
  title: string;
  slug: string;
  year: string;
  targetRole: string;
  roundName: string;
  durationMinutes: number;
  totalMarks: number;
  passingMarks: number;
  difficulty: string;
  paperSource: 'VERIFIED' | 'REPORTED' | 'PRACTICE' | 'AI_GENERATED';
  questionCount: number;
  isAttempted?: boolean;
  bestScore?: number;
}

export interface PlacementPaperQuestion {
  id: number;
  sectionName: string;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  marks: number;
  displayOrder: number;
  selectedOption?: string;
  isCorrect?: boolean;
  explanation?: string;
}

export interface PlacementPaperDetail extends PlacementPaperSummary {
  instructions: string;
  questions: PlacementPaperQuestion[];
}

export interface PlacementPaperAttempt {
  id: number;
  paperId: number;
  paperTitle: string;
  durationMinutes: number;
  totalQuestions: number;
  startedAt: string;
  questions: PlacementPaperQuestion[];
}

export interface SectionScore {
  sectionName: string;
  totalQuestions: number;
  correctAnswers: number;
  score: number;
  accuracyPercentage: number;
}

export interface PlacementPaperResult {
  attemptId: number;
  paperId: number;
  paperTitle: string;
  companyName: string;
  totalMarks: number;
  passingMarks: number;
  scoreObtained: number;
  percentage: number;
  isPassed: boolean;
  correctAnswers: number;
  wrongAnswers: number;
  unanswered: number;
  totalQuestions: number;
  timeSpentSeconds: number;
  completedAt: string;
  sectionScores: SectionScore[];
  questions: PlacementPaperQuestion[];
}

export interface CompanyPrepHub {
  companyId: number;
  companyName: string;
  companySlug: string;
  verifiedQuestionsCount: number;
  reportedQuestionsCount: number;
  placementPapers: PlacementPaperSummary[];
  interviewQuestions: any[];
  dsaProblems: any[];
}

export const careerApi = {
  getDashboard: async (): Promise<CareerDashboard> => {
    const res = await apiClient.get<CareerDashboard>('/student/career/dashboard');
    return res.data;
  },

  getRoadmap: async (): Promise<CareerRoadmap> => {
    const res = await apiClient.get<CareerRoadmap>('/student/career/roadmap');
    return res.data;
  },

  chatWithCopilot: async (message: string, conversationId?: number): Promise<CopilotChatResponse> => {
    const res = await apiClient.post<CopilotChatResponse>('/student/career/copilot/chat', {
      message,
      conversationId,
    });
    return res.data;
  },

  getDailyPlan: async (): Promise<DailyPlan> => {
    const res = await apiClient.get<DailyPlan>('/student/career/daily-plan');
    return res.data;
  },

  toggleDailyTask: async (taskId: number): Promise<DailyPlan> => {
    const res = await apiClient.post<DailyPlan>(`/student/career/daily-plan/toggle/${taskId}`);
    return res.data;
  },

  getWeakAreas: async (): Promise<WeakArea[]> => {
    const res = await apiClient.get<WeakArea[]>('/student/career/weak-areas');
    return res.data;
  },

  getCompanyPrepHub: async (companyId: number): Promise<CompanyPrepHub> => {
    const res = await apiClient.get<CompanyPrepHub>(`/student/career/company-prep/${companyId}`);
    return res.data;
  },

  listPlacementPapers: async (): Promise<PlacementPaperSummary[]> => {
    const res = await apiClient.get<PlacementPaperSummary[]>('/placement-papers');
    return res.data;
  },

  getPlacementPaperDetail: async (paperId: number): Promise<PlacementPaperDetail> => {
    const res = await apiClient.get<PlacementPaperDetail>(`/placement-papers/${paperId}`);
    return res.data;
  },

  startPlacementPaperAttempt: async (paperId: number): Promise<PlacementPaperAttempt> => {
    const res = await apiClient.post<PlacementPaperAttempt>(`/placement-papers/${paperId}/attempt`);
    return res.data;
  },

  submitPlacementPaperAnswer: async (
    paperId: number,
    data: { attemptId: number; questionId: number; selectedOption: string; timeTakenSeconds?: number }
  ): Promise<void> => {
    await apiClient.post(`/placement-papers/${paperId}/answer`, data);
  },

  completePlacementPaperAttempt: async (paperId: number, attemptId: number): Promise<PlacementPaperResult> => {
    const res = await apiClient.post<PlacementPaperResult>(
      `/placement-papers/${paperId}/complete?attemptId=${attemptId}`
    );
    return res.data;
  },

  getPlacementPaperResult: async (paperId: number, attemptId: number): Promise<PlacementPaperResult> => {
    const res = await apiClient.get<PlacementPaperResult>(`/placement-papers/${paperId}/results/${attemptId}`);
    return res.data;
  },

  getTrainerCareerStats: async (): Promise<any> => {
    const res = await apiClient.get('/trainer/career/stats');
    return res.data;
  },

  getAdminCareerAnalytics: async (): Promise<any> => {
    const res = await apiClient.get('/admin/career/analytics');
    return res.data;
  },
};
