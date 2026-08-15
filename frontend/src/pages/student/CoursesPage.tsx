import React from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { coursesApi } from '../../api/courses';
import { BookOpen, Clock, Layers, ChevronRight, AlertCircle, Loader2, GraduationCap } from 'lucide-react';

const DIFFICULTY_COLOR: Record<string, string> = {
  BEGINNER: 'bg-emerald-100 text-emerald-700',
  INTERMEDIATE: 'bg-amber-100 text-amber-700',
  ADVANCED: 'bg-red-100 text-red-700',
};

function DifficultyBadge({ difficulty }: { difficulty: string }) {
  const cls = DIFFICULTY_COLOR[difficulty] ?? 'bg-slate-100 text-slate-600';
  return (
    <span className={`badge ${cls} capitalize`}>
      {difficulty.charAt(0) + difficulty.slice(1).toLowerCase()}
    </span>
  );
}

export function CoursesPage() {
  const { data: courses, isLoading, isError, error } = useQuery({
    queryKey: ['courses'],
    queryFn: () => coursesApi.getAll(),
  });

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 gap-4">
        <Loader2 className="h-8 w-8 animate-spin text-brand-500" />
        <p className="text-sm text-slate-500">Loading courses…</p>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex flex-col items-center justify-center py-20 gap-4 text-center">
        <div className="flex h-14 w-14 items-center justify-center rounded-full bg-red-50">
          <AlertCircle className="h-7 w-7 text-red-500" />
        </div>
        <div>
          <p className="font-semibold text-slate-800">Failed to load courses</p>
          <p className="text-sm text-slate-500 mt-1">
            {(error as any)?.response?.data?.message ?? 'Please try again later.'}
          </p>
        </div>
      </div>
    );
  }

  if (!courses || courses.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-20 gap-4 text-center">
        <div className="flex h-14 w-14 items-center justify-center rounded-full bg-brand-50">
          <GraduationCap className="h-7 w-7 text-brand-400" />
        </div>
        <div>
          <p className="font-semibold text-slate-800">No courses available</p>
          <p className="text-sm text-slate-500 mt-1">Check back soon — content is being added.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div>
        <h1 className="text-xl font-bold text-slate-900">Browse Courses</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          {courses.length} course{courses.length !== 1 ? 's' : ''} available
        </p>
      </div>

      {/* Course Grid */}
      <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
        {courses.map((course) => (
          <Link
            key={course.id}
            to={`/student/courses/${course.id}`}
            className="group card flex flex-col gap-4 hover:shadow-lg transition-all duration-200 hover:-translate-y-0.5 no-underline"
          >
            {/* Category tag + difficulty */}
            <div className="flex items-center justify-between">
              <span className="badge bg-brand-50 text-brand-600 text-xs">
                {course.categoryName}
              </span>
              <DifficultyBadge difficulty={course.difficulty} />
            </div>

            {/* Title + description */}
            <div className="flex-1">
              <h2 className="font-bold text-slate-900 group-hover:text-brand-700 transition-colors leading-snug">
                {course.title}
              </h2>
              {course.description && (
                <p className="mt-1.5 text-sm text-slate-500 line-clamp-2">
                  {course.description}
                </p>
              )}
            </div>

            {/* Meta row */}
            <div className="flex items-center justify-between border-t border-slate-100 pt-3">
              <div className="flex items-center gap-3 text-xs text-slate-500">
                <span className="flex items-center gap-1">
                  <Layers className="h-3.5 w-3.5" />
                  {course.moduleCount} module{course.moduleCount !== 1 ? 's' : ''}
                </span>
                <span className="flex items-center gap-1">
                  <BookOpen className="h-3.5 w-3.5" />
                  {course.topicCount} topic{course.topicCount !== 1 ? 's' : ''}
                </span>
                {course.estimatedHours && (
                  <span className="flex items-center gap-1">
                    <Clock className="h-3.5 w-3.5" />
                    {course.estimatedHours}h
                  </span>
                )}
              </div>
              <ChevronRight className="h-4 w-4 text-slate-300 group-hover:text-brand-400 transition-colors" />
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
