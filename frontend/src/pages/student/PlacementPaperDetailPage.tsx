import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { careerApi, type PlacementPaperDetail } from '../../api/career';
import {
  FileCheck, Clock, Award, AlertCircle, ArrowRight,
  ArrowLeft, CheckCircle2, ShieldCheck, HelpCircle
} from 'lucide-react';

export default function PlacementPaperDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [paper, setPaper] = useState<PlacementPaperDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadDetail() {
      if (!id) return;
      try {
        setLoading(true);
        const data = await careerApi.getPlacementPaperDetail(Number(id));
        setPaper(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load paper details');
      } finally {
        setLoading(false);
      }
    }
    loadDetail();
  }, [id]);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-24">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !paper) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center text-red-700">
        <AlertCircle className="mx-auto h-8 w-8 mb-2" />
        <p className="font-semibold">{error || 'Placement paper not found'}</p>
        <Link to="/student/placement-papers" className="mt-3 inline-flex items-center gap-1 text-xs font-semibold text-red-600 underline">
          <ArrowLeft className="h-3.5 w-3.5" /> Back to list
        </Link>
      </div>
    );
  }

  // Group questions by section
  const sections = Array.from(new Set(paper.questions.map((q) => q.sectionName)));

  return (
    <div className="space-y-8 max-w-4xl mx-auto pb-12">
      <Link
        to="/student/placement-papers"
        className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-indigo-600 transition-colors"
      >
        <ArrowLeft className="h-4 w-4" /> Back to Placement Papers
      </Link>

      {/* Hero Banner */}
      <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm space-y-6">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <span className="rounded-md bg-indigo-50 px-3 py-1 text-xs font-bold text-indigo-700">
            {paper.companyName} • {paper.year}
          </span>
          <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700 border border-emerald-200">
            Source: {paper.paperSource}
          </span>
        </div>

        <div>
          <h1 className="text-2xl font-bold text-slate-900">{paper.title}</h1>
          <p className="text-sm text-slate-500 mt-1">
            Target Role: <span className="text-slate-800 font-semibold">{paper.targetRole}</span> | Round:{' '}
            <span className="text-slate-800 font-semibold">{paper.roundName}</span>
          </p>
        </div>

        {/* Specs */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-2">
          <div className="rounded-xl bg-slate-50 p-4 border border-slate-100 text-center">
            <Clock className="h-5 w-5 text-indigo-600 mx-auto mb-1" />
            <p className="text-xs text-slate-500">Duration</p>
            <p className="text-base font-bold text-slate-900">{paper.durationMinutes} Minutes</p>
          </div>

          <div className="rounded-xl bg-slate-50 p-4 border border-slate-100 text-center">
            <Award className="h-5 w-5 text-emerald-600 mx-auto mb-1" />
            <p className="text-xs text-slate-500">Total Marks</p>
            <p className="text-base font-bold text-slate-900">{paper.totalMarks} Marks</p>
          </div>

          <div className="rounded-xl bg-slate-50 p-4 border border-slate-100 text-center">
            <ShieldCheck className="h-5 w-5 text-violet-600 mx-auto mb-1" />
            <p className="text-xs text-slate-500">Passing Cutoff</p>
            <p className="text-base font-bold text-slate-900">{paper.passingMarks}%</p>
          </div>

          <div className="rounded-xl bg-slate-50 p-4 border border-slate-100 text-center">
            <HelpCircle className="h-5 w-5 text-amber-600 mx-auto mb-1" />
            <p className="text-xs text-slate-500">Questions</p>
            <p className="text-base font-bold text-slate-900">{paper.questions.length}</p>
          </div>
        </div>

        {/* Instructions */}
        <div className="rounded-xl bg-slate-50 p-5 border border-slate-200 space-y-2">
          <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2">
            <FileCheck className="h-4 w-4 text-indigo-600" /> Exam Rules & Instructions
          </h2>
          <p className="text-xs text-slate-600 leading-relaxed whitespace-pre-wrap">
            {paper.instructions}
          </p>
          <ul className="text-xs text-slate-500 list-disc list-inside space-y-1 pt-1">
            <li>Timer starts immediately upon entering the attempt session.</li>
            <li>You can navigate between questions and sections freely.</li>
            <li>Instant sectional evaluation and comprehensive feedback will be generated on submission.</li>
          </ul>
        </div>

        {/* Section Structure Breakdown */}
        <div className="space-y-3">
          <h2 className="text-sm font-bold text-slate-900">Included Sections</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {sections.map((sec) => {
              const count = paper.questions.filter((q) => q.sectionName === sec).length;
              return (
                <div key={sec} className="flex items-center justify-between rounded-xl border border-slate-200 p-3.5 bg-white">
                  <span className="font-semibold text-xs text-slate-800">{sec}</span>
                  <span className="text-xs text-slate-500 font-mono">{count} Questions</span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Launch CTA */}
        <div className="pt-4 border-t border-slate-100 flex justify-end">
          <Link
            to={`/student/placement-papers/${paper.id}/attempt`}
            className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-6 py-3 text-sm font-semibold text-white shadow-md hover:bg-indigo-500 transition-colors"
          >
            Start Placement Examination <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </div>
    </div>
  );
}
