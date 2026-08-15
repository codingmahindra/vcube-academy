import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { careerApi, type PlacementPaperSummary } from '../../api/career';
import {
  FileCheck, Clock, Award, AlertCircle, ArrowRight,
  Sparkles, CheckCircle2
} from 'lucide-react';

export default function PlacementPaperListPage() {
  const [papers, setPapers] = useState<PlacementPaperSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadPapers() {
      try {
        setLoading(true);
        const data = await careerApi.listPlacementPapers();
        setPapers(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load placement papers');
      } finally {
        setLoading(false);
      }
    }
    loadPapers();
  }, []);

  const getSourceBadge = (source: string) => {
    switch (source) {
      case 'VERIFIED':
        return (
          <span className="inline-flex items-center gap-1 rounded-full bg-emerald-50 px-2.5 py-0.5 text-[11px] font-bold text-emerald-700 border border-emerald-200">
            <CheckCircle2 className="h-3 w-3" /> Verified Company Paper
          </span>
        );
      case 'REPORTED':
        return (
          <span className="inline-flex items-center gap-1 rounded-full bg-amber-50 px-2.5 py-0.5 text-[11px] font-bold text-amber-700 border border-amber-200">
            <AlertCircle className="h-3 w-3" /> Reported Placement Exam
          </span>
        );
      case 'AI_GENERATED':
        return (
          <span className="inline-flex items-center gap-1 rounded-full bg-purple-50 px-2.5 py-0.5 text-[11px] font-bold text-purple-700 border border-purple-200">
            <Sparkles className="h-3 w-3" /> AI Practice Simulator
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1 rounded-full bg-slate-50 px-2.5 py-0.5 text-[11px] font-bold text-slate-700 border border-slate-200">
            Practice Paper
          </span>
        );
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-24">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-6xl mx-auto pb-12">
      {/* Header */}
      <div className="rounded-2xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-8 text-white shadow-xl">
        <div className="space-y-2">
          <div className="inline-flex items-center gap-2 rounded-full bg-indigo-500/20 px-3 py-1 text-xs font-semibold text-indigo-300 border border-indigo-400/30">
            <FileCheck className="h-3.5 w-3.5" /> Company Exam Simulator
          </div>
          <h1 className="text-2xl md:text-3xl font-bold tracking-tight">
            Company Placement Papers & Online Assessments
          </h1>
          <p className="text-xs md:text-sm text-slate-300 max-w-2xl leading-relaxed">
            Practice authentic multi-section online aptitude and technical screening papers with real time constraints, negative marking rules, and sectional breakdowns.
          </p>
        </div>
      </div>

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-xs text-red-700 flex items-center gap-2">
          <AlertCircle className="h-4 w-4" /> {error}
        </div>
      )}

      {/* Placement Papers Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {papers.map((paper) => (
          <div
            key={paper.id}
            className="flex flex-col justify-between rounded-2xl border border-slate-200 bg-white p-6 shadow-sm hover:shadow-md hover:border-indigo-300 transition-all space-y-4"
          >
            <div className="space-y-3">
              <div className="flex items-start justify-between gap-2">
                <span className="rounded-md bg-indigo-50 px-2.5 py-1 text-xs font-bold text-indigo-700">
                  {paper.companyName}
                </span>
                {getSourceBadge(paper.paperSource)}
              </div>

              <h2 className="text-base font-bold text-slate-900 line-clamp-2 leading-snug">
                {paper.title}
              </h2>

              <p className="text-xs text-slate-500 font-medium">
                Target Role: <span className="text-slate-800 font-semibold">{paper.targetRole}</span>
              </p>

              {/* Specs Badge Bar */}
              <div className="grid grid-cols-3 gap-2 pt-2 text-center text-xs">
                <div className="rounded-lg bg-slate-50 p-2 border border-slate-100">
                  <div className="flex items-center justify-center gap-1 text-slate-500 text-[11px] mb-0.5">
                    <Clock className="h-3 w-3" /> Duration
                  </div>
                  <span className="font-bold text-slate-800">{paper.durationMinutes} Mins</span>
                </div>

                <div className="rounded-lg bg-slate-50 p-2 border border-slate-100">
                  <div className="flex items-center justify-center gap-1 text-slate-500 text-[11px] mb-0.5">
                    <Award className="h-3 w-3" /> Total Marks
                  </div>
                  <span className="font-bold text-slate-800">{paper.totalMarks}</span>
                </div>

                <div className="rounded-lg bg-slate-50 p-2 border border-slate-100">
                  <div className="flex items-center justify-center gap-1 text-slate-500 text-[11px] mb-0.5">
                    <FileCheck className="h-3 w-3" /> Questions
                  </div>
                  <span className="font-bold text-slate-800">{paper.questionCount}</span>
                </div>
              </div>
            </div>

            {/* Card Footer */}
            <div className="flex items-center justify-between pt-4 border-t border-slate-100">
              <div className="text-xs text-slate-500">
                {paper.isAttempted ? (
                  <span className="inline-flex items-center gap-1 text-emerald-600 font-semibold">
                    <CheckCircle2 className="h-3.5 w-3.5" /> Attempted (Best: {paper.bestScore}%)
                  </span>
                ) : (
                  <span>Pass mark: {paper.passingMarks}%</span>
                )}
              </div>

              <div className="flex items-center gap-2">
                <Link
                  to={`/student/placement-papers/${paper.id}`}
                  className="rounded-xl border border-slate-200 bg-white px-3.5 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
                >
                  Details
                </Link>
                <Link
                  to={`/student/placement-papers/${paper.id}/attempt`}
                  className="inline-flex items-center gap-1.5 rounded-xl bg-indigo-600 px-4 py-2 text-xs font-semibold text-white shadow-xs hover:bg-indigo-500 transition-colors"
                >
                  Start Exam <ArrowRight className="h-3.5 w-3.5" />
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
