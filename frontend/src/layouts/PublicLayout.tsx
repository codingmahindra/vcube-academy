import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { GraduationCap, LogIn, UserPlus } from 'lucide-react';

export function PublicLayout() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();
  const isLanding = location.pathname === '/';

  return (
    <div className="min-h-screen flex flex-col">
      {/* Navbar */}
      <header className="sticky top-0 z-50 border-b border-slate-100 bg-white/80 backdrop-blur-md">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6">
          <Link to="/" className="flex items-center gap-2.5 font-display font-bold text-brand-700">
            <GraduationCap className="h-7 w-7 text-brand-600" />
            <span className="hidden sm:block text-sm leading-tight">
              VCUBE <span className="text-accent-500">Academy</span>
            </span>
          </Link>

          {!isAuthenticated && (
            <nav className="flex items-center gap-2">
              {isLanding && (
                <>
                  <a href="#roadmap" className="hidden md:block btn-ghost text-xs">Roadmap</a>
                  <a href="#trainers" className="hidden md:block btn-ghost text-xs">Trainers</a>
                </>
              )}
              <Link to="/login" className="btn-secondary py-2 text-xs">
                <LogIn className="h-3.5 w-3.5" /> Sign In
              </Link>
              <Link to="/register" className="btn-primary py-2 text-xs">
                <UserPlus className="h-3.5 w-3.5" /> Get Started
              </Link>
            </nav>
          )}

          {isAuthenticated && (
            <Link to="/dashboard" className="btn-primary py-2 text-xs">
              Go to Dashboard
            </Link>
          )}
        </div>
      </header>

      <main className="flex-1">
        <Outlet />
      </main>
    </div>
  );
}
