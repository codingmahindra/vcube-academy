import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { interviewApi, type MockInterviewResultDto } from '../../../api/interview';
import {
  Award, CheckCircle2, AlertTriangle, ArrowLeft,
  Sparkles, RefreshCw, BookOpen, Star, Loader2, ShieldCheck
} from 'lucide-react';
import toast from 'react-hot-toast';

export function MockInterviewReportPage() {
  const { id } = useParams<{ id: string }>();
  const [result, setResult] = useState<MockInterviewResultDto | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadResult() {
      if (!id) return;
      try {
        setLoading(true);
        // complete / get result
        const data = await interviewApi.completeMockInterview(Number(id));
        setResult(data);
      } catch (err) {
        toast.error('Failed to load interview report');
      } finally {
        setLoading(false);
      }
    }
    loadResult();
  }, [id]);

  if (loading || !result) {
    return (
      <div className="flex h-72 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  const readinessColor =
    result.recommendationStatus === 'READY_FOR_INTERVIEW'
      ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
      : result.recommendationStatus === 'NEEDS_MORE_PREPARATION'
      ? 'bg-amber-50 text-amber-700 border-amber-200'
      : 'bg-red-50 text-red-700 border-red-200';

  const readinessText =
    result.recommendationStatus === 'READY_FOR_INTERVIEW'
      ? 'Ready for Company Interviews'
      : result.recommendationStatus === 'NEEDS_MORE_PREPARATION'
      ? 'Needs More Preparation'
      : 'Fundamental Revision Required';

  return (
    <div className="space-y-6 max-w-5xl mx-auto pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-bold text-slate-800">{result.title}</h1>
            <span className={`badge border text-xs font-bold ${readinessColor}`}>
              {readinessText}
            </span>
          </div>
          <p className="text-xs text-slate-500 mt-0.5">
            Target Company: {result.targetCompanyName} • Completed {result.totalQuestions} Questions
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link
            to="/student/interview/mock"
            className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-brand-600 text-white text-xs font-semibold hover:bg-brand-500 shadow-md shadow-brand-600/20"
          >
            <RefreshCw className="h-3.5 w-3.5" /> Start Another Mock
          </Link>
          <Link
            to="/student/interview"
            className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-white border border-slate-200 text-slate-700 text-xs font-semibold hover:bg-slate-50"
          >
            <ArrowLeft className="h-3.5 w-3.5" /> Back to Dashboard
          </Link>
        </div>
      </div>

      {/* Primary Score & Feedback Card */}
      <div className="card p-6 bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 text-white space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1">
            <span className="text-xs font-semibold text-indigo-300 uppercase tracking-wider">
              Overall Benchmark Performance
            </span>
            <div className="flex items-baseline gap-2">
              <span className="text-4xl font-black text-white">{result.overallScore}</span>
              <span className="text-sm text-slate-400">/ 100</span>
            </div>
            <p className="text-xs text-slate-300 max-w-xl leading-relaxed mt-2">
              {result.feedbackSummary}
            </p>
          </div>

          <div className="flex sm:flex-col items-center justify-center p-4 rounded-xl bg-white/10 border border-white/10 text-center min-w-[140px]">
            <Award className="h-8 w-8 text-amber-400 mb-1" />
            <span className="text-xs text-slate-300 font-medium">Readiness Index</span>
            <span className="text-xl font-bold text-white mt-0.5">{result.interviewReadinessPercentage}%</span>
          </div>
        </div>
      </div>

      {/* Multi-Dimensional Domain Scores */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        <div className="stat-card p-4 text-center">
          <p className="text-[11px] font-semibold text-slate-500">Java & OOP</p>
          <p className="text-xl font-bold text-slate-800 mt-1">{result.javaScore ?? '-'}</p>
        </div>
        <div className="stat-card p-4 text-center">
          <p className="text-[11px] font-semibold text-slate-500">SQL & DB</p>
          <p className="text-xl font-bold text-slate-800 mt-1">{result.sqlScore ?? '-'}</p>
        </div>
        <div className="stat-card p-4 text-center">
          <p className="text-[11px] font-semibold text-slate-500">Spring Boot</p>
          <p className="text-xl font-bold text-slate-800 mt-1">{result.springScore ?? '-'}</p>
        </div>
        <div className="stat-card p-4 text-center">
          <p className="text-[11px] font-semibold text-slate-500">DSA & Logic</p>
          <p className="text-xl font-bold text-slate-800 mt-1">{result.dsaScore ?? '-'}</p>
        </div>
        <div className="stat-card p-4 text-center">
          <p className="text-[11px] font-semibold text-slate-500">Clarity & Comm</p>
          <p className="text-xl font-bold text-slate-800 mt-1">{result.communicationScore ?? '-'}</p>
        </div>
        <div className="stat-card p-4 text-center">
          <p className="text-[11px] font-semibold text-slate-500">HR & Behavioral</p>
          <p className="text-xl font-bold text-slate-800 mt-1">{result.hrScore ?? '-'}</p>
        </div>
      </div>

      {/* Strengths, Weaknesses, and Revision */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="card p-5 space-y-3">
          <h3 className="text-xs font-bold text-emerald-800 flex items-center gap-1.5 uppercase tracking-wider">
            <CheckCircle2 className="h-4 w-4 text-emerald-600" /> Strengths Demonstrated
          </h3>
          {result.strongAreas.length === 0 ? (
            <p className="text-xs text-slate-400">Continue practicing to establish technical strengths.</p>
          ) : (
            <ul className="list-disc pl-5 text-xs text-slate-700 space-y-1">
              {result.strongAreas.map((s, idx) => (
                <li key={idx}>{s}</li>
              ))}
            </ul>
          )}
        </div>

        <div className="card p-5 space-y-3">
          <h3 className="text-xs font-bold text-amber-800 flex items-center gap-1.5 uppercase tracking-wider">
            <AlertTriangle className="h-4 w-4 text-amber-600" /> Focus Revision Areas
          </h3>
          {result.weakAreas.length === 0 ? (
            <p className="text-xs text-emerald-600 font-medium">No severe weaknesses identified!</p>
          ) : (
            <ul className="list-disc pl-5 text-xs text-slate-700 space-y-1">
              {result.weakAreas.map((w, idx) => (
                <li key={idx}>{w}</li>
              ))}
            </ul>
          )}
        </div>
      </div>

      {/* Question by Question Review */}
      <div className="rounded-2xl bg-white border border-slate-100 shadow-sm p-6 space-y-4">
        <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2">
          <BookOpen className="h-4 w-4 text-brand-600" /> Question-by-Question Detailed Breakdown
        </h2>

        <div className="space-y-4">
          {result.questionEvaluations.map((q, idx) => (
            <div key={q.id} className="p-4 rounded-xl border border-slate-100 bg-slate-50/50 space-y-3">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <span className="badge bg-brand-50 text-brand-700 text-xs font-bold">
                  Question #{idx + 1} ({q.categoryName} • {q.topicName})
                </span>
                <span className="badge bg-white border border-slate-200 text-slate-800 font-bold text-xs">
                  Score: {q.score} / 100
                </span>
              </div>

              <p className="text-xs font-bold text-slate-900">{q.questionText}</p>

              <div>
                <p className="text-[11px] font-bold text-slate-500 uppercase">Your Answer:</p>
                <p className="text-xs text-slate-700 mt-0.5 bg-white p-2.5 rounded-lg border border-slate-100">
                  {q.userAnswer || 'No answer recorded'}
                </p>
              </div>

              <div>
                <p className="text-[11px] font-bold text-brand-600 uppercase">Evaluation & Feedback:</p>
                <p className="text-xs text-slate-700 mt-0.5">{q.feedback}</p>
              </div>

              {q.expectedAnswer && (
                <div>
                  <p className="text-[11px] font-bold text-emerald-700 uppercase">Ideal Expected Answer:</p>
                  <p className="text-xs text-slate-700 mt-0.5 whitespace-pre-wrap bg-emerald-50/30 p-2.5 rounded-lg border border-emerald-100">
                    {q.expectedAnswer}
                  </p>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
