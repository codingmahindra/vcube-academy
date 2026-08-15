import { useState, useEffect } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import toast from 'react-hot-toast';
import { notificationsApi } from '../api/notifications';
import {
  GraduationCap, LayoutDashboard, User, LogOut, Menu,
  BookOpen, Code2, HelpCircle, FileText, Briefcase,
  Bookmark, TrendingUp, Building, Sliders, Sparkles, Layers,
  Bot, MapPin, Calendar, FileCheck, Compass, Search, Bell
} from 'lucide-react';

const studentNav = [
  { to: '/student/dashboard', label: 'Master Dashboard', icon: LayoutDashboard },
  { to: '/student/search', label: 'Global Search', icon: Search },
  { to: '/student/bookmarks', label: 'Saved Bookmarks', icon: Bookmark },
  { to: '/student/notifications', label: 'Notifications', icon: Bell },
  { to: '/student/career', label: 'Career Hub', icon: Compass },
  { to: '/student/career/copilot', label: 'AI Career Copilot', icon: Bot },
  { to: '/student/career/roadmap', label: 'Career Roadmap', icon: MapPin },
  { to: '/student/career/daily-plan', label: 'Daily Preparation', icon: Calendar },
  { to: '/student/placement-papers', label: 'Placement Papers', icon: FileCheck },
  { to: '/student/interview/mock', label: 'Mock Interview', icon: GraduationCap },
  { to: '/student/interview/companies', label: 'Company Preparation', icon: Building },
  { to: '/student/courses', label: 'Courses & Modules', icon: BookOpen },
  { to: '/student/dsa', label: 'DSA Practice', icon: Code2 },
  { to: '/student/interview', label: 'Interview Prep', icon: HelpCircle },
  { to: '/student/jobs', label: 'Job Portal', icon: Briefcase },
  { to: '/student/applications', label: 'Application Tracker', icon: TrendingUp },
  { to: '/student/placements', label: 'Placement Drives', icon: Building },
  { to: '/student/job-preferences', label: 'Career Preferences', icon: Sliders },
  { to: '/student/resume/analyzer', label: 'Resume Analyzer', icon: Sparkles },
  { to: '/student/resume/builder', label: 'Resume Builder', icon: FileText },
  { to: '/student/resume/resumes', label: 'My Resumes', icon: Layers },
  { to: '/student/profile', label: 'My Profile', icon: User },
];

export function StudentLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    async function fetchUnread() {
      try {
        const count = await notificationsApi.getUnreadCount();
        setUnreadCount(count);
      } catch (e) {
        // ignore if guest or network error
      }
    }
    fetchUnread();
    const interval = setInterval(fetchUnread, 30000);
    return () => clearInterval(interval);
  }, []);

  async function handleLogout() {
    await logout();
    toast.success('Logged out successfully');
    navigate('/login');
  }

  return (
    <div className="flex h-screen overflow-hidden bg-slate-50">
      {/* Mobile overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-20 bg-black/40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-30 flex w-64 flex-col bg-white border-r border-slate-100
          shadow-sm transition-transform duration-200 lg:relative lg:translate-x-0
          ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}
      >
        {/* Logo & VCUBE Branding */}
        <div className="flex h-16 items-center gap-2.5 border-b border-slate-100 px-4">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-indigo-600 text-white shadow-xs flex-shrink-0">
            <GraduationCap className="h-5 w-5" />
          </div>
          <div className="min-w-0">
            <p className="text-xs font-bold text-slate-900 leading-tight truncate">VCUBE ACADEMY</p>
            <p className="text-[10px] font-medium text-indigo-600 truncate">Software Solutions</p>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 overflow-y-auto p-3 space-y-1">
          {studentNav.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `sidebar-link flex items-center justify-between ${isActive ? 'active' : ''}`
              }
              onClick={() => setSidebarOpen(false)}
            >
              <div className="flex items-center gap-2.5">
                <Icon className="h-4 w-4 flex-shrink-0" />
                <span className="truncate">{label}</span>
              </div>
              {label === 'Notifications' && unreadCount > 0 && (
                <span className="flex h-4.5 min-w-[1.125rem] items-center justify-center rounded-full bg-indigo-600 px-1 text-[10px] font-bold text-white">
                  {unreadCount}
                </span>
              )}
            </NavLink>
          ))}
        </nav>

        {/* Mentors & User info footer */}
        <div className="border-t border-slate-100 p-3 space-y-2">
          <div className="rounded-xl bg-slate-50 px-3 py-2 text-[10px] text-slate-500 border border-slate-100">
            <p className="font-semibold text-slate-700">Mentors: Srikanth & Viswanath</p>
            <p className="text-[9px] text-slate-400">VCUBE Placement Wing</p>
          </div>

          <div className="flex items-center gap-2.5 rounded-xl p-1.5">
            <div className="h-8 w-8 rounded-full bg-indigo-100 flex items-center justify-center flex-shrink-0 text-indigo-700 font-bold text-xs">
              {user?.fullName?.[0]?.toUpperCase() ?? 'S'}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-xs font-semibold text-slate-800 truncate">{user?.fullName}</p>
              <p className="text-[10px] text-slate-400 truncate">{user?.email}</p>
            </div>
          </div>

          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 rounded-xl py-2 text-xs font-semibold text-red-500 hover:bg-red-50 hover:text-red-600 transition-colors"
          >
            <LogOut className="h-3.5 w-3.5" /> Sign Out
          </button>
        </div>
      </aside>

      {/* Main Container */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Header */}
        <header className="flex h-16 items-center justify-between border-b border-slate-100 bg-white px-4 sm:px-6">
          <button
            className="lg:hidden p-2 rounded-lg hover:bg-slate-100 text-slate-600"
            onClick={() => setSidebarOpen(true)}
          >
            <Menu className="h-5 w-5" />
          </button>

          <div className="hidden lg:flex items-center gap-3 text-xs text-slate-500">
            <span className="font-bold text-slate-800">VCUBE Software Solutions</span>
            <span>•</span>
            <span>Java Full Stack Career Academy</span>
          </div>

          <div className="flex items-center gap-3">
            <NavLink
              to="/student/search"
              className="p-2 rounded-xl text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition-colors"
              title="Global Search"
            >
              <Search className="h-4 w-4" />
            </NavLink>

            <NavLink
              to="/student/notifications"
              className="relative p-2 rounded-xl text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition-colors"
              title="Notifications"
            >
              <Bell className="h-4 w-4" />
              {unreadCount > 0 && (
                <span className="absolute top-1.5 right-1.5 h-2 w-2 rounded-full bg-indigo-600"></span>
              )}
            </NavLink>

            <div className="h-4 w-px bg-slate-200"></div>

            <div className="flex items-center gap-2">
              <span className="text-xs font-semibold text-slate-700 hidden sm:block">{user?.fullName}</span>
              <span className="rounded-full bg-indigo-50 border border-indigo-200 px-2.5 py-0.5 text-[10px] font-bold text-indigo-700">
                Student
              </span>
            </div>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
