// ─── Course Category ──────────────────────────────────────────────────────────

export interface CourseCategoryDto {
  id: number;
  name: string;
  slug: string;
  description?: string;
  icon?: string;
  displayOrder: number;
  isActive: boolean;
  courseCount: number;
  createdAt: string;
}

// ─── Course ───────────────────────────────────────────────────────────────────

export interface CourseDto {
  id: number;
  categoryId: number;
  categoryName: string;
  categorySlug: string;
  title: string;
  slug: string;
  description?: string;
  difficulty: string;
  estimatedHours?: number;
  isPublished: boolean;
  displayOrder: number;
  moduleCount: number;
  topicCount: number;
  createdAt: string;
  updatedAt: string;
}

// ─── Course Module ────────────────────────────────────────────────────────────

export interface TopicSummaryDto {
  id: number;
  moduleId: number;
  title: string;
  slug: string;
  difficulty: string;
  estimatedMinutes?: number;
  displayOrder: number;
  isPublished: boolean;
}

export interface CourseModuleDto {
  id: number;
  courseId: number;
  title: string;
  description?: string;
  displayOrder: number;
  topics?: TopicSummaryDto[];
}

export interface CourseDetailDto extends CourseDto {
  modules: CourseModuleDto[];
}

export interface CourseRequest {
  categoryId?: number;
  title: string;
  slug: string;
  description?: string;
  difficulty?: string;
  estimatedHours?: number;
  isPublished?: boolean;
  displayOrder?: number;
}

export interface CourseModuleRequest {
  title: string;
  description?: string;
  displayOrder?: number;
}
