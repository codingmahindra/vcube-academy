import api from './client';

export type EmploymentType = 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'INTERNSHIP';
export type ExperienceLevel = 'FRESHER' | 'ENTRY_LEVEL' | 'MID_LEVEL' | 'SENIOR';
export type WorkMode = 'ONSITE' | 'HYBRID' | 'REMOTE';
export type JobSource = 'COMPANY_CAREER_PAGE' | 'LINKEDIN' | 'NAUKRI' | 'FOUNDIT' | 'WELLFOUND' | 'INDEED' | 'OTHER';
export type ApplicationStatus = 'SAVED' | 'APPLIED' | 'ASSESSMENT' | 'INTERVIEW' | 'OFFER' | 'REJECTED' | 'WITHDRAWN';
export type PlacementDriveStatus = 'UPCOMING' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';

export interface JobCategoryDto {
  id: number;
  name: string;
  slug: string;
  description: string;
  icon: string;
  totalJobs: number;
}

export interface JobSkillDto {
  id: number;
  name: string;
  slug: string;
  category: string;
  isRequired: boolean;
}

export interface JobSummaryDto {
  id: number;
  companyId: number;
  companyName: string;
  companyLogoUrl?: string;
  companyTier?: string;
  categoryId?: number;
  categoryName?: string;
  title: string;
  slug: string;
  location: string;
  employmentType: EmploymentType;
  experienceLevel: ExperienceLevel;
  workMode: WorkMode;
  salaryMin?: number;
  salaryMax?: number;
  salaryText?: string;
  source: JobSource;
  sourceUrl?: string;
  postedDate: string;
  applicationDeadline?: string;
  isSaved?: boolean;
  hasApplied?: boolean;
  applicationStatus?: string;
  skills: JobSkillDto[];
}

export interface JobMatchResultDto {
  matchPercentage: number;
  matchedSkills: string[];
  missingSkills: string[];
  rolePreferenceMatched: boolean;
  locationPreferenceMatched: boolean;
  workModeMatched: boolean;
  experienceMatched: boolean;
  summary: string;
}

export interface JobPreparationRecommendationDto {
  recommendedCourses: Array<{ id: number; title: string; slug: string; difficulty: string }>;
  recommendedDsaProblems: Array<{ id: number; title: string; difficulty: string; category: string }>;
  recommendedInterviewQuestions: Array<{ id: number; questionText: string; difficulty: string; round: string }>;
  technicalChecklist: string[];
  recommendedMockInterviewRole: string;
}

export interface JobDetailDto extends JobSummaryDto {
  companyDescription?: string;
  description: string;
  qualification?: string;
  responsibilities?: string;
  selectionProcess?: string;
  salaryCurrency?: string;
  matchResult?: JobMatchResultDto;
  preparationRoadmap?: JobPreparationRecommendationDto;
}

export interface SavedJobDto {
  id: number;
  jobId: number;
  job: JobSummaryDto;
  savedAt: string;
}

export interface ApplicationStatusHistoryDto {
  id: number;
  previousStatus?: ApplicationStatus;
  newStatus: ApplicationStatus;
  changedAt: string;
  notes?: string;
}

export interface JobApplicationDto {
  id: number;
  jobId: number;
  job: JobSummaryDto;
  status: ApplicationStatus;
  appliedDate: string;
  notes?: string;
  nextAction?: string;
  interviewDate?: string;
  statusHistories: ApplicationStatusHistoryDto[];
  createdAt: string;
  updatedAt: string;
}

export interface ApplicationDashboardDto {
  totalApplications: number;
  appliedCount: number;
  assessmentCount: number;
  interviewCount: number;
  offerCount: number;
  rejectedCount: number;
  upcomingInterviews: JobApplicationDto[];
  recentApplications: JobApplicationDto[];
}

export interface PlacementDriveDto {
  id: number;
  companyId: number;
  companyName: string;
  companyLogoUrl?: string;
  companyTier?: string;
  title: string;
  description: string;
  location: string;
  driveDate: string;
  registrationDeadline: string;
  packageDetails?: string;
  eligibilityCriteria?: string;
  selectionProcess?: string;
  applicationLink?: string;
  status: PlacementDriveStatus;
}

export interface StudentJobPreferenceDto {
  preferredRoles: string[];
  preferredLocations: string[];
  preferredTechnologies: string[];
  experienceLevel?: ExperienceLevel;
  workMode?: WorkMode;
  employmentType?: EmploymentType;
  expectedSalaryMin?: number;
}

export interface JobFilterParams {
  keyword?: string;
  companyId?: number;
  categoryId?: number;
  location?: string;
  employmentType?: EmploymentType;
  experienceLevel?: ExperienceLevel;
  workMode?: WorkMode;
  skillId?: number;
  minSalary?: number;
  sortBy?: string;
  page?: number;
  size?: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export const jobsApi = {
  searchJobs: (params: JobFilterParams) =>
    api.get<PageResponse<JobSummaryDto>>('/jobs', { params }).then((res) => res.data),

  getJobDetail: (id: number) =>
    api.get<JobDetailDto>(`/jobs/${id}`).then((res) => res.data),

  getCategories: () =>
    api.get<JobCategoryDto[]>('/jobs/categories').then((res) => res.data),

  getLocations: () =>
    api.get<string[]>('/jobs/locations').then((res) => res.data),

  getSkills: () =>
    api.get<JobSkillDto[]>('/jobs/skills').then((res) => res.data),

  getCompanies: () =>
    api.get<any[]>('/jobs/companies').then((res) => res.data),

  saveJob: (jobId: number) =>
    api.post<SavedJobDto>(`/student/jobs/${jobId}/save`).then((res) => res.data),

  unsaveJob: (jobId: number) =>
    api.delete(`/student/jobs/${jobId}/save`).then((res) => res.data),

  getSavedJobs: (page = 0, size = 10) =>
    api.get<PageResponse<SavedJobDto>>('/student/saved-jobs', { params: { page, size } }).then((res) => res.data),

  createApplication: (data: { jobId: number; status?: ApplicationStatus; notes?: string; nextAction?: string; interviewDate?: string }) =>
    api.post<JobApplicationDto>('/student/applications', data).then((res) => res.data),

  getApplications: (page = 0, size = 15) =>
    api.get<PageResponse<JobApplicationDto>>('/student/applications', { params: { page, size } }).then((res) => res.data),

  getApplicationDetail: (id: number) =>
    api.get<JobApplicationDto>(`/student/applications/${id}`).then((res) => res.data),

  updateApplication: (id: number, data: { status?: ApplicationStatus; notes?: string; nextAction?: string; interviewDate?: string }) =>
    api.put<JobApplicationDto>(`/student/applications/${id}`, data).then((res) => res.data),

  deleteApplication: (id: number) =>
    api.delete(`/student/applications/${id}`).then((res) => res.data),

  getApplicationDashboard: () =>
    api.get<ApplicationDashboardDto>('/student/applications/dashboard').then((res) => res.data),

  getPlacementDrives: () =>
    api.get<PlacementDriveDto[]>('/placements').then((res) => res.data),

  getPlacementDriveDetail: (id: number) =>
    api.get<PlacementDriveDto>(`/placements/${id}`).then((res) => res.data),

  getJobPreferences: () =>
    api.get<StudentJobPreferenceDto>('/student/job-preferences').then((res) => res.data),

  saveJobPreferences: (data: StudentJobPreferenceDto) =>
    api.put<StudentJobPreferenceDto>('/student/job-preferences', data).then((res) => res.data),

  getJobRecommendations: () =>
    api.get<{ recommendedJobs: Array<{ job: JobSummaryDto; matchScore: number; matchedSkills: string[]; missingSkills: string[]; summary: string }>; recommendedSkillRevision: string[] }>('/student/job-recommendations').then((res) => res.data),

  // Trainer / Admin API
  createJob: (data: any) =>
    api.post<JobDetailDto>('/trainer/jobs', data).then((res) => res.data),

  updateJob: (id: number, data: any) =>
    api.put<JobDetailDto>(`/trainer/jobs/${id}`, data).then((res) => res.data),

  deleteJob: (id: number) =>
    api.delete(`/admin/jobs/${id}`).then((res) => res.data),

  createPlacementDrive: (data: any) =>
    api.post<PlacementDriveDto>('/admin/placements', data).then((res) => res.data),

  updatePlacementDrive: (id: number, data: any) =>
    api.put<PlacementDriveDto>(`/admin/placements/${id}`, data).then((res) => res.data),

  deletePlacementDrive: (id: number) =>
    api.delete(`/admin/placements/${id}`).then((res) => res.data),

  getJobAnalytics: () =>
    api.get<any>('/admin/jobs/analytics').then((res) => res.data),
};
