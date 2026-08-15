import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { dsaApi, type DsaSubmissionResponse } from '../../../api/dsa';
import {
  Code2, CheckCircle2, XCircle, Clock, AlertTriangle,
  ChevronLeft, ChevronRight, Eye, X, Loader2
} from 'lucide-react';
import toast from 'react-hot-toast';

export function DsaSubmissionsPage() {
  const [submissions, setSubmissions] = useState<DsaSubmissionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const [viewingCode, setViewingCode] = useState<DsaSubmissionResponse | null>(null);

  useEffect(() => {
    async function loadSubmissions() {
      try {
        setLoading(true);
        const res = await dsaApi.getUserSubmissions(page, 15);
        setSubmissions(res.content);
        setTotalPages(res.totalPages);
        setTotalElements(res.totalElements);
      } catch (err) {
        toast.error('Failed to load submissions');
      } finally {
        setLoading(false);
      }
    }
    loadSubmissions();
  }, [page]);

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-800">My DSA Submissions</h1>
          <p className="text-xs text-slate-500 mt-0.5">{totalElements} total code submission{totalElements === 1 ? '' : 's'}</p>
        </div>
        <Link
          to="/student/dsa/problems"
          className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-brand-600 text-white text-xs font-semibold hover:bg-brand-500 shadow-sm"
        >
          <Code2 className="h-4 w-4" /> Practice Problems
        </Link>
      </div>

      {/* Submissions Table */}
      <div className="rounded-2xl bg-white border border-slate-100 shadow-sm overflow-hidden">
        {loading ? (
          <div className="flex h-64 items-center justify-center">
            <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
          </div>
        ) : submissions.length === 0 ? (
          <div className="p-12 text-center space-y-3">
            <Clock className="h-10 w-10 text-slate-300 mx-auto" />
            <p className="text-sm font-semibold text-slate-700">No submissions yet</p>
            <p className="text-xs text-slate-400">Solve coding challenges to build your DSA track record!</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 border-b border-slate-100 text-slate-500 font-semibold uppercase tracking-wider text-[10px]">
                <tr>
                  <th className="px-5 py-3">Problem</th>
                  <th className="px-5 py-3">Status</th>
                  <th className="px-5 py-3">Test Cases</th>
                  <th className="px-5 py-3">Runtime</th>
                  <th className="px-5 py-3">Submitted At</th>
                  <th className="px-5 py-3 text-right">Source</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {submissions.map((sub) => (
                  <tr key={sub.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-5 py-3.5">
                      <Link
                        to={`/student/dsa/problems/${sub.problemId}`}
                        className="font-bold text-slate-800 hover:text-brand-600 transition-colors"
                      >
                        {sub.problemTitle}
                      </Link>
                      <p className="text-[10px] text-slate-400 font-mono mt-0.5">{sub.language}</p>
                    </td>
                    <td className="px-5 py-3.5">
                      <span
                        className={`badge text-[10px] font-bold ${
                          sub.status === 'ACCEPTED'
                            ? 'bg-emerald-100 text-emerald-800'
                            : 'bg-red-100 text-red-800'
                        }`}
                      >
                        {sub.status.replace('_', ' ')}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 font-medium text-slate-700">
                      {sub.passedTestCases} / {sub.totalTestCases}
                    </td>
                    <td className="px-5 py-3.5 text-slate-600 font-mono text-[11px]">
                      {sub.executionTimeMs !== undefined ? `${sub.executionTimeMs} ms` : '-'}
                    </td>
                    <td className="px-5 py-3.5 text-slate-400 text-[11px]">
                      {new Date(sub.submittedAt).toLocaleString()}
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      <button
                        onClick={() => setViewingCode(sub)}
                        className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-50 text-[11px] font-semibold"
                      >
                        <Eye className="h-3 w-3" /> View Code
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-5 py-3 border-t border-slate-100 text-xs text-slate-500">
            <span>
              Page {page + 1} of {totalPages}
            </span>
            <div className="flex items-center gap-1">
              <button
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                className="p-1.5 rounded-lg border border-slate-200 disabled:opacity-40 hover:bg-slate-50"
              >
                <ChevronLeft className="h-4 w-4" />
              </button>
              <button
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
                className="p-1.5 rounded-lg border border-slate-200 disabled:opacity-40 hover:bg-slate-50"
              >
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* View Code Modal */}
      {viewingCode && (
        <div className="fixed inset-0 z-50 bg-black/60 flex items-center justify-center p-4 backdrop-blur-xs">
          <div className="bg-white rounded-2xl max-w-2xl w-full border border-slate-100 shadow-2xl overflow-hidden flex flex-col max-h-[85vh]">
            <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100 bg-slate-50">
              <div>
                <h3 className="text-sm font-bold text-slate-800">{viewingCode.problemTitle}</h3>
                <p className="text-xs text-slate-500">Status: {viewingCode.status} • {viewingCode.language}</p>
              </div>
              <button
                onClick={() => setViewingCode(null)}
                className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-200/60"
              >
                <X className="h-4 w-4" />
              </button>
            </div>
            <div className="p-4 flex-1 overflow-y-auto bg-slate-950">
              <pre className="text-xs font-mono text-emerald-300 leading-relaxed overflow-x-auto">
                {viewingCode.sourceCode}
              </pre>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
