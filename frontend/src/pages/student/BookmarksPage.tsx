import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { bookmarksApi, type Bookmark, type BookmarkItemType } from '../../api/bookmarks';
import {
  Bookmark as BookmarkIcon, Trash2, ArrowRight, BookOpen,
  Brain, Code2, HelpCircle, Briefcase, FileCheck, AlertCircle
} from 'lucide-react';

const CATEGORIES: Array<{ key?: BookmarkItemType; label: string }> = [
  { label: 'All Items' },
  { key: 'TOPIC', label: 'Topics' },
  { key: 'MCQ', label: 'MCQs' },
  { key: 'DSA_PROBLEM', label: 'DSA Challenges' },
  { key: 'INTERVIEW_QUESTION', label: 'Interview Q&A' },
  { key: 'JOB', label: 'Saved Jobs' },
  { key: 'PLACEMENT_PAPER', label: 'Placement Papers' },
];

export default function BookmarksPage() {
  const [bookmarks, setBookmarks] = useState<Bookmark[]>([]);
  const [selectedType, setSelectedType] = useState<BookmarkItemType | undefined>(undefined);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadBookmarks() {
      try {
        setLoading(true);
        const data = await bookmarksApi.list(selectedType);
        setBookmarks(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load bookmarks');
      } finally {
        setLoading(false);
      }
    }
    loadBookmarks();
  }, [selectedType]);

  const handleRemove = async (itemType: BookmarkItemType, itemId: number) => {
    try {
      await bookmarksApi.remove(itemType, itemId);
      setBookmarks((prev) => prev.filter((b) => !(b.itemType === itemType && b.itemId === itemId)));
    } catch (e) {
      console.error('Failed to remove bookmark', e);
    }
  };

  const getItemIcon = (type: string) => {
    switch (type) {
      case 'TOPIC':
        return <BookOpen className="h-4 w-4 text-blue-600" />;
      case 'MCQ':
        return <Brain className="h-4 w-4 text-emerald-600" />;
      case 'DSA_PROBLEM':
        return <Code2 className="h-4 w-4 text-purple-600" />;
      case 'INTERVIEW_QUESTION':
        return <HelpCircle className="h-4 w-4 text-amber-600" />;
      case 'JOB':
        return <Briefcase className="h-4 w-4 text-indigo-600" />;
      case 'PLACEMENT_PAPER':
        return <FileCheck className="h-4 w-4 text-rose-600" />;
      default:
        return <BookmarkIcon className="h-4 w-4 text-slate-600" />;
    }
  };

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-16">
      {/* Header */}
      <div className="rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-8 text-white shadow-xl">
        <div className="space-y-2">
          <div className="inline-flex items-center gap-2 rounded-full bg-indigo-500/20 px-3.5 py-1 text-xs font-bold text-indigo-300 border border-indigo-400/30">
            <BookmarkIcon className="h-3.5 w-3.5" /> Universal Study Bookmarks
          </div>
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight">
            My Saved Topics, Challenges & Exam Papers
          </h1>
          <p className="text-xs md:text-sm text-slate-300 max-w-2xl">
            Quickly revisit difficult MCQ questions, DSA algorithms, target job portal listings, and interview questions for active recall.
          </p>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center gap-2 overflow-x-auto no-scrollbar pb-1">
        {CATEGORIES.map((cat, idx) => {
          const active = selectedType === cat.key;
          return (
            <button
              key={idx}
              onClick={() => setSelectedType(cat.key)}
              className={`rounded-full px-4 py-2 text-xs font-bold transition-all whitespace-nowrap ${
                active
                  ? 'bg-indigo-600 text-white shadow-xs'
                  : 'border border-slate-200 bg-white text-slate-600 hover:border-slate-300'
              }`}
            >
              {cat.label}
            </button>
          );
        })}
      </div>

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-xs text-red-700 flex items-center gap-2">
          <AlertCircle className="h-4 w-4" /> {error}
        </div>
      )}

      {/* Bookmarks List */}
      {loading ? (
        <div className="flex items-center justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        </div>
      ) : bookmarks.length === 0 ? (
        <div className="rounded-2xl border border-slate-200 bg-white p-12 text-center text-slate-500 space-y-3">
          <BookmarkIcon className="h-10 w-10 mx-auto text-slate-300" />
          <h3 className="text-sm font-bold text-slate-700">No saved items found</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            You can bookmark any Topic, DSA problem, Interview question, or Placement paper across VCUBE Academy for easy revision.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {bookmarks.map((bm) => (
            <div
              key={bm.id}
              className="flex flex-col justify-between rounded-2xl border border-slate-200 bg-white p-5 shadow-xs hover:border-indigo-300 hover:shadow-sm transition-all space-y-3"
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-2">
                  <span className="p-1.5 rounded-lg bg-slate-100">{getItemIcon(bm.itemType)}</span>
                  <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
                    {bm.itemType.replace(/_/g, ' ')}
                  </span>
                </div>
                <button
                  type="button"
                  onClick={() => handleRemove(bm.itemType, bm.itemId)}
                  className="text-slate-400 hover:text-red-600 transition-colors p-1"
                  title="Remove Bookmark"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>

              <div>
                <h3 className="text-sm font-bold text-slate-900 leading-snug">{bm.itemTitle}</h3>
                {bm.itemSubtitle && (
                  <p className="text-xs text-slate-500 mt-0.5 line-clamp-1">{bm.itemSubtitle}</p>
                )}
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-slate-100 text-xs">
                <span className="text-[11px] text-slate-400">
                  Saved {new Date(bm.createdAt).toLocaleDateString()}
                </span>
                <Link
                  to={bm.itemRoute}
                  className="inline-flex items-center gap-1 font-bold text-indigo-600 hover:text-indigo-700"
                >
                  Open Item <ArrowRight className="h-3.5 w-3.5" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
