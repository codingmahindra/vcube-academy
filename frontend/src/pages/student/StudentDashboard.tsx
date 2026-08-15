import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { careerApi, type CareerDashboard, type WeakArea } from '../../api/career';
import { gamificationApi, type GamificationSummary } from '../../api/gamification';
import {
  BookOpen, Code2, Brain, Award,
  Sparkles, AlertCircle, ArrowRight, ShieldCheck, CheckCircle2, TrendingUp,
  FileText, Flame, Bookmark, Bell, Search, Briefcase, Compass, Calendar
} from 'lucide-react';

export function StudentDashboard() {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState<CareerDashboard | null>(null);
  const [gamification, setGamification] = useState<GamificationSummary | null>(null);
  const [weakAreas, setWeakAreas] = useState<WeakArea[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const [dash, gam, weak] = await Promise.all([
          careerApi.getDashboard(),
          gamificationApi.getSummary().catch(() => null),
          careerApi.getWeakAreas().catch(() => []),
        ]);
        setDashboard(dash);
        setGamification(gam);
        setWeakAreas(weak);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load master dashboard metrics');
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-28">
        <div className="h-9 w-9 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !dashboard) {
    return (
      <div className="rounded-2xl border border-red-200 bg-red-50 p-8 text-center text-red-700 max-w-xl mx-auto mt-12">
        <AlertCircle className="mx-auto h-8 w-8 mb-2" />
        <p className="font-bold">{error || 'Master dashboard metrics unavailable.'}</p>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-16">
      {/* ─── Hero Branding & Welcome Banner ────────────────────────────────── */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-8 md:p-10 text-white shadow-2xl">
        <div className="relative z-10 flex flex-col lg:flex-row lg:items-center lg:justify-between gap-8">
          <div className="space-y-3">
            <div className="flex flex-wrap items-center gap-2.5">
              <span className="inline-flex items-center gap-1.5 rounded-full bg-indigo-500/20 px-3.5 py-1 text-xs font-bold text-indigo-300 border border-indigo-400/30">
                <Sparkles className="h-3.5 w-3.5" /> VCUBE SOFTWARE SOLUTIONS
              </span>
              <span className="inline-flex items-center gap-1 rounded-full bg-amber-500/20 px-3 py-1 text-xs font-semibold text-amber-300 border border-amber-400/30">
                Mentors: Srikanth & Viswanath
              </span>
            </div>
            <h1 className="text-3xl md:text-4xl font-extrabold tracking-tight">
              Welcome back, {user?.fullName || dashboard.studentName}!
            </h1>
            <p className="text-sm text-slate-300 max-w-2xl leading-relaxed">
              Your comprehensive placement cockpit tracks Course Progress, MCQ benchmarks, DSA mastery, ATS resume optimization, and Placement drive readiness in real time.
            </p>
          </div>

          {/* Gamification Streak & Points Showcase */}
          {gamification && (
            <div className="flex flex-col sm:flex-row gap-3 lg:items-center">
              <div className="flex items-center gap-3 rounded-2xl bg-white/10 backdrop-blur-md p-4 border border-white/10 shadow-sm">
                <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-amber-500 text-white shadow-md">
                  <Flame className="h-6 w-6 animate-pulse" />
                </div>
                <div>
                  <p className="text-xs font-bold uppercase tracking-wider text-slate-300">Study Streak</p>
                  <p className="text-2xl font-black text-white">{gamification.currentStreakDays} Days</p>
                  <p className="text-[10px] text-amber-300 font-medium">Record: {gamification.longestStreakDays} days</p>
                </div>
              </div>

              <div className="flex items-center gap-3 rounded-2xl bg-white/10 backdrop-blur-md p-4 border border-white/10 shadow-sm">
                <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-600 text-white shadow-md">
                  <Award className="h-6 w-6" />
                </div>
                <div>
                  <p className="text-xs font-bold uppercase tracking-wider text-slate-300">Total XP</p>
                  <p className="text-2xl font-black text-white">{gamification.totalXpPoints} pts</p>
                  <p className="text-[10px] text-indigo-300 font-medium">{gamification.unlockedBadgesCount} / {gamification.totalBadgesCount} Badges</p>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* ─── Core Career Metrics Grid ───────────────────────────────────────── */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {/* Course Progress */}
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-xs hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Learning Progress</span>
            <BookOpen className="h-5 w-5 text-indigo-600" />
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-extrabold text-slate-900">{dashboard.completedTopicsCount}</span>
            <span className="text-xs text-slate-500">Topics Completed</span>
          </div>
          <div className="mt-2 w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
            <div className="bg-indigo-600 h-full rounded-full" style={{ width: `${Math.min(100, dashboard.completedTopicsCount * 10)}%` }}></div>
          </div>
          <p className="mt-2 text-[11px] text-slate-500">{dashboard.enrolledCoursesCount} Enrolled Course(s)</p>
        </div>

        {/* MCQ Accuracy */}
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-xs hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">MCQ Accuracy</span>
            <Brain className="h-5 w-5 text-emerald-600" />
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-extrabold text-slate-900">{dashboard.mcqAccuracyPercentage}%</span>
            <span className="text-xs text-slate-500">Accuracy Rate</span>
          </div>
          <div className="mt-2 w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
            <div className="bg-emerald-600 h-full rounded-full" style={{ width: `${dashboard.mcqAccuracyPercentage}%` }}></div>
          </div>
          <p className="mt-2 text-[11px] text-slate-500">{dashboard.mcqQuizzesAttempted} Quizzes Attempted</p>
        </div>

        {/* DSA Solved */}
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-xs hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">DSA Mastery</span>
            <Code2 className="h-5 w-5 text-purple-600" />
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-extrabold text-slate-900">{dashboard.dsaProblemsSolved}</span>
            <span className="text-xs text-slate-500">Problems Solved</span>
          </div>
          <div className="mt-2 w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
            <div className="bg-purple-600 h-full rounded-full" style={{ width: `${Math.min(100, dashboard.dsaProblemsSolved * 5)}%` }}></div>
          </div>
          <p className="mt-2 text-[11px] text-slate-500">Top Problem Patterns</p>
        </div>

        {/* ATS Resume Score */}
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-xs hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">ATS Resume Score</span>
            <FileText className="h-5 w-5 text-amber-600" />
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-extrabold text-slate-900">{dashboard.primaryResumeAtsScore}/100</span>
            <span className="text-xs text-slate-500">Primary Version</span>
          </div>
          <div className="mt-2 w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
            <div className="bg-amber-600 h-full rounded-full" style={{ width: `${dashboard.primaryResumeAtsScore}%` }}></div>
          </div>
          <p className="mt-2 text-[11px] text-slate-500">{dashboard.jobApplicationsSubmitted} Job Applications Active</p>
        </div>
      </div>

      {/* ─── Main Content Split ────────────────────────────────────────────── */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column: Quick Navigation & Gamification Badges */}
        <div className="lg:col-span-2 space-y-8">
          {/* Quick Hub Portals */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs">
            <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
              <Compass className="h-5 w-5 text-indigo-600" /> Key Career Modules
            </h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Link
                to="/student/career/daily-plan"
                className="group rounded-xl border border-slate-200 p-4 hover:border-indigo-400 hover:bg-indigo-50/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center gap-2 text-indigo-600 font-bold text-sm mb-1">
                    <Calendar className="h-4 w-4" /> Daily Preparation Plan
                  </div>
                  <p className="text-xs text-slate-600 leading-relaxed">
                    Execute today's 7 targeted tasks: Course study, MCQ checkpoint, DSA problem & resume keyword refinement.
                  </p>
                </div>
                <div className="mt-3 inline-flex items-center gap-1 text-xs font-semibold text-indigo-600 group-hover:translate-x-1 transition-transform">
                  Launch Daily Plan <ArrowRight className="h-3.5 w-3.5" />
                </div>
              </Link>

              <Link
                to="/student/placement-papers"
                className="group rounded-xl border border-slate-200 p-4 hover:border-emerald-400 hover:bg-emerald-50/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center gap-2 text-emerald-600 font-bold text-sm mb-1">
                    <CheckCircle2 className="h-4 w-4" /> Company Placement Papers
                  </div>
                  <p className="text-xs text-slate-600 leading-relaxed">
                    Practice authentic multi-section exams (TCS, Infosys) with sectional time constraints and score analytics.
                  </p>
                </div>
                <div className="mt-3 inline-flex items-center gap-1 text-xs font-semibold text-emerald-600 group-hover:translate-x-1 transition-transform">
                  Attempt Exam Paper <ArrowRight className="h-3.5 w-3.5" />
                </div>
              </Link>

              <Link
                to="/student/interview/mock"
                className="group rounded-xl border border-slate-200 p-4 hover:border-purple-400 hover:bg-purple-50/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center gap-2 text-purple-600 font-bold text-sm mb-1">
                    <ShieldCheck className="h-4 w-4" /> Mock Interview Simulator
                  </div>
                  <p className="text-xs text-slate-600 leading-relaxed">
                    Practice technical & HR interview rounds with semantic evaluation and benchmark reports.
                  </p>
                </div>
                <div className="mt-3 inline-flex items-center gap-1 text-xs font-semibold text-purple-600 group-hover:translate-x-1 transition-transform">
                  Start Mock Interview <ArrowRight className="h-3.5 w-3.5" />
                </div>
              </Link>

              <Link
                to="/student/jobs"
                className="group rounded-xl border border-slate-200 p-4 hover:border-amber-400 hover:bg-amber-50/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center gap-2 text-amber-600 font-bold text-sm mb-1">
                    <Briefcase className="h-4 w-4" /> Placement Job Portal
                  </div>
                  <p className="text-xs text-slate-600 leading-relaxed">
                    Browse verified fresher & junior Java developer openings curated directly by VCUBE Software Solutions.
                  </p>
                </div>
                <div className="mt-3 inline-flex items-center gap-1 text-xs font-semibold text-amber-600 group-hover:translate-x-1 transition-transform">
                  Browse Active Openings <ArrowRight className="h-3.5 w-3.5" />
                </div>
              </Link>
            </div>
          </div>

          {/* Gamification Badges Section */}
          {gamification && (
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs space-y-4">
              <div className="flex items-center justify-between">
                <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
                  <Award className="h-5 w-5 text-indigo-600" /> Career Badges & Achievements
                </h2>
                <span className="text-xs font-semibold text-slate-500">
                  {gamification.unlockedBadgesCount} of {gamification.totalBadgesCount} Unlocked
                </span>
              </div>

              <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                {gamification.badges.map((badge) => (
                  <div
                    key={badge.badgeCode}
                    className={`rounded-xl border p-3.5 space-y-1 transition-all ${
                      badge.isUnlocked
                        ? 'border-indigo-200 bg-indigo-50/40 text-slate-800'
                        : 'border-slate-200 bg-slate-50 opacity-60 text-slate-400'
                    }`}
                  >
                    <div className="flex items-center gap-2">
                      <span className={`p-1.5 rounded-lg text-xs ${badge.isUnlocked ? 'bg-indigo-600 text-white' : 'bg-slate-200 text-slate-400'}`}>
                        <Award className="h-4 w-4" />
                      </span>
                      <span className="text-xs font-bold line-clamp-1">{badge.badgeName}</span>
                    </div>
                    <p className="text-[11px] leading-tight line-clamp-2">{badge.description}</p>
                  </div>
                ))}
              </div>

              <div className="pt-2 text-xs text-indigo-700 bg-indigo-50/60 border border-indigo-100 p-3 rounded-xl">
                <span className="font-bold">Next Target: </span>{gamification.nextMilestoneGoal}
              </div>
            </div>
          )}
        </div>

        {/* Right Column: Weak Area Engine & Career Trajectory */}
        <div className="space-y-8">
          {/* Career Stage Card */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs space-y-3">
            <h2 className="text-sm font-bold uppercase tracking-wider text-slate-500">Roadmap Milestone</h2>
            <p className="text-lg font-black text-slate-900">{dashboard.currentRoadmapStage.replace(/_/g, ' ')}</p>
            <p className="text-xs text-slate-600 leading-relaxed">
              Readiness status: <span className="font-bold text-indigo-600">{dashboard.interviewReadinessStatus}</span>
            </p>
            <Link
              to="/student/career/roadmap"
              className="inline-flex items-center gap-1.5 text-xs font-bold text-indigo-600 hover:text-indigo-700 pt-1"
            >
              View 9-Stage Milestone Roadmap <ArrowRight className="h-3.5 w-3.5" />
            </Link>
          </div>

          {/* Weak Area Alerts */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
                <AlertCircle className="h-4 w-4 text-amber-600" /> Focus & Weak Areas
              </h2>
              <span className="text-xs font-bold text-slate-400">{weakAreas.length} Found</span>
            </div>

            {weakAreas.length === 0 ? (
              <div className="text-center py-6 text-slate-500 space-y-1">
                <CheckCircle2 className="h-7 w-7 text-emerald-500 mx-auto" />
                <p className="text-xs font-medium">All quiz and DSA topics performing above target benchmark!</p>
              </div>
            ) : (
              <div className="space-y-3">
                {weakAreas.slice(0, 3).map((w) => (
                  <div key={w.id} className="rounded-xl border border-slate-200 bg-slate-50 p-3 space-y-1">
                    <div className="flex justify-between items-center text-xs font-bold">
                      <span className="text-slate-800">{w.skillName}</span>
                      <span className="text-[10px] px-2 py-0.5 rounded-full bg-red-100 text-red-700">{w.severity}</span>
                    </div>
                    <p className="text-xs text-slate-600 leading-snug">{w.actionPlan}</p>
                  </div>
                ))}
              </div>
            )}

            <Link
              to="/student/career/copilot"
              className="block text-center rounded-xl bg-indigo-50 border border-indigo-200 py-2.5 text-xs font-bold text-indigo-700 hover:bg-indigo-100 transition-colors"
            >
              Ask AI Copilot for Remedy Plan
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
