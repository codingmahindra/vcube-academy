import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  interviewApi,
  type InterviewProgressSummaryDto,
  type CompanySummaryDto,
  type InterviewQuestionSummaryDto,
} from '../../../api/interview';
import {
  HelpCircle, GraduationCap, Building2, BookOpen, CheckCircle2,
  TrendingUp, Award, ArrowRight, Play, Sparkles, ChevronRight,
  Flame, Loader2, Star, ShieldCheck
} from 'lucide-react';
import toast from 'react-hot-toast';

export function InterviewDashboardPage() {
  const navigate = useNavigate();
  const [progress, setProgress] = useState<InterviewProgressSummaryDto | null>(null);
  const [companies, setCompanies] = useState<CompanySummaryDto[]>([]);
  const [recommendations, setRecommendations] = useState<{
    recommendedQuestions: InterviewQuestionSummaryDto[];
    recommendedRevisionTopics: string[];
    targetCompanies: string[];
  } | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const [progData, compData, recData] = await Promise.all([
          interviewApi.getProgress(),
          interviewApi.getCompanies(),
          interviewApi.getRecommendations(),
        ]);
        setProgress(progData);
        setCompanies(compData);
        setRecommendations(recData);
      } catch (err) {
        toast.error('Failed to load interview dashboard data');
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="flex h-72 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header Banner */}
      <div className="rounded-2xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-6 sm:p-8 text-white shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 rounded-full bg-indigo-500/20 px-3 py-1 text-xs font-semibold text-indigo-300 border border-indigo-500/30">
              <GraduationCap className="h-3.5 w-3.5" /> Placement & Mock Interview Suite
            </div>
            <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-white">
              Java Full Stack Interview Prep
            </h1>
            <p className="text-sm text-slate-300 max-w-xl">
              Master technical, architectural, and behavioral rounds for Tier-1 IT leaders and product MNCs with automated answer evaluation.
            </p>
          </div>
          <div className="flex flex-wrap items-center gap-3">
            <Link
              to="/student/interview/mock"
              className="inline-flex items-center gap-2 rounded-xl bg-brand-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-brand-500 transition-all shadow-lg shadow-brand-600/30"
            >
              <Play className="h-4 w-4" /> Start Mock Interview
            </Link>
            <Link
              to="/student/interview/topics"
              className="inline-flex items-center gap-2 rounded-xl bg-white/10 px-4 py-2.5 text-sm font-semibold text-white hover:bg-white/20 transition-all border border-white/10"
            >
              Topic Q&A Bank
            </Link>
          </div>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="stat-card">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-slate-500">Interview Readiness</p>
            <div className="h-8 w-8 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <ShieldCheck className="h-4 w-4" />
            </div>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800">{progress?.readinessPercentage ?? 0}%</p>
          <div className="mt-2 w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
            <div
              className="bg-emerald-500 h-1.5 rounded-full transition-all duration-500"
              style={{ width: `${progress?.readinessPercentage ?? 0}%` }}
            />
          </div>
        </div>

        <div className="stat-card">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-slate-500">Questions Mastered</p>
            <div className="h-8 w-8 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
              <CheckCircle2 className="h-4 w-4" />
            </div>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800">
            {progress?.completedQuestions ?? 0} <span className="text-xs text-slate-400 font-normal">/ {progress?.totalQuestions ?? 0}</span>
          </p>
          <p className="mt-1 text-xs text-slate-400">Total Q&A practice sessions</p>
        </div>

        <div className="stat-card">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-slate-500">Mock Interviews</p>
            <div className="h-8 w-8 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
              <GraduationCap className="h-4 w-4" />
            </div>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800">{progress?.completedMockInterviews ?? 0}</p>
          <p className="mt-1 text-xs text-slate-400">Completed full sessions</p>
        </div>

        <div className="stat-card">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-slate-500">Avg Mock Score</p>
            <div className="h-8 w-8 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
              <Star className="h-4 w-4" />
            </div>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800">{progress?.averageMockScore ?? 0} / 100</p>
          <p className="mt-1 text-xs text-slate-400">Multi-round performance</p>
        </div>
      </div>

      {/* Target Companies Grid */}
      <div className="rounded-2xl bg-white p-6 border border-slate-100 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
              <Building2 className="h-4 w-4 text-brand-600" /> Company-Wise Placement Tracks
            </h2>
            <p className="text-xs text-slate-400 mt-0.5">Explore reported questions and hiring rounds for top tech employers</p>
          </div>
          <Link to="/student/interview/companies" className="text-xs font-semibold text-brand-600 hover:text-brand-700">
            View All &rarr;
          </Link>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          {companies.map((comp) => (
            <div
              key={comp.id}
              onClick={() => navigate(`/student/interview/companies/${comp.id}`)}
              className="p-4 rounded-xl border border-slate-100 hover:border-brand-200 hover:bg-brand-50/20 cursor-pointer transition-all flex items-center justify-between"
            >
              <div className="space-y-1">
                <p className="text-sm font-bold text-slate-800">{comp.name}</p>
                <p className="text-xs text-slate-400">{comp.industry} • {comp.tier}</p>
                <span className="badge bg-slate-100 text-slate-600 text-[10px]">
                  {comp.totalQuestions} Questions Available
                </span>
              </div>
              <ChevronRight className="h-4 w-4 text-slate-400 flex-shrink-0" />
            </div>
          ))}
        </div>
      </div>

      {/* Recommended Next Questions */}
      {recommendations && recommendations.recommendedQuestions.length > 0 && (
        <div className="rounded-2xl bg-white p-6 border border-slate-100 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
              <Sparkles className="h-4 w-4 text-brand-600" /> Recommended Questions to Practice
            </h2>
            <Link to="/student/interview/topics" className="text-xs font-semibold text-brand-600 hover:text-brand-700">
              Browse All Topics &rarr;
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {recommendations.recommendedQuestions.map((q) => (
              <div
                key={q.id}
                onClick={() => navigate(`/student/interview/questions?questionId=${q.id}`)}
                className="flex items-center justify-between p-4 rounded-xl border border-slate-100 hover:border-brand-200 hover:bg-brand-50/20 cursor-pointer transition-all"
              >
                <div className="space-y-1">
                  <p className="text-xs font-semibold text-slate-800 line-clamp-1">{q.questionText}</p>
                  <div className="flex items-center gap-2 text-[11px] text-slate-400">
                    <span>{q.categoryName}</span>
                    <span>•</span>
                    <span
                      className={`badge text-[9px] ${
                        q.difficulty === 'BASIC'
                          ? 'bg-emerald-100 text-emerald-700'
                          : q.difficulty === 'INTERMEDIATE'
                          ? 'bg-amber-100 text-amber-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {q.difficulty}
                    </span>
                  </div>
                </div>
                <ChevronRight className="h-4 w-4 text-slate-400 flex-shrink-0" />
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Topic Mastery Breakdown */}
      <div className="rounded-2xl bg-white p-6 border border-slate-100 shadow-sm space-y-4">
        <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
          <BookOpen className="h-4 w-4 text-brand-600" /> Core Tech Stacks
        </h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          {progress?.categoryProgress.map((cat) => {
            const pct = cat.totalQuestions > 0 ? Math.round((cat.completedQuestions / cat.totalQuestions) * 100) : 0;
            return (
              <div
                key={cat.id}
                onClick={() => navigate(`/student/interview/topics?categoryId=${cat.id}`)}
                className="p-4 rounded-xl border border-slate-100 hover:border-brand-200 hover:bg-slate-50 cursor-pointer transition-all space-y-2"
              >
                <div>
                  <p className="text-xs font-bold text-slate-800">{cat.name}</p>
                  <p className="text-[11px] text-slate-400 line-clamp-1 mt-0.5">{cat.description}</p>
                </div>
                <div className="pt-2">
                  <div className="flex justify-between text-[10px] text-slate-500 font-medium mb-1">
                    <span>{cat.completedQuestions} / {cat.totalQuestions} questions</span>
                    <span>{pct}%</span>
                  </div>
                  <div className="w-full bg-slate-100 rounded-full h-1.5">
                    <div className="bg-brand-500 h-1.5 rounded-full" style={{ width: `${pct}%` }} />
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
