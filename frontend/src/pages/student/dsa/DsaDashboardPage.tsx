import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { dsaApi, type DsaProgressSummary, type DsaProblemSummary } from '../../../api/dsa';
import {
  Code2, CheckCircle2, Target, Flame, ArrowRight, BookOpen,
  Award, TrendingUp, Layers, ChevronRight, Play, Loader2
} from 'lucide-react';
import toast from 'react-hot-toast';

export function DsaDashboardPage() {
  const navigate = useNavigate();
  const [progress, setProgress] = useState<DsaProgressSummary | null>(null);
  const [recommended, setRecommended] = useState<DsaProblemSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const [progData, probData] = await Promise.all([
          dsaApi.getProgress(),
          dsaApi.getProblems({ statusFilter: 'UNSOLVED', size: 4 }),
        ]);
        setProgress(progData);
        setRecommended(probData.content);
      } catch (err: any) {
        toast.error('Failed to load DSA dashboard data');
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

  const easyPct = progress?.easyTotal ? Math.round((progress.easySolved / progress.easyTotal) * 100) : 0;
  const medPct = progress?.mediumTotal ? Math.round((progress.mediumSolved / progress.mediumTotal) * 100) : 0;
  const hardPct = progress?.hardTotal ? Math.round((progress.hardSolved / progress.hardTotal) * 100) : 0;

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header Banner */}
      <div className="rounded-2xl bg-gradient-to-r from-slate-900 via-brand-950 to-slate-900 p-6 sm:p-8 text-white shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 rounded-full bg-brand-500/20 px-3 py-1 text-xs font-semibold text-brand-300 border border-brand-500/30">
              <Code2 className="h-3.5 w-3.5" /> Java DSA Practice Engine
            </div>
            <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-white">
              Data Structures & Algorithms
            </h1>
            <p className="text-sm text-slate-300 max-w-xl">
              Master Java placement coding challenges, standard patterns, time/space complexity analysis, and real interview test cases.
            </p>
          </div>
          <div className="flex flex-wrap items-center gap-3">
            <Link
              to="/student/dsa/problems"
              className="inline-flex items-center gap-2 rounded-xl bg-brand-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-brand-500 transition-all shadow-lg shadow-brand-600/30"
            >
              Browse Problem Bank <ArrowRight className="h-4 w-4" />
            </Link>
            <Link
              to="/student/dsa/submissions"
              className="inline-flex items-center gap-2 rounded-xl bg-white/10 px-4 py-2.5 text-sm font-semibold text-white hover:bg-white/20 transition-all border border-white/10"
            >
              My Submissions
            </Link>
          </div>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="stat-card">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-slate-500">Solved Problems</p>
            <div className="h-8 w-8 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <CheckCircle2 className="h-4 w-4" />
            </div>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800">
            {progress?.solvedProblems ?? 0} <span className="text-xs text-slate-400 font-normal">/ {progress?.totalProblems ?? 0}</span>
          </p>
          <div className="mt-2 w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
            <div
              className="bg-emerald-500 h-1.5 rounded-full transition-all duration-500"
              style={{ width: `${progress?.totalProblems ? Math.min(100, (progress.solvedProblems / progress.totalProblems) * 100) : 0}%` }}
            />
          </div>
        </div>

        <div className="stat-card">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-slate-500">Attempted</p>
            <div className="h-8 w-8 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
              <Target className="h-4 w-4" />
            </div>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800">{progress?.attemptedProblems ?? 0}</p>
          <p className="mt-1 text-xs text-slate-400">Total problems tackled</p>
        </div>

        <div className="stat-card">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-slate-500">Success Rate</p>
            <div className="h-8 w-8 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
              <TrendingUp className="h-4 w-4" />
            </div>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800">{progress?.successRate ?? 0}%</p>
          <p className="mt-1 text-xs text-slate-400">Solved / Attempted ratio</p>
        </div>

        <div className="stat-card">
          <div className="flex items-center justify-between">
            <p className="text-xs font-medium text-slate-500">Submissions</p>
            <div className="h-8 w-8 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
              <Flame className="h-4 w-4" />
            </div>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800">{progress?.totalSubmissions ?? 0}</p>
          <p className="mt-1 text-xs text-slate-400">Total code evaluations</p>
        </div>
      </div>

      {/* Difficulty Breakdown */}
      <div className="rounded-2xl bg-white p-6 border border-slate-100 shadow-sm space-y-4">
        <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
          <Award className="h-4 w-4 text-brand-600" /> Difficulty Mastery
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="p-4 rounded-xl border border-emerald-100 bg-emerald-50/40">
            <div className="flex justify-between items-center mb-2">
              <span className="badge bg-emerald-100 text-emerald-800">Easy</span>
              <span className="text-xs font-semibold text-slate-600">
                {progress?.easySolved ?? 0} / {progress?.easyTotal ?? 0} ({easyPct}%)
              </span>
            </div>
            <div className="w-full bg-emerald-200/50 rounded-full h-2">
              <div className="bg-emerald-500 h-2 rounded-full" style={{ width: `${easyPct}%` }} />
            </div>
          </div>

          <div className="p-4 rounded-xl border border-amber-100 bg-amber-50/40">
            <div className="flex justify-between items-center mb-2">
              <span className="badge bg-amber-100 text-amber-800">Medium</span>
              <span className="text-xs font-semibold text-slate-600">
                {progress?.mediumSolved ?? 0} / {progress?.mediumTotal ?? 0} ({medPct}%)
              </span>
            </div>
            <div className="w-full bg-amber-200/50 rounded-full h-2">
              <div className="bg-amber-500 h-2 rounded-full" style={{ width: `${medPct}%` }} />
            </div>
          </div>

          <div className="p-4 rounded-xl border border-red-100 bg-red-50/40">
            <div className="flex justify-between items-center mb-2">
              <span className="badge bg-red-100 text-red-800">Hard</span>
              <span className="text-xs font-semibold text-slate-600">
                {progress?.hardSolved ?? 0} / {progress?.hardTotal ?? 0} ({hardPct}%)
              </span>
            </div>
            <div className="w-full bg-red-200/50 rounded-full h-2">
              <div className="bg-red-500 h-2 rounded-full" style={{ width: `${hardPct}%` }} />
            </div>
          </div>
        </div>
      </div>

      {/* Recommended Next Problems */}
      <div className="rounded-2xl bg-white p-6 border border-slate-100 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
            <Play className="h-4 w-4 text-brand-600" /> Recommended Problems
          </h2>
          <Link to="/student/dsa/problems" className="text-xs font-semibold text-brand-600 hover:text-brand-700">
            View All &rarr;
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          {recommended.map((p) => (
            <div
              key={p.id}
              onClick={() => navigate(`/student/dsa/problems/${p.id}`)}
              className="flex items-center justify-between p-4 rounded-xl border border-slate-100 hover:border-brand-200 hover:bg-brand-50/20 cursor-pointer transition-all"
            >
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <span className="text-sm font-semibold text-slate-800">{p.title}</span>
                  <span
                    className={`badge text-[10px] ${
                      p.difficulty === 'EASY'
                        ? 'bg-emerald-100 text-emerald-700'
                        : p.difficulty === 'MEDIUM'
                        ? 'bg-amber-100 text-amber-700'
                        : 'bg-red-100 text-red-700'
                    }`}
                  >
                    {p.difficulty}
                  </span>
                </div>
                <p className="text-xs text-slate-400">{p.categoryName} • {p.subtopic ?? 'Algorithm'}</p>
              </div>
              <ChevronRight className="h-4 w-4 text-slate-400 flex-shrink-0" />
            </div>
          ))}
        </div>
      </div>

      {/* Topic / Category Progress Grid */}
      <div className="rounded-2xl bg-white p-6 border border-slate-100 shadow-sm space-y-4">
        <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
          <Layers className="h-4 w-4 text-brand-600" /> Category Breakdown
        </h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          {progress?.categoryProgress.map((cat) => {
            const catPct = cat.totalProblems > 0 ? Math.round((cat.solvedProblems / cat.totalProblems) * 100) : 0;
            return (
              <div
                key={cat.id}
                onClick={() => navigate(`/student/dsa/problems?categoryId=${cat.id}`)}
                className="p-3.5 rounded-xl border border-slate-100 hover:border-brand-200 hover:bg-slate-50 cursor-pointer transition-all flex flex-col justify-between"
              >
                <div>
                  <p className="text-xs font-bold text-slate-800 truncate">{cat.name}</p>
                  <p className="text-[11px] text-slate-400 line-clamp-1 mt-0.5">{cat.description}</p>
                </div>
                <div className="mt-3">
                  <div className="flex justify-between text-[10px] text-slate-500 font-medium mb-1">
                    <span>{cat.solvedProblems} / {cat.totalProblems} solved</span>
                    <span>{catPct}%</span>
                  </div>
                  <div className="w-full bg-slate-100 rounded-full h-1.5">
                    <div className="bg-brand-500 h-1.5 rounded-full" style={{ width: `${catPct}%` }} />
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
