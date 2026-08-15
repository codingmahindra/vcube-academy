import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { careerApi, type CareerRoadmap } from '../../api/career';
import {
  MapPin, CheckCircle2, Lock, ArrowRight,
  TrendingUp, AlertCircle, Bot
} from 'lucide-react';

export default function CareerRoadmapPage() {
  const [roadmap, setRoadmap] = useState<CareerRoadmap | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadRoadmap() {
      try {
        setLoading(true);
        const data = await careerApi.getRoadmap();
        setRoadmap(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load career roadmap');
      } finally {
        setLoading(false);
      }
    }
    loadRoadmap();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-24">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !roadmap) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center text-red-700">
        <AlertCircle className="mx-auto h-8 w-8 mb-2" />
        <p className="font-semibold">{error || 'Career roadmap unavailable.'}</p>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-12">
      {/* Header */}
      <div className="rounded-2xl bg-gradient-to-r from-indigo-900 via-slate-900 to-indigo-950 p-8 text-white shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
          <div>
            <div className="inline-flex items-center gap-2 rounded-full bg-indigo-500/20 px-3 py-1 text-xs font-semibold text-indigo-300 border border-indigo-400/30 mb-2">
              <MapPin className="h-3.5 w-3.5" /> Deterministic Career Trajectory
            </div>
            <h1 className="text-2xl md:text-3xl font-bold tracking-tight">
              Target Role: {roadmap.targetRole}
            </h1>
            <p className="text-sm text-slate-300 mt-1 max-w-2xl">
              Calculated across 9 milestone stages. Stages unlock sequentially as you complete coursework, pass MCQ checkpoints, solve DSA patterns, and optimize ATS scores.
            </p>
          </div>

          <div className="rounded-xl bg-white/10 backdrop-blur-md p-4 border border-white/10 text-center min-w-[140px]">
            <p className="text-xs uppercase font-semibold tracking-wider text-slate-300">Overall Readiness</p>
            <p className="text-3xl font-bold text-white mt-1">{roadmap.overallReadinessPercentage}%</p>
            <p className="text-[11px] text-emerald-400 font-medium mt-0.5">Verified Completion</p>
          </div>
        </div>
      </div>

      {/* Stage Progression Timeline */}
      <div className="space-y-6">
        {roadmap.stages.map((stage, idx) => {
          const isCompleted = stage.status === 'COMPLETED';
          const isInProgress = stage.status === 'IN_PROGRESS';
          const isLocked = stage.status === 'LOCKED';

          return (
            <div
              key={stage.stage}
              className={`relative rounded-2xl border p-6 transition-all ${
                isCompleted
                  ? 'border-emerald-200 bg-emerald-50/30'
                  : isInProgress
                  ? 'border-indigo-300 bg-white shadow-md ring-2 ring-indigo-500/20'
                  : 'border-slate-200 bg-slate-50/60 opacity-75'
              }`}
            >
              <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
                <div className="flex items-start gap-4">
                  {/* Status Indicator Icon */}
                  <div
                    className={`flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-xl font-bold text-sm shadow-sm ${
                      isCompleted
                        ? 'bg-emerald-600 text-white'
                        : isInProgress
                        ? 'bg-indigo-600 text-white animate-pulse'
                        : 'bg-slate-200 text-slate-500'
                    }`}
                  >
                    {isCompleted ? (
                      <CheckCircle2 className="h-5 w-5" />
                    ) : isLocked ? (
                      <Lock className="h-4 w-4" />
                    ) : (
                      idx + 1
                    )}
                  </div>

                  {/* Stage Details */}
                  <div className="space-y-1">
                    <div className="flex items-center gap-2.5">
                      <h2 className="text-base font-bold text-slate-900">
                        {idx + 1}. {stage.stageName}
                      </h2>
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                          isCompleted
                            ? 'bg-emerald-100 text-emerald-800'
                            : isInProgress
                            ? 'bg-indigo-100 text-indigo-800'
                            : 'bg-slate-200 text-slate-600'
                        }`}
                      >
                        {stage.status}
                      </span>
                    </div>

                    <p className="text-xs text-slate-600 leading-relaxed max-w-2xl">
                      {stage.description}
                    </p>

                    <div className="pt-2 text-xs font-semibold text-slate-700 flex items-center gap-2">
                      <span className="text-slate-400 font-normal">Audit Metrics:</span>
                      <span className="bg-white px-2.5 py-1 rounded-md border border-slate-200 shadow-2xs font-mono text-[11px]">
                        {stage.metricsSummary}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Progress & Action CTA */}
                <div className="flex flex-col md:items-end gap-3 min-w-[200px] pt-2 md:pt-0">
                  <div className="w-full md:w-48 text-right">
                    <div className="flex justify-between text-xs font-semibold text-slate-600 mb-1">
                      <span>Milestone Progress</span>
                      <span>{stage.progressPercentage}%</span>
                    </div>
                    <div className="w-full bg-slate-200 rounded-full h-1.5 overflow-hidden">
                      <div
                        className={`h-full rounded-full transition-all duration-500 ${
                          isCompleted ? 'bg-emerald-600' : 'bg-indigo-600'
                        }`}
                        style={{ width: `${stage.progressPercentage}%` }}
                      ></div>
                    </div>
                  </div>

                  {!isLocked && stage.actionRoute && (
                    <Link
                      to={stage.actionRoute}
                      className={`inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold shadow-xs transition-colors ${
                        isCompleted
                          ? 'bg-white border border-slate-300 text-slate-700 hover:bg-slate-50'
                          : 'bg-indigo-600 text-white hover:bg-indigo-500'
                      }`}
                    >
                      {stage.recommendedAction}
                      <ArrowRight className="h-3.5 w-3.5" />
                    </Link>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Floating Copilot Consultation Banner */}
      <div className="rounded-2xl border border-indigo-100 bg-gradient-to-r from-indigo-50 to-white p-6 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-600 text-white">
            <Bot className="h-5 w-5" />
          </div>
          <div>
            <h3 className="text-sm font-bold text-slate-900">Need personalized guidance on unlocking stages?</h3>
            <p className="text-xs text-slate-600">
              The AI Career Copilot can identify which specific topic quizzes or DSA patterns will advance your stage the fastest.
            </p>
          </div>
        </div>
        <Link
          to="/student/career/copilot"
          className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-xs font-semibold text-white hover:bg-indigo-500 transition-colors whitespace-nowrap"
        >
          Consult Copilot <ArrowRight className="h-3.5 w-3.5" />
        </Link>
      </div>
    </div>
  );
}
