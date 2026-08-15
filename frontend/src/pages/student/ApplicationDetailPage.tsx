import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  Briefcase,
  Calendar,
  Clock,
  ArrowLeft,
  CheckCircle2,
  AlertCircle,
  Building,
  MapPin,
  FileText,
  MessageSquare,
  Sparkles,
} from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type { JobApplicationDto } from '../../api/jobs';

export const ApplicationDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [app, setApp] = useState<JobApplicationDto | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (id) {
      loadApplicationDetail(Number(id));
    }
  }, [id]);

  const loadApplicationDetail = async (appId: number) => {
    try {
      setLoading(true);
      const res = await jobsApi.getApplicationDetail(appId);
      setApp(res);
    } catch (err) {
      console.error('Failed to load application detail:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="card p-12 border-gray-800 animate-pulse space-y-4">
        <div className="h-8 bg-gray-800 rounded w-1/3"></div>
        <div className="h-32 bg-gray-800 rounded"></div>
      </div>
    );
  }

  if (!app) {
    return (
      <div className="card p-12 text-center border-gray-800 space-y-3">
        <AlertCircle className="w-12 h-12 text-rose-500 mx-auto" />
        <h2 className="text-xl font-bold text-white">Application Not Found</h2>
        <Link to="/student/applications" className="btn btn-secondary text-sm inline-flex items-center gap-2">
          <ArrowLeft className="w-4 h-4" /> Back to Tracker
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-fade-in">
      <div className="flex items-center justify-between">
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-2 text-sm text-gray-400 hover:text-white transition-colors"
        >
          <ArrowLeft className="w-4 h-4" /> Back to Applications
        </button>

        <Link
          to={`/student/jobs/${app.job.id}`}
          className="btn btn-secondary text-xs inline-flex items-center gap-2"
        >
          <Briefcase className="w-3.5 h-3.5" /> View Original Job Spec
        </Link>
      </div>

      {/* Header Info */}
      <div className="card p-6 border-gray-800 bg-gray-900/60 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div className="flex items-start gap-4">
          <div className="w-14 h-14 rounded-2xl bg-gray-800 border border-gray-700 flex items-center justify-center font-bold text-blue-400 text-2xl">
            {app.job.companyName.charAt(0)}
          </div>
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-xl font-bold text-white">{app.job.title}</h1>
              <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-500/10 text-blue-400 border border-blue-500/20">
                {app.status}
              </span>
            </div>
            <h2 className="text-sm text-gray-300 font-medium mt-1">{app.job.companyName}</h2>
            <p className="text-xs text-gray-400 mt-2">
              Applied on: {new Date(app.appliedDate).toLocaleDateString()} • Location: {app.job.location}
            </p>
          </div>
        </div>

        {app.interviewDate && (
          <div className="p-4 rounded-xl bg-purple-950/30 border border-purple-500/30 text-xs text-purple-300 space-y-1">
            <span className="font-semibold flex items-center gap-1.5">
              <Calendar className="w-3.5 h-3.5 text-purple-400" /> Upcoming Interview:
            </span>
            <p className="text-white font-bold text-sm">
              {new Date(app.interviewDate).toLocaleString()}
            </p>
          </div>
        )}
      </div>

      {/* Status History Timeline */}
      <div className="card p-6 border-gray-800 space-y-6">
        <h3 className="text-base font-bold text-white flex items-center gap-2">
          <Clock className="w-4 h-4 text-blue-400" />
          Recruitment Progression Timeline
        </h3>

        <div className="relative pl-6 space-y-6 before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-gray-800">
          {app.statusHistories.map((h, index) => (
            <div key={h.id} className="relative space-y-1.5">
              <div className="absolute -left-6 top-1 w-3.5 h-3.5 rounded-full bg-blue-500 border-2 border-gray-900"></div>
              <div className="flex items-center gap-3">
                <span className="text-xs font-bold text-white uppercase px-2 py-0.5 rounded bg-gray-800 border border-gray-700">
                  {h.newStatus}
                </span>
                <span className="text-xs text-gray-400">
                  {new Date(h.changedAt).toLocaleString()}
                </span>
              </div>
              {h.previousStatus && (
                <p className="text-xs text-gray-500">
                  Transitioned from <span className="font-semibold text-gray-400">{h.previousStatus}</span>
                </p>
              )}
              {h.notes && (
                <p className="text-xs text-gray-300 bg-gray-900/80 p-3 rounded-lg border border-gray-800 max-w-xl">
                  {h.notes}
                </p>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
export default ApplicationDetailPage;
