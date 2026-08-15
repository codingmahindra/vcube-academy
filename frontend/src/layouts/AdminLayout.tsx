import React, { useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import toast from 'react-hot-toast';
import {
  GraduationCap, LayoutDashboard, User, LogOut, Menu,
  Users, BookOpen, HelpCircle, Briefcase, Shield,
} from 'lucide-react';

const adminNav = [
  { to: '/admin/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/profile',         label: 'Profile',   icon: User },
];

export function AdminLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  async function handleLogout() {
    await logout();
    toast.success('Logged out successfully');
    navigate('/login');
  }

  return (
    <div className="flex h-screen overflow-hidden bg-slate-50">
      {sidebarOpen && (
        <div className="fixed inset-0 z-20 bg-black/40 lg:hidden" onClick={() => setSidebarOpen(false)} />
      )}

      <aside className={`fixed inset-y-0 left-0 z-30 flex w-64 flex-col bg-white border-r border-slate-100
          shadow-sm transition-transform duration-200 lg:relative lg:translate-x-0
          ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="flex h-16 items-center gap-2.5 border-b border-slate-100 px-4">
          <Shield className="h-7 w-7 text-brand-600 flex-shrink-0" />
          <div className="min-w-0">
            <p className="text-xs font-bold text-brand-700 leading-tight truncate">VCUBE Academy</p>
            <p className="text-[10px] text-slate-400 truncate">Admin Portal</p>
          </div>
        </div>

        <nav className="flex-1 overflow-y-auto p-3 space-y-1">
          {adminNav.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
              onClick={() => setSidebarOpen(false)}
            >
              <Icon className="h-4 w-4 flex-shrink-0" />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="border-t border-slate-100 p-3">
          <div className="flex items-center gap-3 rounded-xl p-2">
            <div className="h-8 w-8 rounded-full bg-violet-100 flex items-center justify-center flex-shrink-0">
              <span className="text-xs font-bold text-violet-700">
                {user?.fullName?.[0]?.toUpperCase() ?? 'A'}
              </span>
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-xs font-semibold text-slate-800 truncate">{user?.fullName}</p>
              <p className="text-[10px] text-slate-400 truncate">{user?.email}</p>
            </div>
          </div>
          <button onClick={handleLogout} className="mt-2 w-full sidebar-link text-red-500 hover:bg-red-50 hover:text-red-600">
            <LogOut className="h-4 w-4" /> Logout
          </button>
        </div>
      </aside>

      <div className="flex flex-1 flex-col overflow-hidden">
        <header className="flex h-16 items-center justify-between border-b border-slate-100 bg-white px-4 sm:px-6">
          <button className="lg:hidden p-2 rounded-lg hover:bg-slate-100" onClick={() => setSidebarOpen(true)}>
            <Menu className="h-5 w-5 text-slate-600" />
          </button>
          <div className="hidden lg:block" />
          <div className="flex items-center gap-2">
            <span className="text-xs font-semibold text-slate-700">{user?.fullName}</span>
            <span className="badge bg-violet-100 text-violet-700 ml-1">Admin</span>
          </div>
        </header>
        <main className="flex-1 overflow-y-auto p-4 sm:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
