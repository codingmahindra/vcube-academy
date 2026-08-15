import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { interviewApi, type CompanyDetailDto } from '../../../api/interview';
import {
  Building2, ChevronLeft, ChevronRight, CheckCircle2,
  Clock, Play, Loader2, Sparkles, HelpCircle, Layers
} from 'lucide-react';
import toast from 'react-hot-toast';

export function InterviewCompanyDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [company, setCompany] = useState<CompanyDetailDto | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadCompany() {
      if (!id) return;
      try {
        setLoading(true);
        const data = await interviewApi.getCompanyDetail(Number(id));
        setCompany(data);
      } catch (err) {
        toast.error('Failed to load company detail');
        navigate('/student/interview/companies');
      } finally {
        setLoading(false);
      }
    }
    loadCompany();
  }, [id, navigate]);

  if (loading || !company) {
    return (
      <div className="flex h-72 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex items-center gap-3">
        <Link
          to="/student/interview/companies"
          className="p-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600"
        >
          <ChevronLeft className="h-4 w-4" />
        </Link>
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-bold text-slate-800">{company.name}</h1>
            <span className="badge bg-slate-100 text-slate-700 text-xs">{company.tier}</span>
          </div>
          <p className="text-xs text-slate-500 mt-0.5">{company.industry} • {company.totalQuestions} Questions</p>
        </div>
      </div>

      {/* Hiring Process Card */}
      {company.hiringRoundsInfo && (
        <div className="card p-5 bg-gradient-to-br from-indigo-50/50 to-purple-50/30 border-indigo-100 space-y-2">
          <div className="flex items-center gap-2 text-indigo-900 font-bold text-xs uppercase tracking-wider">
            <Sparkles className="h-4 w-4 text-indigo-600" /> Hiring Rounds & Selection Structure
          </div>
          <p className="text-xs text-slate-700 leading-relaxed whitespace-pre-wrap">{company.hiringRoundsInfo}</p>
        </div>
      )}

      {/* Questions Table */}
      <div className="rounded-2xl bg-white border border-slate-100 shadow-sm overflow-hidden">
        <div className="px-5 py-4 border-b border-slate-100 flex items-center justify-between">
          <h2 className="text-sm font-bold text-slate-800">Reported Interview Questions</h2>
          <Link
            to={`/student/interview/mock?companyId=${company.id}`}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-brand-600 text-white text-xs font-semibold hover:bg-brand-500 shadow-sm"
          >
            <Play className="h-3.5 w-3.5" /> Start {company.name} Mock Interview
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 border-b border-slate-100 text-slate-500 font-semibold uppercase text-[10px]">
              <tr>
                <th className="px-5 py-3">Question</th>
                <th className="px-5 py-3">Domain</th>
                <th className="px-5 py-3">Difficulty</th>
                <th className="px-5 py-3">Round</th>
                <th className="px-5 py-3">Source</th>
                <th className="px-5 py-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {company.questions.map((q) => (
                <tr
                  key={q.id}
                  onClick={() => navigate(`/student/interview/questions?questionId=${q.id}`)}
                  className="hover:bg-brand-50/20 cursor-pointer transition-colors"
                >
                  <td className="px-5 py-3.5 font-bold text-slate-800 max-w-sm truncate">
                    {q.questionText}
                  </td>
                  <td className="px-5 py-3.5 text-slate-600 font-medium">{q.categoryName}</td>
                  <td className="px-5 py-3.5">
                    <span
                      className={`badge text-[10px] ${
                        q.difficulty === 'BASIC'
                          ? 'bg-emerald-100 text-emerald-700'
                          : q.difficulty === 'INTERMEDIATE'
                          ? 'bg-amber-100 text-amber-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {q.difficulty}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 text-slate-500 text-[11px]">{q.interviewRound.replace(/_/g, ' ')}</td>
                  <td className="px-5 py-3.5">
                    <span className="badge bg-slate-100 text-slate-600 text-[9px]">
                      {q.questionSource.replace(/_/g, ' ')}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 text-right">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        navigate(`/student/interview/questions?questionId=${q.id}`);
                      }}
                      className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-brand-50 text-brand-700 font-semibold text-xs hover:bg-brand-100"
                    >
                      Practice &rarr;
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
