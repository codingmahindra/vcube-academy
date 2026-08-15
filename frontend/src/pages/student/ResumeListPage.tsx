import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { resumeApi } from '../../api/resume';
import toast from 'react-hot-toast';
import {
  FileText, Plus, Sparkles, Download, Eye, Edit3, Trash2,
  Calendar, CheckCircle2, ChevronRight, Layers, Star
} from 'lucide-react';

export function ResumeListPage() {
  const queryClient = useQueryClient();

  const { data: versions, isLoading } = useQuery({
    queryKey: ['student-resume-versions'],
    queryFn: resumeApi.listVersions,
  });

  const deleteMutation = useMutation({
    mutationFn: resumeApi.deleteVersion,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['student-resume-versions'] });
      toast.success('Resume version deleted');
    },
    onError: () => toast.error('Failed to delete resume'),
  });

  return (
    <div className="space-y-6 animate-fade-in pb-16">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2.5">
            <Layers className="h-7 w-7 text-brand-600" />
            My Resume Versions
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Tailor multiple versions for specific companies (TCS, Infosys, Amazon) with verified ATS scores.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Link to="/student/resume/analyzer" className="btn-secondary text-xs flex items-center gap-1.5">
            <Sparkles className="h-4 w-4" />
            ATS Analyzer
          </Link>
          <Link to="/student/resume/builder" className="btn-primary text-xs flex items-center gap-1.5">
            <Plus className="h-4 w-4" />
            Create New Version
          </Link>
        </div>
      </div>

      {/* Resumes Grid */}
      {isLoading ? (
        <div className="card p-12 text-center text-slate-400">Loading your resumes...</div>
      ) : versions && versions.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {versions.map((v) => (
            <div
              key={v.id}
              className="card p-6 flex flex-col justify-between hover:shadow-md transition-shadow border-slate-200"
            >
              <div className="space-y-3">
                <div className="flex items-start justify-between">
                  <div>
                    <span className="badge bg-brand-50 text-brand-700 font-semibold text-[10px]">
                      {v.template.replace('_', ' ')}
                    </span>
                    {v.isPrimary && (
                      <span className="badge bg-emerald-50 text-emerald-700 ml-1.5 text-[10px] flex items-center gap-1">
                        <Star className="h-2.5 w-2.5 fill-emerald-600" /> Primary
                      </span>
                    )}
                  </div>
                  <div className="text-right">
                    <span className="text-2xl font-black text-slate-900">
                      {v.latestAtsScore > 0 ? v.latestAtsScore : '—'}
                    </span>
                    <span className="text-[10px] text-slate-400 block font-semibold">ATS SCORE</span>
                  </div>
                </div>

                <div>
                  <h3 className="text-base font-bold text-slate-900 line-clamp-1">{v.versionTitle}</h3>
                  <p className="text-xs text-slate-500 mt-0.5">
                    {v.targetRole || 'Java Developer'} {v.targetCompany ? `• ${v.targetCompany}` : ''}
                  </p>
                </div>
              </div>

              <div className="pt-4 mt-4 border-t border-slate-100 flex items-center justify-between">
                <div className="flex items-center gap-1">
                  <Link
                    to={`/student/resume/preview/${v.id}`}
                    className="p-2 rounded-lg text-slate-600 hover:bg-slate-100 transition-colors"
                    title="Preview ATS Resume"
                  >
                    <Eye className="h-4 w-4" />
                  </Link>
                  <Link
                    to={`/student/resume/builder?id=${v.id}`}
                    className="p-2 rounded-lg text-slate-600 hover:bg-slate-100 transition-colors"
                    title="Edit Resume"
                  >
                    <Edit3 className="h-4 w-4" />
                  </Link>
                  <a
                    href={resumeApi.getPdfUrl(v.id)}
                    target="_blank"
                    rel="noreferrer"
                    className="p-2 rounded-lg text-brand-600 hover:bg-brand-50 transition-colors"
                    title="Download A4 PDF"
                  >
                    <Download className="h-4 w-4" />
                  </a>
                  <button
                    onClick={() => {
                      if (confirm(`Delete version "${v.versionTitle}"?`)) {
                        deleteMutation.mutate(v.id);
                      }
                    }}
                    className="p-2 rounded-lg text-red-500 hover:bg-red-50 transition-colors"
                    title="Delete Version"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>

                <Link
                  to={`/student/resume/preview/${v.id}`}
                  className="text-xs font-bold text-brand-600 hover:text-brand-700 flex items-center gap-1"
                >
                  View Details <ChevronRight className="h-3.5 w-3.5" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card p-12 text-center space-y-4">
          <FileText className="h-12 w-12 text-slate-300 mx-auto" />
          <h3 className="text-base font-bold text-slate-800">No Resume Versions Created Yet</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            Use the ATS Resume Builder to create your first version or analyze an existing resume against Job Descriptions.
          </p>
          <Link to="/student/resume/builder" className="btn-primary text-xs inline-flex items-center gap-1.5">
            <Plus className="h-4 w-4" /> Create First Resume
          </Link>
        </div>
      )}
    </div>
  );
}
