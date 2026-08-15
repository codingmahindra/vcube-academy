import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { interviewApi, type CompanySummaryDto, type MockInterviewResponse } from '../../../api/interview';
import {
  GraduationCap, Building2, Play, Clock, Sparkles,
  ChevronLeft, Award, CheckCircle2, Loader2
} from 'lucide-react';
import toast from 'react-hot-toast';

export function MockInterviewSetupPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const companyIdParam = searchParams.get('companyId');

  const [companies, setCompanies] = useState<CompanySummaryDto[]>([]);
  const [pastMocks, setPastMocks] = useState<MockInterviewResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [starting, setStarting] = useState(false);

  const [roleTitle, setRoleTitle] = useState('Java Full Stack Developer');
  const [targetCompanyId, setTargetCompanyId] = useState<string>(companyIdParam || 'ALL');
  const [difficulty, setDifficulty] = useState('INTERMEDIATE');
  const [totalQuestions, setTotalQuestions] = useState(5);

  useEffect(() => {
    async function loadSetupData() {
      try {
        setLoading(true);
        const [compData, pastData] = await Promise.all([
          interviewApi.getCompanies(),
          interviewApi.getUserMockInterviews(0, 5),
        ]);
        setCompanies(compData);
        setPastMocks(pastData.content);
        if (companyIdParam) {
          setTargetCompanyId(companyIdParam);
        }
      } catch (err) {
        toast.error('Failed to load mock setup data');
      } finally {
        setLoading(false);
      }
    }
    loadSetupData();
  }, [companyIdParam]);

  async function handleStart() {
    try {
      setStarting(true);
      const res = await interviewApi.startMockInterview({
        roleTitle,
        targetCompanyId: targetCompanyId !== 'ALL' ? Number(targetCompanyId) : undefined,
        difficulty,
        totalQuestions,
        interviewType: 'TECHNICAL',
      });
      toast.success('Mock Interview generated!');
      navigate(`/student/interview/mock/${res.id}`);
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to start mock interview');
    } finally {
      setStarting(false);
    }
  }

  if (loading) {
    return (
      <div className="flex h-72 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-5xl mx-auto pb-12">
      {/* Header */}
      <div className="flex items-center gap-3">
        <Link
          to="/student/interview"
          className="p-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600"
        >
          <ChevronLeft className="h-4 w-4" />
        </Link>
        <div>
          <h1 className="text-xl font-bold text-slate-800">Mock Interview Simulator</h1>
          <p className="text-xs text-slate-500">Live 1-on-1 simulated technical rounds with automated multi-dimensional evaluation</p>
        </div>
      </div>

      {/* Setup Form */}
      <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
        <div className="md:col-span-7 card p-6 space-y-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
            <Sparkles className="h-4 w-4 text-brand-600" /> Configure Interview Session
          </h2>

          <div className="space-y-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Target Job Role</label>
              <input
                type="text"
                className="input text-xs"
                value={roleTitle}
                onChange={(e) => setRoleTitle(e.target.value)}
                placeholder="e.g. Java Full Stack Developer, Backend SDE"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Target Company Track</label>
              <select
                className="input text-xs"
                value={targetCompanyId}
                onChange={(e) => setTargetCompanyId(e.target.value)}
              >
                <option value="ALL">General Java Full Stack (All Top MNCs)</option>
                {companies.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name} ({c.tier})
                  </option>
                ))}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Difficulty</label>
                <select
                  className="input text-xs"
                  value={difficulty}
                  onChange={(e) => setDifficulty(e.target.value)}
                >
                  <option value="BASIC">Basic (Freshers / Campus)</option>
                  <option value="INTERMEDIATE">Intermediate (0-2 Years)</option>
                  <option value="ADVANCED">Advanced (Senior / SDE)</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Number of Questions</label>
                <select
                  className="input text-xs"
                  value={totalQuestions}
                  onChange={(e) => setTotalQuestions(Number(e.target.value))}
                >
                  <option value={3}>3 Questions (Quick Sprint)</option>
                  <option value={5}>5 Questions (Standard Round)</option>
                  <option value={10}>10 Questions (Deep Dive)</option>
                </select>
              </div>
            </div>
          </div>

          <div className="pt-4 border-t border-slate-100 flex items-center justify-between">
            <span className="text-[11px] text-slate-400">Estimated duration: {totalQuestions * 3} mins</span>
            <button
              onClick={handleStart}
              disabled={starting}
              className="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-brand-600 text-white font-semibold text-xs hover:bg-brand-500 shadow-md shadow-brand-600/30 transition-all disabled:opacity-50"
            >
              {starting ? <Loader2 className="h-4 w-4 animate-spin" /> : <Play className="h-4 w-4" />}
              Launch Live Interview
            </button>
          </div>
        </div>

        {/* Past Sessions Sidebar */}
        <div className="md:col-span-5 space-y-4">
          <div className="card p-5 space-y-3">
            <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
              Recent Mock Results
            </h3>
            {pastMocks.length === 0 ? (
              <p className="text-xs text-slate-400">No mock interviews completed yet. Complete your first session to receive your readiness benchmark!</p>
            ) : (
              <div className="space-y-2.5">
                {pastMocks.map((m) => (
                  <div
                    key={m.id}
                    onClick={() => navigate(m.status === 'COMPLETED' ? `/student/interview/result/${m.id}` : `/student/interview/mock/${m.id}`)}
                    className="p-3 rounded-xl border border-slate-100 hover:border-brand-200 hover:bg-slate-50 cursor-pointer transition-all flex items-center justify-between"
                  >
                    <div>
                      <p className="text-xs font-bold text-slate-800 line-clamp-1">{m.title}</p>
                      <p className="text-[10px] text-slate-400 mt-0.5">
                        Status: <span className="font-semibold text-slate-600">{m.status}</span>
                      </p>
                    </div>
                    {m.overallScore !== undefined && m.overallScore !== null && (
                      <span className="badge bg-brand-50 text-brand-700 text-[10px] font-bold">
                        {m.overallScore}/100
                      </span>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
