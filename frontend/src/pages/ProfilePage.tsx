import React from 'react';
import { useAuth } from '../hooks/useAuth';
import {
  User, Mail, Phone, Calendar, Shield,
} from 'lucide-react';

function InfoRow({ icon: Icon, label, value }: {
  icon: React.ElementType; label: string; value: string | undefined | null;
}) {
  return (
    <div className="flex items-start gap-3 py-3 border-b border-slate-100 last:border-0">
      <div className="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-lg bg-brand-50">
        <Icon className="h-4 w-4 text-brand-600" />
      </div>
      <div>
        <p className="text-xs text-slate-400">{label}</p>
        <p className="text-sm font-medium text-slate-800">{value ?? '—'}</p>
      </div>
    </div>
  );
}

export function ProfilePage() {
  const { user } = useAuth();

  if (!user) return null;

  const joined = user.createdAt
    ? new Date(user.createdAt).toLocaleDateString('en-IN', { year: 'numeric', month: 'long', day: 'numeric' })
    : '—';

  return (
    <div className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="text-2xl font-bold text-slate-900 mb-6">My Profile</h1>

      <div className="card shadow-sm">
        {/* Avatar */}
        <div className="flex items-center gap-4 mb-6 pb-6 border-b border-slate-100">
          <div className="h-16 w-16 rounded-2xl bg-brand-100 flex items-center justify-center flex-shrink-0">
            <span className="text-2xl font-bold text-brand-700">
              {user.fullName?.[0]?.toUpperCase() ?? 'U'}
            </span>
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">{user.fullName}</h2>
            <div className="flex flex-wrap gap-1 mt-1">
              {user.roles.map((r) => (
                <span
                  key={r}
                  className={`badge text-xs ${
                    r === 'STUDENT' ? 'bg-brand-100 text-brand-700' :
                    r === 'TRAINER' ? 'bg-emerald-100 text-emerald-700' :
                    'bg-violet-100 text-violet-700'
                  }`}
                >
                  {r}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Details */}
        <InfoRow icon={User}     label="Full Name"   value={user.fullName} />
        <InfoRow icon={Mail}     label="Email"       value={user.email} />
        <InfoRow icon={Phone}    label="Phone"       value={user.phone} />
        <InfoRow icon={Calendar} label="Joined"      value={joined} />
        <InfoRow icon={Shield}   label="Status"      value={user.isActive ? 'Active' : 'Inactive'} />
      </div>
    </div>
  );
}
