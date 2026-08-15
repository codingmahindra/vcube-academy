import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { interviewApi, type InterviewCategoryDto, type InterviewTopicDto } from '../../../api/interview';
import {
  BookOpen, ChevronRight, CheckCircle2, Layers, Search, Loader2
} from 'lucide-react';
import toast from 'react-hot-toast';

export function InterviewTopicListPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [categories, setCategories] = useState<InterviewCategoryDto[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(
    searchParams.get('categoryId') ? Number(searchParams.get('categoryId')) : null
  );
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const data = await interviewApi.getCategories();
        setCategories(data);
        if (!selectedCategoryId && data.length > 0) {
          setSelectedCategoryId(data[0].id);
        }
      } catch (err) {
        toast.error('Failed to load interview topics');
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="flex h-72 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  const activeCategory = categories.find((c) => c.id === selectedCategoryId) || categories[0];
  const filteredTopics = activeCategory?.topics.filter((t) =>
    t.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    t.description?.toLowerCase().includes(searchTerm.toLowerCase())
  ) || [];

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-xl sm:text-2xl font-bold text-slate-800">Interview Topic Q&A Bank</h1>
        <p className="text-xs text-slate-500 mt-0.5">Explore question catalogs curated by subject and technical domain</p>
      </div>

      {/* Category Pills */}
      <div className="flex items-center gap-2 overflow-x-auto pb-2">
        {categories.map((cat) => {
          const active = cat.id === selectedCategoryId;
          return (
            <button
              key={cat.id}
              onClick={() => setSelectedCategoryId(cat.id)}
              className={`px-4 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition-all flex items-center gap-2 ${
                active
                  ? 'bg-brand-600 text-white shadow-md shadow-brand-600/20'
                  : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
            >
              <Layers className="h-3.5 w-3.5" />
              {cat.name}
              <span className={`text-[10px] px-1.5 py-0.5 rounded-full ${active ? 'bg-white/20' : 'bg-slate-100'}`}>
                {cat.totalQuestions}
              </span>
            </button>
          );
        })}
      </div>

      {/* Topics Grid */}
      <div className="rounded-2xl bg-white p-6 border border-slate-100 shadow-sm space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <h2 className="text-base font-bold text-slate-800">{activeCategory?.name} Topics</h2>
            <p className="text-xs text-slate-400">{activeCategory?.description}</p>
          </div>
          <div className="relative w-full sm:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-slate-400" />
            <input
              type="text"
              placeholder="Search topics..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="input pl-9 text-xs w-full"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          {filteredTopics.map((topic) => {
            const pct = topic.totalQuestions > 0 ? Math.round((topic.completedQuestions / topic.totalQuestions) * 100) : 0;
            return (
              <div
                key={topic.id}
                onClick={() => navigate(`/student/interview/questions?topicId=${topic.id}`)}
                className="p-4 rounded-xl border border-slate-100 hover:border-brand-200 hover:bg-brand-50/20 cursor-pointer transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between">
                    <p className="text-sm font-bold text-slate-800">{topic.name}</p>
                    <ChevronRight className="h-4 w-4 text-slate-400" />
                  </div>
                  <p className="text-xs text-slate-400 mt-1 line-clamp-2">{topic.description}</p>
                </div>

                <div className="mt-4 pt-2 border-t border-slate-50">
                  <div className="flex justify-between text-[10px] text-slate-500 font-medium mb-1">
                    <span>{topic.completedQuestions} / {topic.totalQuestions} mastered</span>
                    <span>{pct}%</span>
                  </div>
                  <div className="w-full bg-slate-100 rounded-full h-1.5">
                    <div className="bg-emerald-500 h-1.5 rounded-full" style={{ width: `${pct}%` }} />
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
