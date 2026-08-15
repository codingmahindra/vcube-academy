import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { careerApi, type CareerDashboard, type WeakArea } from '../../api/career';
import {
  Compass, Bot, MapPin, Calendar, FileCheck, Brain,
  Sparkles, AlertCircle, ArrowRight, ShieldCheck, CheckCircle2, TrendingUp,
  FileText
} from 'lucide-react';

export default function CareerDashboardPage() {
  const [dashboard, setDashboard] = useState<CareerDashboard | null>(null);
  const [weakAreas, setWeakAreas] = useState<WeakArea[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const [dashData, weakData] = await Promise.all([
          careerApi.getDashboard(),
          careerApi.getWeakAreas(),
        ]);
        setDashboard(dashData);
        setWeakAreas(weakData);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load career dashboard');
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-24">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !dashboard) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center text-red-700">
        <AlertCircle className="mx-auto h-8 w-8 mb-2" />
        <p className="font-semibold">{error || 'Career command center unavailable.'}</p>
      </div>
    );
  }

  const readinessColor =
    dashboard.interviewReadinessStatus === 'READY'
      ? 'text-emerald-700 bg-emerald-50 border-emerald-200'
      : dashboard.interviewReadinessStatus === 'NEEDS_PRACTICE'
      ? 'text-amber-700 bg-amber-50 border-amber-200'
      : 'text-indigo-700 bg-indigo-50 border-indigo-200';

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12">
      {/* Header Banner */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-8 text-white shadow-xl">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center md:justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 rounded-full bg-indigo-500/20 px-3 py-1 text-xs font-semibold text-indigo-300 border border-indigo-400/30">
              <Sparkles className="h-3.5 w-3.5" /> AI Career Command Center
            </div>
            <h1 className="text-2xl md:text-3xl font-bold tracking-tight">
              Welcome back, {dashboard.studentName}
            </h1>
            <p className="text-sm text-slate-300 max-w-2xl leading-relaxed">
              Your personalized Full Stack Java career roadmap is calculated deterministically across your Course progress, MCQ benchmarks, DSA mastery, and ATS resume audits.
            </p>
          </div>

          <div className="flex flex-wrap gap-3">
            <Link
              to="/student/career/copilot"
              className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white shadow-lg hover:bg-indigo-500 transition-colors"
            >
              <Bot className="h-4 w-4" /> Ask Career Copilot
            </Link>
            <Link
              to="/student/career/roadmap"
              className="inline-flex items-center gap-2 rounded-xl bg-slate-800 px-4 py-2.5 text-sm font-semibold text-slate-200 hover:bg-slate-700 border border-slate-700 transition-colors"
            >
              <MapPin className="h-4 w-4" /> View Full Roadmap
            </Link>
          </div>
        </div>
      </div>

      {/* Core Career Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Career Readiness</span>
            <ShieldCheck className="h-5 w-5 text-indigo-600" />
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">{dashboard.profileCompletionPercentage}%</span>
            <span className="text-xs text-slate-500">Completion</span>
          </div>
          <div className="mt-2 w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
            <div className="bg-indigo-600 h-full rounded-full transition-all duration-500" style={{ width: `${dashboard.profileCompletionPercentage}%` }}></div>
          </div>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Interview Readiness</span>
            <Brain className="h-5 w-5 text-emerald-600" />
          </div>
          <div className="mt-3">
            <span className={`inline-block rounded-lg px-2.5 py-1 text-xs font-bold border ${readinessColor}`}>
              {dashboard.interviewReadinessStatus.replace(/_/g, ' ')}
            </span>
          </div>
          <p className="mt-2 text-xs text-slate-500">Avg Mock Score: {dashboard.averageMockScore}%</p>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">ATS Resume Score</span>
            <FileText className="h-5 w-5 text-violet-600" />
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">{dashboard.primaryResumeAtsScore}/100</span>
            <span className="text-xs text-slate-500">Primary Version</span>
          </div>
          <p className="mt-2 text-xs text-slate-500">
            {dashboard.primaryResumeAtsScore >= 75 ? 'Ready for job portal applications' : 'Optimize missing keywords'}
          </p>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Current Milestone</span>
            <TrendingUp className="h-5 w-5 text-amber-600" />
          </div>
          <div className="mt-3">
            <span className="text-base font-bold text-slate-900 line-clamp-1">
              {dashboard.currentRoadmapStage.replace(/_/g, ' ')}
            </span>
          </div>
          <p className="mt-2 text-xs text-slate-500">DSA Solved: {dashboard.dsaProblemsSolved} Problems</p>
        </div>
      </div>

      {/* Main Hub Split Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column: Modules & Actions */}
        <div className="lg:col-span-2 space-y-6">
          <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
              <Compass className="h-5 w-5 text-indigo-600" /> Quick Career Workflows
            </h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Link
                to="/student/career/daily-plan"
                className="group rounded-xl border border-slate-200 p-4 hover:border-indigo-400 hover:bg-indigo-50/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center gap-2 text-indigo-600 font-semibold text-sm mb-1">
                    <Calendar className="h-4 w-4" /> Daily Preparation Plan
                  </div>
                  <p className="text-xs text-slate-600">
                    7 high-yield tasks curated for today including MCQ test, DSA problem & resume keyword refinement.
                  </p>
                </div>
                <div className="mt-3 inline-flex items-center gap-1 text-xs font-medium text-indigo-600 group-hover:translate-x-1 transition-transform">
                  Launch Today's Tasks <ArrowRight className="h-3.5 w-3.5" />
                </div>
              </Link>

              <Link
                to="/student/placement-papers"
                className="group rounded-xl border border-slate-200 p-4 hover:border-indigo-400 hover:bg-indigo-50/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center gap-2 text-emerald-600 font-semibold text-sm mb-1">
                    <FileCheck className="h-4 w-4" /> Company Placement Papers
                  </div>
                  <p className="text-xs text-slate-600">
                    Full-length authentic papers with section timings, negative marking rules, and instant analysis.
                  </p>
                </div>
                <div className="mt-3 inline-flex items-center gap-1 text-xs font-medium text-emerald-600 group-hover:translate-x-1 transition-transform">
                  Browse Exam Papers <ArrowRight className="h-3.5 w-3.5" />
                </div>
              </Link>

              <Link
                to="/student/mock-interview/setup"
                className="group rounded-xl border border-slate-200 p-4 hover:border-indigo-400 hover:bg-indigo-50/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center gap-2 text-violet-600 font-semibold text-sm mb-1">
                    <Brain className="h-4 w-4" /> Mock Interview Simulator
                  </div>
                  <p className="text-xs text-slate-600">
                    Company-specific technical and behavioral mock interview sessions with semantic answer evaluation.
                  </p>
                </div>
                <div className="mt-3 inline-flex items-center gap-1 text-xs font-medium text-violet-600 group-hover:translate-x-1 transition-transform">
                  Start Mock Interview <ArrowRight className="h-3.5 w-3.5" />
                </div>
              </Link>

              <Link
                to="/student/resume/analyzer"
                className="group rounded-xl border border-slate-200 p-4 hover:border-indigo-400 hover:bg-indigo-50/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center gap-2 text-amber-600 font-semibold text-sm mb-1">
                    <FileText className="h-4 w-4" /> ATS Resume Intelligence
                  </div>
                  <p className="text-xs text-slate-600">
                    Upload your latest CV or compare against target Job Portal descriptions to close technical skill gaps.
                  </p>
                </div>
                <div className="mt-3 inline-flex items-center gap-1 text-xs font-medium text-amber-600 group-hover:translate-x-1 transition-transform">
                  Run ATS Analysis <ArrowRight className="h-3.5 w-3.5" />
                </div>
              </Link>
            </div>
          </div>

          {/* Activity Breakdown */}
          <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-lg font-bold text-slate-900 mb-4">Academic & Career Progress Summary</h2>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
              <div className="p-3 bg-slate-50 rounded-xl">
                <p className="text-xl font-bold text-indigo-600">{dashboard.enrolledCoursesCount}</p>
                <p className="text-xs text-slate-500 mt-0.5">Enrolled Courses</p>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl">
                <p className="text-xl font-bold text-emerald-600">{dashboard.completedTopicsCount}</p>
                <p className="text-xs text-slate-500 mt-0.5">Topics Completed</p>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl">
                <p className="text-xl font-bold text-violet-600">{dashboard.mcqQuizzesAttempted}</p>
                <p className="text-xs text-slate-500 mt-0.5">MCQ Quizzes</p>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl">
                <p className="text-xl font-bold text-amber-600">{dashboard.jobApplicationsSubmitted}</p>
                <p className="text-xs text-slate-500 mt-0.5">Job Applications</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right Column: Weak Area Engine */}
        <div className="space-y-6">
          <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
                <AlertCircle className="h-4 w-4 text-amber-600" /> Diagnosis & Weak Areas
              </h2>
              <span className="text-xs font-semibold text-slate-400">{weakAreas.length} Detected</span>
            </div>

            {weakAreas.length === 0 ? (
              <div className="text-center py-8 text-slate-500 space-y-2">
                <CheckCircle2 className="h-8 w-8 text-emerald-500 mx-auto" />
                <p className="text-xs">No critical weaknesses detected in your recent quizzes and submissions.</p>
              </div>
            ) : (
              <div className="space-y-3">
                {weakAreas.map((area) => (
                  <div
                    key={area.id}
                    className="rounded-xl border border-slate-200 bg-slate-50/60 p-3.5 space-y-1.5"
                  >
                    <div className="flex items-center justify-between">
                      <span className="font-semibold text-xs text-slate-800">{area.skillName}</span>
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                          area.severity === 'CRITICAL'
                            ? 'bg-red-100 text-red-700'
                            : 'bg-amber-100 text-amber-700'
                        }`}
                      >
                        {area.severity}
                      </span>
                    </div>
                    <p className="text-xs text-slate-600 leading-relaxed">{area.actionPlan}</p>
                  </div>
                ))}
              </div>
            )}

            <div className="mt-4 pt-4 border-t border-slate-100 text-center">
              <Link
                to="/student/career/copilot"
                className="text-xs font-semibold text-indigo-600 hover:text-indigo-700 inline-flex items-center gap-1"
              >
                Discuss with AI Career Copilot <ArrowRight className="h-3 w-3" />
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
