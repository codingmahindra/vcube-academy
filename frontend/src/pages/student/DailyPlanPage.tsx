import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { careerApi, type DailyPlan } from '../../api/career';

import {
  Calendar,
  CheckCircle,
  Circle,
  ArrowRight,
  TrendingUp,
  AlertCircle,
  Sparkles,
  BookOpen,
  Code2,
  Brain,
  FileText,
  FileCheck,
  ShieldCheck,
} from 'lucide-react';

export default function DailyPlanPage() {
  const [dailyPlan, setDailyPlan] = useState<DailyPlan | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [togglingId, setTogglingId] = useState<number | null>(null);

  useEffect(() => {
    async function loadPlan() {
      try {
        setLoading(true);

        const data = await careerApi.getDailyPlan();

        setDailyPlan(data);
      } catch (err: any) {
        console.error('Daily plan error:', err);

        setError(
          err.response?.data?.message ||
            'Failed to load daily preparation plan'
        );
      } finally {
        setLoading(false);
      }
    }

    loadPlan();
  }, []);

  /*
   * Mark/unmark task as completed
   */
  const handleToggle = async (taskId: number) => {
    try {
      setTogglingId(taskId);
      setError(null);

      const updatedPlan =
        await careerApi.toggleDailyTask(taskId);

      setDailyPlan(updatedPlan);
    } catch (err: any) {
      console.error('Toggle task error:', err);

      setError(
        err.response?.data?.message ||
          'Failed to update task status'
      );
    } finally {
      setTogglingId(null);
    }
  };

  /*
   * Icons according to backend category
   */
  const getCategoryIcon = (category: string) => {
    switch (category) {
      case 'JAVA_TOPIC':
        return (
          <BookOpen className="h-4 w-4 text-blue-600" />
        );

      case 'MCQ_PRACTICE':
        return (
          <Brain className="h-4 w-4 text-emerald-600" />
        );

      case 'DSA_PROBLEM':
        return (
          <Code2 className="h-4 w-4 text-purple-600" />
        );

      case 'SQL_PRACTICE':
        return (
          <FileText className="h-4 w-4 text-amber-600" />
        );

      case 'INTERVIEW_QA':
        return (
          <FileCheck className="h-4 w-4 text-indigo-600" />
        );

      case 'MOCK_INTERVIEW':
        return (
          <ShieldCheck className="h-4 w-4 text-violet-600" />
        );

      case 'JOB_APPLY':
        return (
          <FileText className="h-4 w-4 text-green-600" />
        );

      default:
        return (
          <Sparkles className="h-4 w-4 text-slate-600" />
        );
    }
  };

  /*
   * Loading
   */
  if (loading) {
    return (
      <div className="flex items-center justify-center py-24">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  /*
   * Error
   */
  if (error || !dailyPlan) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center text-red-700">
        <AlertCircle className="mx-auto h-8 w-8 mb-2" />

        <p className="font-semibold">
          {error || 'Daily preparation plan unavailable.'}
        </p>
      </div>
    );
  }

  /*
   * Main page
   */
  return (
    <div className="space-y-8 max-w-4xl mx-auto pb-12">

      {/* ================= HEADER ================= */}

      <div className="rounded-2xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-8 text-white shadow-xl">

        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">

          <div className="space-y-1.5">

            <div className="inline-flex items-center gap-2 rounded-full bg-indigo-500/20 px-3 py-1 text-xs font-semibold text-indigo-300 border border-indigo-400/30">

              <Calendar className="h-3.5 w-3.5" />

              Target Routine: {dailyPlan.planDate}

            </div>

            <h1 className="text-2xl md:text-3xl font-bold tracking-tight">
              Daily High-Yield Preparation Plan
            </h1>

            <p className="text-xs md:text-sm text-slate-300 max-w-xl">
              Consistent daily practice across MCQ quizzes,
              DSA coding patterns, ATS resume audits, and
              mock rounds ensures placement readiness.
            </p>

          </div>

          {/* Progress */}

          <div className="rounded-2xl bg-white/10 backdrop-blur-md p-5 border border-white/10 text-center min-w-[160px]">

            <p className="text-xs uppercase font-semibold text-slate-300 tracking-wider">
              Today's Progress
            </p>

            <p className="text-3xl font-bold text-white mt-1">
              {dailyPlan.completedTasks} / {dailyPlan.totalTasks}
            </p>

            <div className="mt-2 w-full bg-white/20 rounded-full h-1.5 overflow-hidden">

              <div
                className="bg-emerald-400 h-full rounded-full transition-all duration-500"
                style={{
                  width: `${dailyPlan.completionPercentage}%`,
                }}
              />

            </div>

            <p className="text-[11px] text-emerald-300 mt-1 font-medium">
              {dailyPlan.completionPercentage}% Completed
            </p>

          </div>

        </div>
      </div>

      {/* ================= TASKS ================= */}

      <div className="space-y-4">

        <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">

          <TrendingUp className="h-4 w-4 text-indigo-600" />

          Focus Tasks for Today

        </h2>

        <div className="space-y-3">

          {dailyPlan.items
            .sort(
              (a, b) =>
                a.displayOrder - b.displayOrder
            )
            .map((item, idx) => (

            <div
              key={item.id}
              className={`flex flex-col sm:flex-row sm:items-center justify-between gap-4 rounded-xl border p-4 transition-all ${
                item.isCompleted
                  ? 'border-emerald-200 bg-emerald-50/40 text-slate-600'
                  : 'border-slate-200 bg-white hover:border-indigo-300 hover:shadow-sm text-slate-800'
              }`}
            >

              {/* LEFT SIDE */}

              <div className="flex items-start gap-3.5">

                {/* Checkbox */}

                <button
                  type="button"
                  onClick={() =>
                    handleToggle(item.id)
                  }
                  disabled={
                    togglingId === item.id
                  }
                  className={`mt-0.5 flex-shrink-0 transition-transform active:scale-90 ${
                    item.isCompleted
                      ? 'text-emerald-600'
                      : 'text-slate-400 hover:text-indigo-600'
                  }`}
                >

                  {item.isCompleted ? (
                    <CheckCircle className="h-5 w-5" />
                  ) : (
                    <Circle className="h-5 w-5" />
                  )}

                </button>

                {/* Task information */}

                <div className="space-y-1">

                  <div className="flex items-center gap-2">

                    <span className="p-1 rounded-md bg-slate-100">
                      {getCategoryIcon(
                        item.category
                      )}
                    </span>

                    <span
                      className={`text-sm font-semibold ${
                        item.isCompleted
                          ? 'line-through text-slate-500'
                          : 'text-slate-900'
                      }`}
                    >
                      {idx + 1}. {item.title}
                    </span>

                  </div>

                  <p className="text-xs text-slate-500 leading-relaxed max-w-xl">
                    Complete the required target for this task.
                  </p>

                </div>

              </div>

              {/* RIGHT SIDE */}

              <div className="flex items-center justify-between sm:justify-end gap-3 pl-8 sm:pl-0">

                {/* Target */}

                <span className="text-[11px] font-mono font-medium text-slate-400">
                  Target: {item.completedCount}/
                  {item.targetCount}
                </span>

                {/* OPEN BUTTON */}

                <Link
                  to={item.actionLink}
                  className={`inline-flex items-center gap-1.5 rounded-xl px-3.5 py-1.5 text-xs font-semibold shadow-sm transition-colors ${
                    item.isCompleted
                      ? 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                      : 'bg-indigo-600 text-white hover:bg-indigo-500'
                  }`}
                >

                  Open

                  <ArrowRight className="h-3 w-3" />

                </Link>

              </div>

            </div>

          ))}

        </div>

      </div>

    </div>
  );
}