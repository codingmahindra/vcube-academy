import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { dsaApi, type DsaCategory, type DsaDifficulty, type DsaProblemSummary } from '../../../api/dsa';
import {
  Search, Filter, CheckCircle2, Clock, Play,
  ChevronLeft, ChevronRight, Layers, Building2, Loader2, RefreshCw
} from 'lucide-react';
import toast from 'react-hot-toast';

export function DsaProblemListPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const [categories, setCategories] = useState<DsaCategory[]>([]);
  const [problems, setProblems] = useState<DsaProblemSummary[]>([]);
  const [loading, setLoading] = useState(true);

  const [search, setSearch] = useState(searchParams.get('search') ?? '');
  const [selectedCategory, setSelectedCategory] = useState<string>(searchParams.get('categoryId') ?? 'ALL');
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>(searchParams.get('difficulty') ?? 'ALL');
  const [selectedStatus, setSelectedStatus] = useState<'ALL' | 'SOLVED' | 'UNSOLVED'>(
    (searchParams.get('status') as any) ?? 'ALL'
  );
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    async function loadCategories() {
      try {
        const cats = await dsaApi.getCategories();
        setCategories(cats);
      } catch (e) {
        toast.error('Failed to load categories');
      }
    }
    loadCategories();
  }, []);

  useEffect(() => {
    async function fetchProblems() {
      try {
        setLoading(true);
        const res = await dsaApi.getProblems({
          categoryId: selectedCategory !== 'ALL' ? Number(selectedCategory) : undefined,
          difficulty: selectedDifficulty !== 'ALL' ? (selectedDifficulty as DsaDifficulty) : undefined,
          search: search.trim() ? search.trim() : undefined,
          statusFilter: selectedStatus,
          page,
          size: 15,
        });
        setProblems(res.content);
        setTotalPages(res.totalPages);
        setTotalElements(res.totalElements);
      } catch (err) {
        toast.error('Failed to load DSA problems');
      } finally {
        setLoading(false);
      }
    }
    fetchProblems();
  }, [selectedCategory, selectedDifficulty, selectedStatus, page, search]);

  function handleFilterChange(key: string, val: string) {
    setPage(0);
    const newParams = new URLSearchParams(searchParams);
    if (val === 'ALL' || !val) {
      newParams.delete(key);
    } else {
      newParams.set(key, val);
    }
    setSearchParams(newParams);
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-800">DSA Problem Bank</h1>
          <p className="text-xs text-slate-500 mt-0.5">
            {totalElements} problem{totalElements === 1 ? '' : 's'} available for practice
          </p>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="p-4 rounded-2xl bg-white border border-slate-100 shadow-sm space-y-3">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search problems, topics..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                handleFilterChange('search', e.target.value);
              }}
              className="input pl-10 text-xs w-full"
            />
          </div>

          {/* Category */}
          <select
            value={selectedCategory}
            onChange={(e) => {
              setSelectedCategory(e.target.value);
              handleFilterChange('categoryId', e.target.value);
            }}
            className="input text-xs w-full"
          >
            <option value="ALL">All Categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name} ({c.totalProblems})
              </option>
            ))}
          </select>

          {/* Difficulty */}
          <select
            value={selectedDifficulty}
            onChange={(e) => {
              setSelectedDifficulty(e.target.value);
              handleFilterChange('difficulty', e.target.value);
            }}
            className="input text-xs w-full"
          >
            <option value="ALL">All Difficulties</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>

          {/* Status */}
          <select
            value={selectedStatus}
            onChange={(e) => {
              const val = e.target.value as any;
              setSelectedStatus(val);
              handleFilterChange('status', val);
            }}
            className="input text-xs w-full"
          >
            <option value="ALL">All Status</option>
            <option value="SOLVED">Solved</option>
            <option value="UNSOLVED">Unsolved</option>
          </select>
        </div>
      </div>

      {/* Problems Table */}
      <div className="rounded-2xl bg-white border border-slate-100 shadow-sm overflow-hidden">
        {loading ? (
          <div className="flex h-64 items-center justify-center">
            <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
          </div>
        ) : problems.length === 0 ? (
          <div className="p-12 text-center space-y-3">
            <Layers className="h-10 w-10 text-slate-300 mx-auto" />
            <p className="text-sm font-semibold text-slate-700">No problems found</p>
            <p className="text-xs text-slate-400">Try adjusting your filters or search keywords.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 border-b border-slate-100 text-slate-500 font-semibold uppercase tracking-wider text-[10px]">
                <tr>
                  <th className="px-5 py-3 w-12 text-center">Status</th>
                  <th className="px-5 py-3">Title</th>
                  <th className="px-5 py-3">Category</th>
                  <th className="px-5 py-3">Difficulty</th>
                  <th className="px-5 py-3">Company Tags</th>
                  <th className="px-5 py-3 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {problems.map((p) => {
                  let tags: string[] = [];
                  try {
                    if (p.companyTags) tags = JSON.parse(p.companyTags);
                  } catch {
                    tags = [];
                  }

                  return (
                    <tr
                      key={p.id}
                      onClick={() => navigate(`/student/dsa/problems/${p.id}`)}
                      className="hover:bg-brand-50/20 cursor-pointer transition-colors"
                    >
                      <td className="px-5 py-3.5 text-center">
                        {p.isSolved ? (
                          <CheckCircle2 className="h-4 w-4 text-emerald-500 mx-auto" />
                        ) : p.isAttempted ? (
                          <Clock className="h-4 w-4 text-amber-500 mx-auto" />
                        ) : (
                          <div className="h-2 w-2 rounded-full bg-slate-200 mx-auto" />
                        )}
                      </td>
                      <td className="px-5 py-3.5">
                        <p className="font-bold text-slate-800">{p.title}</p>
                        {p.subtopic && <p className="text-[10px] text-slate-400 mt-0.5">{p.subtopic}</p>}
                      </td>
                      <td className="px-5 py-3.5 text-slate-600 font-medium">{p.categoryName}</td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`badge text-[10px] ${
                            p.difficulty === 'EASY'
                              ? 'bg-emerald-100 text-emerald-700'
                              : p.difficulty === 'MEDIUM'
                              ? 'bg-amber-100 text-amber-700'
                              : 'bg-red-100 text-red-700'
                          }`}
                        >
                          {p.difficulty}
                        </span>
                      </td>
                      <td className="px-5 py-3.5">
                        <div className="flex flex-wrap gap-1 max-w-xs">
                          {tags.slice(0, 3).map((tag) => (
                            <span key={tag} className="badge bg-slate-100 text-slate-600 text-[10px]">
                              {tag}
                            </span>
                          ))}
                          {tags.length > 3 && (
                            <span className="badge bg-slate-100 text-slate-400 text-[10px]">
                              +{tags.length - 3}
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate(`/student/dsa/problems/${p.id}`);
                          }}
                          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-brand-50 text-brand-700 hover:bg-brand-100 font-semibold text-xs transition-colors"
                        >
                          <Play className="h-3 w-3" /> Solve
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-5 py-3 border-t border-slate-100 text-xs text-slate-500">
            <span>
              Page {page + 1} of {totalPages}
            </span>
            <div className="flex items-center gap-1">
              <button
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                className="p-1.5 rounded-lg border border-slate-200 disabled:opacity-40 hover:bg-slate-50"
              >
                <ChevronLeft className="h-4 w-4" />
              </button>
              <button
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
                className="p-1.5 rounded-lg border border-slate-200 disabled:opacity-40 hover:bg-slate-50"
              >
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
