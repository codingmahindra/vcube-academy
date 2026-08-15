import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import type { RoleName } from '../types';

interface PrivateRouteProps {
  allowedRoles?: RoleName[];
  children?: React.ReactNode;
}

export function PrivateRoute({ allowedRoles, children }: PrivateRouteProps) {
  const { isAuthenticated, isLoading, user } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-brand-600 border-t-transparent" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && user) {
    const hasRole = user.roles.some((r) => allowedRoles.includes(r));
    if (!hasRole) {
      // Redirect to their correct dashboard
      const role = user.roles[0];
      const dashboardMap: Record<RoleName, string> = {
        STUDENT: '/student/dashboard',
        TRAINER: '/trainer/dashboard',
        ADMIN: '/admin/dashboard',
      };
      return <Navigate to={dashboardMap[role] ?? '/'} replace />;
    }
  }

  return children ? <>{children}</> : <Outlet />;
}
