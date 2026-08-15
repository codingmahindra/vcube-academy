import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuth } from '../../hooks/useAuth';
import { adminApi } from '../../api/admin';
import { dsaApi } from '../../api/dsa';
import { jobsApi } from '../../api/jobs';
import toast from 'react-hot-toast';
import {
  Users, UserCheck, BookOpen, Brain, Clock, Shield,
  Search, CheckCircle2, XCircle, ChevronRight, BarChart2,
  TrendingUp, Sparkles, Filter, ToggleLeft, ToggleRight, Loader2, Code2, Briefcase, Building
} from 'lucide-react';

export function AdminDashboard() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState<string>('ALL');

  const { data: dashboard, isLoading: dashLoading } = useQuery({
    queryKey: ['admin-dashboard'],
    queryFn: adminApi.getDashboard,
  });

  const { data: dsaStats } = useQuery({
    queryKey: ['admin-dsa-stats'],
    queryFn: dsaApi.getAdminStats,
  });

  const { data: jobStats } = useQuery({
    queryKey: ['admin-job-stats'],
    queryFn: jobsApi.getJobAnalytics,
  });

  const { data: placementDrives } = useQuery({
    queryKey: ['admin-placement-drives'],
    queryFn: jobsApi.getPlacementDrives,
  });

  const { data: users, isLoading: usersLoading } = useQuery({
    queryKey: ['admin-users'],
    queryFn: adminApi.getUsers,
  });

  const toggleStatusMutation = useMutation({
    mutationFn: (userId: number) => adminApi.toggleUserStatus(userId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      toast.success('User status updated');
    },
    onError: () => toast.error('Failed to update status'),
  });

  const updateRoleMutation = useMutation({
    mutationFn: ({ userId, role }: { userId: number; role: string }) =>
      adminApi.updateUserRole(userId, role),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      queryClient.invalidateQueries({ queryKey: ['admin-dashboard'] });
      toast.success('User role changed');
    },
    onError: () => toast.error('Failed to change role'),
  });

  const filteredUsers = users?.filter((u) => {
    const matchesSearch =
      u.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      u.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesRole =
      roleFilter === 'ALL' || (u.roles && u.roles.includes(roleFilter as any));
    return matchesSearch && matchesRole;
  });

  return (
    <div className="space-y-6 animate-fade-in pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Admin Control Center</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            VCUBE Software Solutions — Academy Oversight & User Management
          </p>
        </div>
        <div className="flex items-center gap-2 text-xs text-slate-400">
          <Clock className="h-3.5 w-3.5" />
          {new Date().toLocaleDateString('en-IN', { weekday: 'long', day: 'numeric', month: 'long' })}
        </div>
      </div>

      {/* Admin banner */}
      <div className="rounded-2xl bg-gradient-to-r from-violet-700 via-purple-700 to-indigo-800 p-6 text-white shadow-lg">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-violet-200">
              <Shield className="h-4 w-4" /> System Administrator
            </div>
            <h2 className="text-xl font-bold mt-1">{user?.fullName}</h2>
            <p className="text-xs text-violet-200 mt-1">
              Full control over curricula, role assignments, student analytics, and platform security.
            </p>
          </div>
          <div className="bg-white/10 backdrop-blur-md rounded-2xl px-5 py-3 border border-white/10 text-center">
            <p className="text-xs text-violet-200">Overall Academy Score</p>
            <p className="text-2xl font-extrabold text-white mt-0.5">
              {dashboard?.averageScorePercentage ?? 0}%
            </p>
          </div>
        </div>
      </div>

      {/* Metrics grid */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
        {[
          { label: 'Students', value: dashboard?.totalStudents ?? 0, icon: Users, color: 'text-brand-600', bg: 'bg-brand-50' },
          { label: 'Trainers', value: dashboard?.totalTrainers ?? 0, icon: UserCheck, color: 'text-emerald-600', bg: 'bg-emerald-50' },
          { label: 'Courses', value: dashboard?.totalCourses ?? 0, icon: BookOpen, color: 'text-violet-600', bg: 'bg-violet-50' },
          { label: 'Topics', value: dashboard?.totalTopics ?? 0, icon: Brain, color: 'text-amber-600', bg: 'bg-amber-50' },
          { label: 'Questions', value: dashboard?.totalQuestions ?? 0, icon: Sparkles, color: 'text-cyan-600', bg: 'bg-cyan-50' },
          { label: 'Quiz Submissions', value: dashboard?.totalQuizAttempts ?? 0, icon: TrendingUp, color: 'text-pink-600', bg: 'bg-pink-50' },
        ].map((m) => {
          const Icon = m.icon;
          return (
            <div key={m.label} className="card p-4 flex flex-col justify-between">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-400">{m.label}</span>
                <div className={`p-2 rounded-xl ${m.bg} ${m.color}`}>
                  <Icon className="h-4 w-4" />
                </div>
              </div>
              <p className="text-xl font-bold text-slate-900 mt-2">{m.value}</p>
            </div>
          );
        })}
      </div>

      {/* DSA Platform Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="card p-4 border-l-4 border-l-brand-600 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">DSA Coding Problems</p>
            <p className="text-xl font-bold text-slate-900 mt-1">{dsaStats?.totalProblems ?? 0}</p>
          </div>
          <div className="p-2.5 rounded-xl bg-brand-50 text-brand-600">
            <Code2 className="h-5 w-5" />
          </div>
        </div>

        <div className="card p-4 border-l-4 border-l-indigo-600 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">Total Submissions</p>
            <p className="text-xl font-bold text-slate-900 mt-1">{dsaStats?.totalSubmissions ?? 0}</p>
          </div>
          <div className="p-2.5 rounded-xl bg-indigo-50 text-indigo-600">
            <TrendingUp className="h-5 w-5" />
          </div>
        </div>

        <div className="card p-4 border-l-4 border-l-emerald-600 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">DSA Solved Records</p>
            <p className="text-xl font-bold text-slate-900 mt-1">{dsaStats?.totalAcceptedSubmissions ?? 0}</p>
          </div>
          <div className="p-2.5 rounded-xl bg-emerald-50 text-emerald-600">
            <CheckCircle2 className="h-5 w-5" />
          </div>
        </div>
      </div>

      {/* Job & Placement Platform Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="card p-4 border-l-4 border-l-blue-600 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">Active Job Openings</p>
            <p className="text-xl font-bold text-slate-900 mt-1">{jobStats?.totalActiveJobs ?? 6}</p>
          </div>
          <div className="p-2.5 rounded-xl bg-blue-50 text-blue-600">
            <Briefcase className="h-5 w-5" />
          </div>
        </div>

        <div className="card p-4 border-l-4 border-l-purple-600 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">Active Placement Drives</p>
            <p className="text-xl font-bold text-slate-900 mt-1">{jobStats?.totalPlacementDrives ?? 3}</p>
          </div>
          <div className="p-2.5 rounded-xl bg-purple-50 text-purple-600">
            <Building className="h-5 w-5" />
          </div>
        </div>

        <div className="card p-4 border-l-4 border-l-amber-600 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">Student Applications Tracked</p>
            <p className="text-xl font-bold text-slate-900 mt-1">{jobStats?.totalApplications ?? 0}</p>
          </div>
          <div className="p-2.5 rounded-xl bg-amber-50 text-amber-600">
            <TrendingUp className="h-5 w-5" />
          </div>
        </div>
      </div>

      {/* User Management Section */}
      <div className="card p-6 space-y-5">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-3">
          <div>
            <h2 className="text-lg font-bold text-slate-900">User Management</h2>
            <p className="text-xs text-slate-500">Manage registered students, trainers, and role authorizations</p>
          </div>

          <div className="flex items-center gap-3 flex-wrap">
            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
              <input
                className="input pl-9 py-2 text-xs w-56"
                placeholder="Search name or email..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            {/* Role Filter */}
            <select
              className="input py-2 text-xs w-36"
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
            >
              <option value="ALL">All Roles</option>
              <option value="STUDENT">Students</option>
              <option value="TRAINER">Trainers</option>
              <option value="ADMIN">Admins</option>
            </select>
          </div>
        </div>

        {usersLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-brand-600" />
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="border-b border-slate-100 text-xs uppercase font-semibold text-slate-400">
                  <th className="py-3 px-2">User</th>
                  <th className="py-3 px-2">Role</th>
                  <th className="py-3 px-2">Change Role</th>
                  <th className="py-3 px-2">Status</th>
                  <th className="py-3 px-2 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredUsers?.map((u) => {
                  const currentRole = u.roles?.[0] ?? 'STUDENT';
                  return (
                    <tr key={u.id} className="hover:bg-slate-50">
                      <td className="py-3 px-2">
                        <div className="flex items-center gap-3">
                          <div className="h-8 w-8 rounded-full bg-slate-100 flex items-center justify-center font-bold text-slate-700 text-xs flex-shrink-0">
                            {u.fullName[0].toUpperCase()}
                          </div>
                          <div>
                            <p className="font-semibold text-slate-900 text-sm">{u.fullName}</p>
                            <p className="text-xs text-slate-400">{u.email}</p>
                          </div>
                        </div>
                      </td>

                      <td className="py-3 px-2">
                        <span className={`badge font-semibold ${
                          currentRole === 'ADMIN' ? 'bg-violet-50 text-violet-700' :
                          currentRole === 'TRAINER' ? 'bg-emerald-50 text-emerald-700' :
                          'bg-brand-50 text-brand-700'
                        }`}>
                          {currentRole}
                        </span>
                      </td>

                      <td className="py-3 px-2">
                        <select
                          className="input py-1 px-2 text-xs w-32 border-slate-200"
                          value={currentRole}
                          onChange={(e) => {
                            if (confirm(`Change role of ${u.fullName} to ${e.target.value}?`)) {
                              updateRoleMutation.mutate({ userId: u.id, role: e.target.value });
                            }
                          }}
                        >
                          <option value="STUDENT">STUDENT</option>
                          <option value="TRAINER">TRAINER</option>
                          <option value="ADMIN">ADMIN</option>
                        </select>
                      </td>

                      <td className="py-3 px-2">
                        {u.isActive !== false ? (
                          <span className="badge bg-emerald-50 text-emerald-700 font-medium">Active</span>
                        ) : (
                          <span className="badge bg-red-50 text-red-700 font-medium">Disabled</span>
                        )}
                      </td>

                      <td className="py-3 px-2 text-right">
                        <button
                          onClick={() => toggleStatusMutation.mutate(u.id)}
                          className={`text-xs font-semibold px-3 py-1 rounded-xl transition-colors ${
                            u.isActive !== false
                              ? 'bg-red-50 text-red-600 hover:bg-red-100'
                              : 'bg-emerald-50 text-emerald-600 hover:bg-emerald-100'
                          }`}
                        >
                          {u.isActive !== false ? 'Deactivate' : 'Activate'}
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
