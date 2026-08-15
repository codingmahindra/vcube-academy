import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { searchApi, type GlobalSearchResult } from '../../api/search';
import {
  Search, ArrowRight, BookOpen, Brain, Code2,
  HelpCircle, Building, Briefcase, FileCheck
} from 'lucide-react';

export default function GlobalSearchPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const initialQuery = searchParams.get('q') || '';

  const [query, setQuery] = useState(initialQuery);
  const [results, setResults] = useState<GlobalSearchResult[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!query.trim() || query.length < 2) {
      setResults([]);
      return;
    }

    const handler = setTimeout(async () => {
      try {
        setLoading(true);
        const data = await searchApi.search(query.trim());
        setResults(data);
      } catch (e) {
        console.error('Search error', e);
      } finally {
        setLoading(false);
      }
    }, 250);

    return () => clearTimeout(handler);
  }, [query]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSearchParams({ q: query });
  };

  const getCategoryIcon = (cat: string) => {
    switch (cat) {
      case 'COURSE':
      case 'TOPIC':
        return <BookOpen className="h-4 w-4 text-blue-600" />;
      case 'DSA':
        return <Code2 className="h-4 w-4 text-purple-600" />;
      case 'INTERVIEW_TOPIC':
      case 'INTERVIEW_QUESTION':
        return <HelpCircle className="h-4 w-4 text-emerald-600" />;
      case 'COMPANY':
        return <Building className="h-4 w-4 text-amber-600" />;
      case 'JOB':
        return <Briefcase className="h-4 w-4 text-indigo-600" />;
      case 'PLACEMENT_PAPER':
        return <FileCheck className="h-4 w-4 text-rose-600" />;
      default:
        return <Brain className="h-4 w-4 text-slate-600" />;
    }
  };

  return (
    <div className="space-y-8 max-w-4xl mx-auto pb-16">
      {/* Search Header */}
      <div className="rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-8 text-white shadow-xl">
        <div className="space-y-3">
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight">
            Universal Academy Search
          </h1>
          <p className="text-xs md:text-sm text-slate-300">
            Search across Courses, Topics, LeetCode-style DSA challenges, Interview questions, Company hiring hubs, and Placement papers.
          </p>

          <form onSubmit={handleSearchSubmit} className="pt-2">
            <div className="relative">
              <Search className="absolute left-4 top-3.5 h-5 w-5 text-slate-400" />
              <input
                type="text"
                value={query}
                onChange={(e) => {
                  setQuery(e.target.value);
                  setSearchParams({ q: e.target.value });
                }}
                placeholder="Search anything (e.g. Spring Boot, Two Sum, TCS, Exception Handling, Fresher)..."
                className="w-full rounded-2xl border border-white/20 bg-white/10 backdrop-blur-md pl-12 pr-4 py-3 text-sm text-white placeholder-slate-400 focus:bg-white focus:text-slate-900 focus:outline-none focus:ring-4 focus:ring-indigo-500/30 transition-all"
                autoFocus
              />
            </div>
          </form>
        </div>
      </div>

      {/* Results Section */}
      {loading ? (
        <div className="flex items-center justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        </div>
      ) : query.length >= 2 && results.length === 0 ? (
        <div className="rounded-2xl border border-slate-200 bg-white p-12 text-center text-slate-500">
          <Search className="h-10 w-10 mx-auto text-slate-300 mb-2" />
          <p className="text-sm font-bold text-slate-700">No matching content found for "{query}"</p>
          <p className="text-xs text-slate-500 mt-1">Try searching for keywords like "Java", "DSA", "Infosys", "Hibernate", or "Resume".</p>
        </div>
      ) : (
        <div className="space-y-3">
          {results.map((item) => (
            <Link
              key={item.id}
              to={item.route}
              className="group flex flex-col sm:flex-row sm:items-center justify-between gap-4 rounded-2xl border border-slate-200 bg-white p-4.5 shadow-xs hover:border-indigo-300 hover:shadow-sm transition-all"
            >
              <div className="flex items-start gap-3.5">
                <span className="p-2 rounded-xl bg-slate-100 mt-0.5">{getCategoryIcon(item.category)}</span>
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-bold text-slate-900 group-hover:text-indigo-600 transition-colors">
                      {item.title}
                    </span>
                    <span className="rounded-md bg-slate-100 px-2 py-0.5 text-[10px] font-bold text-slate-600 uppercase tracking-wider">
                      {item.categoryLabel}
                    </span>
                  </div>
                  <p className="text-xs text-slate-500 leading-relaxed max-w-2xl">{item.description}</p>
                </div>
              </div>

              <div className="flex items-center gap-2 sm:justify-end pl-11 sm:pl-0">
                {item.badge && (
                  <span className="rounded-full bg-indigo-50 border border-indigo-200 px-2.5 py-0.5 text-[11px] font-bold text-indigo-700 whitespace-nowrap">
                    {item.badge}
                  </span>
                )}
                <ArrowRight className="h-4 w-4 text-slate-400 group-hover:text-indigo-600 group-hover:translate-x-1 transition-all" />
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
