import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { interviewApi, type CompanySummaryDto } from '../../../api/interview';
import { Building2, ChevronRight, Search, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';

export function InterviewCompanyListPage() {
  const navigate = useNavigate();
  const [companies, setCompanies] = useState<CompanySummaryDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    async function loadCompanies() {
      try {
        setLoading(true);
        const data = await interviewApi.getCompanies();
        setCompanies(data);
      } catch (err) {
        toast.error('Failed to load companies');
      } finally {
        setLoading(false);
      }
    }
    loadCompanies();
  }, []);

  if (loading) {
    return (
      <div className="flex h-72 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  const filtered = companies.filter((c) =>
    c.name.toLowerCase().includes(search.toLowerCase()) ||
    c.industry?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-800">Company Placement Tracks</h1>
          <p className="text-xs text-slate-500 mt-0.5">Explore reported interview questions, technical rounds, and hiring patterns</p>
        </div>
        <div className="relative w-full sm:w-72">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search company (TCS, Amazon, etc.)..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="input pl-9 text-xs w-full"
          />
        </div>
      </div>

      {/* Companies Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {filtered.map((comp) => (
          <div
            key={comp.id}
            onClick={() => navigate(`/student/interview/companies/${comp.id}`)}
            className="card p-5 hover:border-brand-300 hover:shadow-md cursor-pointer transition-all flex flex-col justify-between space-y-4"
          >
            <div>
              <div className="flex items-center justify-between">
                <div className="h-10 w-10 rounded-xl bg-brand-50 text-brand-600 flex items-center justify-center font-bold text-sm">
                  <Building2 className="h-5 w-5" />
                </div>
                <span className="badge bg-slate-100 text-slate-600 text-[10px]">
                  {comp.tier}
                </span>
              </div>
              <h2 className="text-base font-bold text-slate-800 mt-3">{comp.name}</h2>
              <p className="text-xs text-slate-500 mt-1 line-clamp-2">{comp.description}</p>
            </div>

            <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
              <span className="font-semibold text-brand-600">
                {comp.totalQuestions} Questions Available
              </span>
              <ChevronRight className="h-4 w-4 text-slate-400" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
