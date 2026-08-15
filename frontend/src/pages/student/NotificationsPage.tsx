import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { notificationsApi, type StudentNotification } from '../../api/notifications';
import {
  Bell, CheckCheck, ArrowRight,
  Briefcase, Calendar, FileCheck, Brain, AlertCircle, Sparkles
} from 'lucide-react';

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState<StudentNotification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadNotifications() {
      try {
        setLoading(true);
        const data = await notificationsApi.list(false);
        setNotifications(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load notifications');
      } finally {
        setLoading(false);
      }
    }
    loadNotifications();
  }, []);

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationsApi.markAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      );
    } catch (e) {
      console.error('Failed to mark read', e);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationsApi.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
    } catch (e) {
      console.error('Failed to mark all read', e);
    }
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'JOB_MATCH':
        return <Briefcase className="h-4 w-4 text-amber-600" />;
      case 'DAILY_PLAN_REMINDER':
        return <Calendar className="h-4 w-4 text-indigo-600" />;
      case 'PLACEMENT_PAPER_ALERT':
        return <FileCheck className="h-4 w-4 text-emerald-600" />;
      case 'WEAK_TOPIC_WARNING':
        return <AlertCircle className="h-4 w-4 text-rose-600" />;
      case 'INTERVIEW_RECOMMENDATION':
        return <Brain className="h-4 w-4 text-purple-600" />;
      default:
        return <Sparkles className="h-4 w-4 text-slate-600" />;
    }
  };

  return (
    <div className="space-y-8 max-w-4xl mx-auto pb-16">
      {/* Header */}
      <div className="rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-8 text-white shadow-xl">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1.5">
            <div className="inline-flex items-center gap-2 rounded-full bg-indigo-500/20 px-3.5 py-1 text-xs font-bold text-indigo-300 border border-indigo-400/30">
              <Bell className="h-3.5 w-3.5" /> In-App Activity Center
            </div>
            <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight">
              Placement Alerts & Study Reminders
            </h1>
          </div>

          <button
            type="button"
            onClick={handleMarkAllRead}
            className="inline-flex items-center gap-1.5 rounded-xl bg-white/10 backdrop-blur-md px-4 py-2 text-xs font-bold text-white hover:bg-white/20 border border-white/10 transition-colors whitespace-nowrap"
          >
            <CheckCheck className="h-4 w-4" /> Mark All as Read
          </button>
        </div>
      </div>

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-xs text-red-700 flex items-center gap-2">
          <AlertCircle className="h-4 w-4" /> {error}
        </div>
      )}

      {/* Notification Stream */}
      {loading ? (
        <div className="flex items-center justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        </div>
      ) : notifications.length === 0 ? (
        <div className="rounded-2xl border border-slate-200 bg-white p-12 text-center text-slate-500 space-y-2">
          <Bell className="h-10 w-10 mx-auto text-slate-300" />
          <p className="text-sm font-bold text-slate-700">No new notifications</p>
          <p className="text-xs text-slate-500">You're all caught up with your daily tasks and job alerts.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {notifications.map((n) => (
            <div
              key={n.id}
              onClick={() => !n.isRead && handleMarkAsRead(n.id)}
              className={`flex flex-col sm:flex-row sm:items-center justify-between gap-4 rounded-2xl border p-5 transition-all ${
                n.isRead
                  ? 'border-slate-200 bg-white opacity-85 text-slate-700'
                  : 'border-indigo-300 bg-indigo-50/40 shadow-xs text-slate-900 ring-1 ring-indigo-500/20'
              }`}
            >
              <div className="flex items-start gap-3.5">
                <span className="p-2 rounded-xl bg-white shadow-2xs mt-0.5 border border-slate-200">
                  {getNotificationIcon(n.notificationType)}
                </span>
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-bold">{n.title}</span>
                    {!n.isRead && (
                      <span className="h-2 w-2 rounded-full bg-indigo-600"></span>
                    )}
                  </div>
                  <p className="text-xs text-slate-600 leading-relaxed max-w-xl">{n.message}</p>
                  <p className="text-[10px] text-slate-400 font-mono">
                    {new Date(n.createdAt).toLocaleString()}
                  </p>
                </div>
              </div>

              {n.actionRoute && (
                <div className="pl-11 sm:pl-0 flex items-center">
                  <Link
                    to={n.actionRoute}
                    className="inline-flex items-center gap-1.5 rounded-xl bg-indigo-600 px-4 py-2 text-xs font-bold text-white hover:bg-indigo-500 shadow-2xs transition-colors"
                  >
                    View <ArrowRight className="h-3.5 w-3.5" />
                  </Link>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
