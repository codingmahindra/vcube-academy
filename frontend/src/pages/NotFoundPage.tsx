import { Link } from 'react-router-dom';
import { GraduationCap, Home } from 'lucide-react';

export function NotFoundPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-6 text-center px-4">
      <div className="flex h-20 w-20 items-center justify-center rounded-3xl bg-brand-50">
        <GraduationCap className="h-10 w-10 text-brand-400" />
      </div>
      <div>
        <h1 className="text-6xl font-bold text-slate-200">404</h1>
        <p className="text-xl font-semibold text-slate-800 mt-2">Page not found</p>
        <p className="text-sm text-slate-500 mt-1">The page you're looking for doesn't exist.</p>
      </div>
      <Link to="/" className="btn-primary">
        <Home className="h-4 w-4" /> Go home
      </Link>
    </div>
  );
}
