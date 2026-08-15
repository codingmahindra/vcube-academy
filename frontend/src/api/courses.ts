import apiClient from './client';
import type { CourseCategoryDto, CourseDto, CourseDetailDto } from '../types/course';

export const coursesApi = {
  /** List all published courses (optionally filtered by category slug) */
  getAll: (category?: string) =>
    apiClient
      .get<CourseDto[]>('/courses', { params: category ? { category } : undefined })
      .then((r) => r.data),

  /** Get full course detail (with modules and topics) by ID */
  getById: (id: number) =>
    apiClient.get<CourseDetailDto>(`/courses/${id}`).then((r) => r.data),

  /** Get course detail by slug */
  getBySlug: (slug: string) =>
    apiClient.get<CourseDetailDto>(`/courses/slug/${slug}`).then((r) => r.data),

  /** List all course categories */
  getCategories: () =>
    apiClient.get<CourseCategoryDto[]>('/courses/categories').then((r) => r.data),
};
