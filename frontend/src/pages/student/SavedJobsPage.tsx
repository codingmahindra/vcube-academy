import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Bookmark, BookmarkCheck, Trash2, ArrowLeft, Briefcase, ExternalLink, ChevronRight, Clock } from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type { SavedJobDto } from '../../api/jobs';

export const SavedJobsPage: React.FC = () => {
  const [savedJobs, setSavedJobs] = useState<SavedJobDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);

  useEffect(() => {
    loadSavedJobs();
  }, [page]);

  const loadSavedJobs = async () => {
    try {
      setLoading(true);
      const res = await jobsApi.getSavedJobs(page, 10);
      setSavedJobs(res.content);
      setTotalPages(res.totalPages);
    } catch (err) {
      console.error('Failed to load saved jobs:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = async (jobId: number) => {
    try {
      await jobsApi.unsaveJob(jobId);
      setSavedJobs((prev) => prev.filter((s) => s.jobId !== jobId));
    } catch (err) {
      console.error('Failed to unsave job:', err);
    }
  };

  return (
    <div className="space-y-8 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Bookmark className="w-6 h-6 text-blue-400" />
            Saved Jobs & Watchlist
          </h1>
          <p className="text-sm text-gray-400 mt-1">
            Keep track of job opportunities you are targeting and review required preparation.
          </p>
        </div>
        <Link to="/student/jobs" className="btn btn-secondary text-sm inline-flex items-center gap-2">
          <ArrowLeft className="w-4 h-4" /> Browse All Jobs
        </Link>
      </div>

      {loading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((n) => (
            <div key={n} className="card p-6 border-gray-800 animate-pulse h-28 bg-gray-900/60"></div>
          ))}
        </div>
      ) : savedJobs.length === 0 ? (
        <div className="card p-12 text-center border-gray-800 space-y-4">
          <Bookmark className="w-12 h-12 text-gray-600 mx-auto" />
          <h3 className="text-lg font-medium text-white">No saved jobs yet</h3>
          <p className="text-sm text-gray-400 max-w-sm mx-auto">
            Explore job openings and bookmark roles to monitor application deadlines and skill requirements.
          </p>
          <Link to="/student/jobs" className="btn btn-primary text-sm inline-block">
            Explore Jobs Now
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {savedJobs.map((item) => {
            const job = item.job;
            return (
              <div
                key={item.id}
                className="card p-6 border-gray-800 hover:border-blue-500/40 bg-gray-900/60 flex flex-col md:flex-row md:items-center justify-between gap-6 transition-all"
              >
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 rounded-xl bg-gray-800 border border-gray-700 flex items-center justify-center font-bold text-blue-400 text-xl">
                    {job.companyName.charAt(0)}
                  </div>
                  <div className="space-y-1">
                    <h3 className="text-base font-bold text-white hover:text-blue-400 transition-colors">
                      <Link to={`/student/jobs/${job.id}`}>{job.title}</Link>
                    </h3>
                    <div className="flex flex-wrap items-center gap-3 text-xs text-gray-400">
                      <span className="text-gray-300 font-medium">{job.companyName}</span>
                      <span>•</span>
                      <span>{job.location}</span>
                      <span>•</span>
                      <span>{job.workMode}</span>
                      {job.salaryText && (
                        <>
                          <span>•</span>
                          <span className="text-emerald-400 font-semibold">{job.salaryText}</span>
                        </>
                      )}
                    </div>
                    <div className="flex flex-wrap gap-1.5 pt-2">
                      {job.skills.map((sk) => (
                        <span key={sk.id} className="text-[11px] px-2 py-0.5 rounded-full bg-blue-900/30 text-blue-300 border border-blue-800/40">
                          {sk.name}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-3 self-end md:self-center">
                  <button
                    onClick={() => handleRemove(job.id)}
                    className="btn btn-secondary text-xs px-3 py-2 text-rose-400 hover:bg-rose-500/10 border-rose-500/30"
                    title="Remove from watchlist"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                  <Link
                    to={`/student/jobs/${job.id}`}
                    className="btn btn-primary text-xs px-4 py-2 inline-flex items-center gap-1.5"
                  >
                    Prepare & View
                    <ChevronRight className="w-3.5 h-3.5" />
                  </Link>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
export default SavedJobsPage;
