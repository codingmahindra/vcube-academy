import React from 'react';
import { Link, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { coursesApi } from '../../api/courses';
import {
  BookOpen, Clock, Layers, ChevronRight, AlertCircle, Loader2, ArrowLeft,
  CheckCircle2, Lock, PlayCircle,
} from 'lucide-react';

const DIFFICULTY_COLOR: Record<string, string> = {
  BEGINNER: 'bg-emerald-100 text-emerald-700',
  INTERMEDIATE: 'bg-amber-100 text-amber-700',
  ADVANCED: 'bg-red-100 text-red-700',
  EASY:   'bg-emerald-100 text-emerald-700',
  MEDIUM: 'bg-amber-100 text-amber-700',
  HARD:   'bg-red-100 text-red-700',
};

function DifficultyBadge({ difficulty }: { difficulty: string }) {
  const cls = DIFFICULTY_COLOR[difficulty] ?? 'bg-slate-100 text-slate-600';
  return (
    <span className={`badge ${cls} capitalize`}>
      {difficulty.charAt(0) + difficulty.slice(1).toLowerCase()}
    </span>
  );
}

export function CourseDetailPage() {
  const { id } = useParams<{ id: string }>();
  const courseId = Number(id);

  const { data: course, isLoading, isError, error } = useQuery({
    queryKey: ['course', courseId],
    queryFn: () => coursesApi.getById(courseId),
    enabled: !!courseId,
  });

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 gap-4">
        <Loader2 className="h-8 w-8 animate-spin text-brand-500" />
        <p className="text-sm text-slate-500">Loading course…</p>
      </div>
    );
  }

  if (isError || !course) {
    return (
      <div className="flex flex-col items-center justify-center py-20 gap-4 text-center">
        <div className="flex h-14 w-14 items-center justify-center rounded-full bg-red-50">
          <AlertCircle className="h-7 w-7 text-red-500" />
        </div>
        <div>
          <p className="font-semibold text-slate-800">Failed to load course</p>
          <p className="text-sm text-slate-500 mt-1">
            {(error as any)?.response?.data?.message ?? 'Course not found.'}
          </p>
        </div>
        <Link to="/student/courses" className="btn-secondary text-sm">
          ← Back to Courses
        </Link>
      </div>
    );
  }

  const totalTopics = course.modules.reduce(
    (acc, m) => acc + (m.topics?.length ?? 0),
    0
  );

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Back link */}
      <Link
        to="/student/courses"
        className="inline-flex items-center gap-1.5 text-sm text-slate-500 hover:text-brand-600 transition-colors"
      >
        <ArrowLeft className="h-4 w-4" />
        All Courses
      </Link>

      {/* Course Header Card */}
      <div className="rounded-2xl bg-gradient-to-r from-brand-700 to-brand-900 p-6 text-white shadow-md">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div className="space-y-2">
            <span className="badge bg-white/20 text-white text-xs">
              {course.categoryName}
            </span>
            <h1 className="text-xl font-bold leading-snug">{course.title}</h1>
            {course.description && (
              <p className="text-sm text-brand-200 max-w-xl">{course.description}</p>
            )}
          </div>
          <DifficultyBadge difficulty={course.difficulty} />
        </div>

        {/* Stats row */}
        <div className="mt-4 flex flex-wrap gap-4 text-sm text-brand-100">
          <span className="flex items-center gap-1.5">
            <Layers className="h-4 w-4" /> {course.modules.length} modules
          </span>
          <span className="flex items-center gap-1.5">
            <BookOpen className="h-4 w-4" /> {totalTopics} topics
          </span>
          {course.estimatedHours && (
            <span className="flex items-center gap-1.5">
              <Clock className="h-4 w-4" /> {course.estimatedHours} hours
            </span>
          )}
        </div>
      </div>

      {/* Modules & Topics */}
      <div className="space-y-4">
        <h2 className="text-base font-semibold text-slate-800">Course Content</h2>

        {course.modules.length === 0 ? (
          <div className="card text-center py-10 text-sm text-slate-400">
            No modules have been added yet.
          </div>
        ) : (
          course.modules.map((module, mIdx) => (
            <div key={module.id} className="card p-0 overflow-hidden">
              {/* Module header */}
              <div className="flex items-center gap-3 bg-slate-50 px-5 py-3.5 border-b border-slate-100">
                <div className="flex h-7 w-7 items-center justify-center rounded-full bg-brand-100 text-brand-700 text-xs font-bold flex-shrink-0">
                  {mIdx + 1}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-slate-800 text-sm">{module.title}</p>
                  {module.description && (
                    <p className="text-xs text-slate-500 mt-0.5 truncate">{module.description}</p>
                  )}
                </div>
                <span className="text-xs text-slate-400 flex-shrink-0">
                  {module.topics?.length ?? 0} topics
                </span>
              </div>

              {/* Topics */}
              <div className="divide-y divide-slate-50">
                {!module.topics || module.topics.length === 0 ? (
                  <p className="px-5 py-3 text-xs text-slate-400">No topics yet.</p>
                ) : (
                  module.topics.map((topic, tIdx) => (
                    <Link
                      key={topic.id}
                      to={`/student/topics/${topic.id}`}
                      className="group flex items-center gap-4 px-5 py-3 hover:bg-brand-50 transition-colors no-underline"
                    >
                      <div className="flex h-6 w-6 items-center justify-center rounded-full border border-slate-200 text-[10px] text-slate-400 group-hover:border-brand-300 flex-shrink-0">
                        {tIdx + 1}
                      </div>
                      <PlayCircle className="h-4 w-4 text-slate-300 group-hover:text-brand-400 flex-shrink-0" />
                      <span className="flex-1 text-sm text-slate-700 group-hover:text-brand-700 font-medium truncate">
                        {topic.title}
                      </span>
                      <div className="flex items-center gap-2 flex-shrink-0">
                        {topic.difficulty && (
                          <span className={`badge text-[10px] px-1.5 ${DIFFICULTY_COLOR[topic.difficulty] ?? 'bg-slate-100 text-slate-500'}`}>
                            {topic.difficulty.charAt(0) + topic.difficulty.slice(1).toLowerCase()}
                          </span>
                        )}
                        {topic.estimatedMinutes && (
                          <span className="text-xs text-slate-400 hidden sm:block">
                            {topic.estimatedMinutes}m
                          </span>
                        )}
                        <ChevronRight className="h-4 w-4 text-slate-300 group-hover:text-brand-400" />
                      </div>
                    </Link>
                  ))
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
