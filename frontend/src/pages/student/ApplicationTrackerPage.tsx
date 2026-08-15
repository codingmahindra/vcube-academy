import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Briefcase,
  CheckCircle2,
  Clock,
  Calendar,
  AlertCircle,
  TrendingUp,
  FileText,
  ChevronRight,
  MoreVertical,
  Edit2,
  Trash2,
  ExternalLink,
  Plus,
} from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type {
  JobApplicationDto,
  ApplicationDashboardDto,
  ApplicationStatus,
} from '../../api/jobs';

export const ApplicationTrackerPage: React.FC = () => {
  const [dashboard, setDashboard] = useState<ApplicationDashboardDto | null>(null);
  const [applications, setApplications] = useState<JobApplicationDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  // Edit / Status change modal
  const [editingApp, setEditingApp] = useState<JobApplicationDto | null>(null);
  const [newStatus, setNewStatus] = useState<ApplicationStatus>('APPLIED');
  const [newNotes, setNewNotes] = useState<string>('');
  const [newNextAction, setNewNextAction] = useState<string>('');
  const [newInterviewDate, setNewInterviewDate] = useState<string>('');
  const [saving, setSaving] = useState<boolean>(false);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [dashRes, appsRes] = await Promise.all([
        jobsApi.getApplicationDashboard(),
        jobsApi.getApplications(0, 50),
      ]);
      setDashboard(dashRes);
      setApplications(appsRes.content);
    } catch (err) {
      console.error('Failed to load application tracker data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenEdit = (app: JobApplicationDto) => {
    setEditingApp(app);
    setNewStatus(app.status);
    setNewNotes(app.notes || '');
    setNewNextAction(app.nextAction || '');
    setNewInterviewDate(app.interviewDate ? app.interviewDate.substring(0, 16) : '');
  };

  const handleSaveUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingApp) return;
    try {
      setSaving(true);
      const updated = await jobsApi.updateApplication(editingApp.id, {
        status: newStatus,
        notes: newNotes,
        nextAction: newNextAction,
        interviewDate: newInterviewDate ? new Date(newInterviewDate).toISOString() : undefined,
      });

      setApplications((prev) => prev.map((a) => (a.id === updated.id ? updated : a)));
      setEditingApp(null);
      // Reload counters
      const dash = await jobsApi.getApplicationDashboard();
      setDashboard(dash);
    } catch (err) {
      console.error('Failed to update application:', err);
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteApp = async (id: number) => {
    if (!confirm('Are you sure you want to remove this application from your tracker?')) return;
    try {
      await jobsApi.deleteApplication(id);
      setApplications((prev) => prev.filter((a) => a.id !== id));
      const dash = await jobsApi.getApplicationDashboard();
      setDashboard(dash);
    } catch (err) {
      console.error('Failed to delete application:', err);
    }
  };

  const filteredApps = applications.filter((app) => {
    if (statusFilter === 'ALL') return true;
    return app.status === statusFilter;
  });

  const getStatusBadgeClass = (status: ApplicationStatus) => {
    switch (status) {
      case 'OFFER':
        return 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30';
      case 'INTERVIEW':
        return 'bg-purple-500/15 text-purple-400 border-purple-500/30';
      case 'ASSESSMENT':
        return 'bg-blue-500/15 text-blue-400 border-blue-500/30';
      case 'APPLIED':
        return 'bg-amber-500/15 text-amber-400 border-amber-500/30';
      case 'REJECTED':
        return 'bg-rose-500/15 text-rose-400 border-rose-500/30';
      default:
        return 'bg-gray-800 text-gray-400 border-gray-700';
    }
  };

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <TrendingUp className="w-6 h-6 text-blue-400" />
            Job Application Tracker
          </h1>
          <p className="text-sm text-gray-400 mt-1">
            Track your recruitment stages, online assessments, interviews, and offer letters in one unified pipeline.
          </p>
        </div>
        <Link to="/student/jobs" className="btn btn-primary text-sm inline-flex items-center gap-2">
          <Plus className="w-4 h-4" /> Add Application
        </Link>
      </div>

      {/* Metrics Cards */}
      {dashboard && (
        <div className="grid grid-cols-2 md:grid-cols-6 gap-4">
          <div className="card p-4 border-gray-800 bg-gray-900/60 space-y-1">
            <span className="text-xs text-gray-400">Total Applied</span>
            <p className="text-2xl font-bold text-white">{dashboard.totalApplications}</p>
          </div>
          <div className="card p-4 border-gray-800 bg-gray-900/60 space-y-1">
            <span className="text-xs text-amber-400">Applied</span>
            <p className="text-2xl font-bold text-amber-400">{dashboard.appliedCount}</p>
          </div>
          <div className="card p-4 border-gray-800 bg-gray-900/60 space-y-1">
            <span className="text-xs text-blue-400">Assessment</span>
            <p className="text-2xl font-bold text-blue-400">{dashboard.assessmentCount}</p>
          </div>
          <div className="card p-4 border-gray-800 bg-gray-900/60 space-y-1">
            <span className="text-xs text-purple-400">Interview</span>
            <p className="text-2xl font-bold text-purple-400">{dashboard.interviewCount}</p>
          </div>
          <div className="card p-4 border-gray-800 bg-gray-900/60 space-y-1">
            <span className="text-xs text-emerald-400">Offers</span>
            <p className="text-2xl font-bold text-emerald-400">{dashboard.offerCount}</p>
          </div>
          <div className="card p-4 border-gray-800 bg-gray-900/60 space-y-1">
            <span className="text-xs text-rose-400">Rejected</span>
            <p className="text-2xl font-bold text-rose-400">{dashboard.rejectedCount}</p>
          </div>
        </div>
      )}

      {/* Upcoming Interviews Alert */}
      {dashboard && dashboard.upcomingInterviews.length > 0 && (
        <div className="card p-6 border-purple-500/30 bg-purple-950/20 space-y-3">
          <h3 className="text-sm font-bold text-purple-300 uppercase tracking-wider flex items-center gap-2">
            <Calendar className="w-4 h-4 text-purple-400" />
            Upcoming Interview Schedule
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {dashboard.upcomingInterviews.map((app) => (
              <div key={app.id} className="p-4 rounded-xl bg-gray-900/80 border border-purple-500/20 flex items-center justify-between">
                <div>
                  <h4 className="text-sm font-bold text-white">{app.job.companyName}</h4>
                  <p className="text-xs text-gray-400">{app.job.title}</p>
                  <span className="text-xs text-purple-400 mt-1 inline-block font-semibold">
                    Scheduled: {app.interviewDate ? new Date(app.interviewDate).toLocaleString() : 'TBD'}
                  </span>
                </div>
                <Link
                  to="/student/interview/mock/setup"
                  className="btn btn-secondary text-xs px-3 py-1.5 text-purple-300 border-purple-500/30"
                >
                  Mock Practice
                </Link>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Filter Tabs */}
      <div className="flex items-center gap-2 overflow-x-auto pb-2 border-b border-gray-800">
        {['ALL', 'APPLIED', 'ASSESSMENT', 'INTERVIEW', 'OFFER', 'REJECTED'].map((st) => (
          <button
            key={st}
            onClick={() => setStatusFilter(st)}
            className={`px-4 py-2 rounded-lg text-xs font-semibold whitespace-nowrap transition-all ${
              statusFilter === st
                ? 'bg-blue-600 text-white'
                : 'text-gray-400 hover:text-white hover:bg-gray-800'
            }`}
          >
            {st}
          </button>
        ))}
      </div>

      {/* Applications List */}
      {loading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((n) => (
            <div key={n} className="card p-6 border-gray-800 animate-pulse h-24 bg-gray-900/60"></div>
          ))}
        </div>
      ) : filteredApps.length === 0 ? (
        <div className="card p-12 text-center border-gray-800 space-y-3">
          <Briefcase className="w-12 h-12 text-gray-600 mx-auto" />
          <h3 className="text-lg font-medium text-white">No applications in this stage</h3>
          <p className="text-sm text-gray-400 max-w-sm mx-auto">
            Apply to open positions or log off-campus applications to keep your career dashboard up to date.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredApps.map((app) => (
            <div
              key={app.id}
              className="card p-6 border-gray-800 hover:border-gray-700 bg-gray-900/60 flex flex-col md:flex-row md:items-center justify-between gap-6 transition-all"
            >
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-xl bg-gray-800 border border-gray-700 flex items-center justify-center font-bold text-blue-400 text-xl">
                  {app.job.companyName.charAt(0)}
                </div>
                <div className="space-y-1.5">
                  <div className="flex items-center gap-3">
                    <h3 className="text-base font-bold text-white hover:text-blue-400 transition-colors">
                      <Link to={`/student/applications/${app.id}`}>{app.job.title}</Link>
                    </h3>
                    <span className={`text-xs px-2.5 py-0.5 rounded-full font-semibold border ${getStatusBadgeClass(app.status)}`}>
                      {app.status}
                    </span>
                  </div>
                  <div className="flex flex-wrap items-center gap-3 text-xs text-gray-400">
                    <span className="text-gray-300 font-medium">{app.job.companyName}</span>
                    <span>•</span>
                    <span>Applied: {new Date(app.appliedDate).toLocaleDateString()}</span>
                    {app.interviewDate && (
                      <>
                        <span>•</span>
                        <span className="text-purple-400 font-medium">
                          Interview: {new Date(app.interviewDate).toLocaleDateString()}
                        </span>
                      </>
                    )}
                  </div>
                  {app.notes && (
                    <p className="text-xs text-gray-300 bg-gray-800/40 p-2 rounded-lg border border-gray-800">
                      <strong>Notes:</strong> {app.notes}
                    </p>
                  )}
                </div>
              </div>

              <div className="flex items-center gap-2 self-end md:self-center">
                <button
                  onClick={() => handleOpenEdit(app)}
                  className="btn btn-secondary text-xs px-3 py-2 flex items-center gap-1.5"
                >
                  <Edit2 className="w-3.5 h-3.5" />
                  Update Status
                </button>
                <Link
                  to={`/student/applications/${app.id}`}
                  className="btn btn-secondary text-xs px-3 py-2 flex items-center gap-1.5 text-blue-400 hover:text-blue-300"
                >
                  <Clock className="w-3.5 h-3.5" />
                  Timeline
                </Link>
                <button
                  onClick={() => handleDeleteApp(app.id)}
                  className="btn btn-secondary text-xs px-3 py-2 text-rose-400 hover:bg-rose-500/10 border-rose-500/30"
                  title="Remove"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Edit / Update Status Modal */}
      {editingApp && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fade-in">
          <div className="card p-6 border-gray-700 bg-gray-900 max-w-lg w-full space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-white">Update Recruitment Stage</h3>
              <button onClick={() => setEditingApp(null)} className="text-gray-400 hover:text-white text-sm">
                ✕
              </button>
            </div>

            <form onSubmit={handleSaveUpdate} className="space-y-4">
              <div>
                <label className="text-xs font-semibold text-gray-300 block mb-1.5">New Stage Status</label>
                <select
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value as ApplicationStatus)}
                  className="input w-full bg-gray-800 border-gray-700 text-sm text-white"
                >
                  <option value="APPLIED">Applied</option>
                  <option value="ASSESSMENT">Online Assessment Scheduled/Cleared</option>
                  <option value="INTERVIEW">Interview Round (Tech / HR)</option>
                  <option value="OFFER">Offer Letter Received 🎉</option>
                  <option value="REJECTED">Rejected</option>
                  <option value="WITHDRAWN">Withdrawn</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-gray-300 block mb-1.5">Interview / Assessment Date (Optional)</label>
                <input
                  type="datetime-local"
                  value={newInterviewDate}
                  onChange={(e) => setNewInterviewDate(e.target.value)}
                  className="input w-full bg-gray-800 border-gray-700 text-sm text-white"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-gray-300 block mb-1.5">Stage Notes & Action Items</label>
                <textarea
                  rows={3}
                  placeholder="e.g. Cleared 1st coding round. Next is Java Live System Design on Monday."
                  value={newNotes}
                  onChange={(e) => setNewNotes(e.target.value)}
                  className="input w-full bg-gray-800 border-gray-700 text-sm text-white"
                ></textarea>
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setEditingApp(null)}
                  className="btn btn-secondary text-xs px-4"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="btn btn-primary text-xs px-5 flex items-center gap-1.5"
                >
                  {saving ? 'Updating...' : 'Save Stage Update'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
export default ApplicationTrackerPage;
