import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Sparkles,
  Briefcase,
  CheckCircle2,
  AlertCircle,
  ChevronRight,
  ArrowLeft,
  BookOpen,
  Sliders,
} from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type { JobSummaryDto } from '../../api/jobs';

interface RecommendationItem {
  job: JobSummaryDto;
  matchScore: number;
  matchedSkills: string[];
  missingSkills: string[];
  summary: string;
}

export const JobRecommendationsPage: React.FC = () => {
  const [recommendations, setRecommendations] = useState<RecommendationItem[]>([]);
  const [revisions, setRevisions] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    loadRecommendations();
  }, []);

  const loadRecommendations = async () => {
    try {
      setLoading(true);
      const res = await jobsApi.getJobRecommendations();
      setRecommendations(res.recommendedJobs);
      setRevisions(res.recommendedSkillRevision);
    } catch (err) {
      console.error('Failed to load job recommendations:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8 animate-fade-in">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Sparkles className="w-6 h-6 text-amber-400" />
            Personalized Job Recommendations
          </h1>
          <p className="text-sm text-gray-400 mt-1">
            Jobs matching your configured preferences, technical skills, and experience level.
          </p>
        </div>
        <Link
          to="/student/job-preferences"
          className="btn btn-secondary text-xs inline-flex items-center gap-2"
        >
          <Sliders className="w-4 h-4" /> Edit Career Preferences
        </Link>
      </div>

      {/* Suggested Revision Box */}
      {revisions.length > 0 && (
        <div className="card p-6 border-blue-500/20 bg-blue-950/20 space-y-3">
          <h3 className="text-sm font-bold text-blue-300 uppercase tracking-wider flex items-center gap-2">
            <BookOpen className="w-4 h-4 text-blue-400" />
            Key Technical Topics to Boost Match Score
          </h3>
          <div className="flex flex-wrap gap-2">
            {revisions.map((topic, i) => (
              <span
                key={i}
                className="text-xs px-3 py-1 rounded-full bg-blue-900/40 text-blue-200 border border-blue-800/60 font-medium"
              >
                {topic}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Recommendations List */}
      {loading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((n) => (
            <div key={n} className="card p-6 border-gray-800 animate-pulse h-36 bg-gray-900/60"></div>
          ))}
        </div>
      ) : recommendations.length === 0 ? (
        <div className="card p-12 text-center border-gray-800 space-y-3">
          <Briefcase className="w-12 h-12 text-gray-600 mx-auto" />
          <h3 className="text-lg font-medium text-white">No job matches found</h3>
          <p className="text-sm text-gray-400 max-w-sm mx-auto">
            Update your career preferences and technologies to receive targeted recommendations.
          </p>
          <Link to="/student/job-preferences" className="btn btn-primary text-sm inline-block">
            Configure Preferences
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {recommendations.map((item) => (
            <div
              key={item.job.id}
              className="card p-6 border-gray-800 hover:border-blue-500/40 bg-gray-900/60 flex flex-col md:flex-row md:items-center justify-between gap-6 transition-all"
            >
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-xl bg-gray-800 border border-gray-700 flex items-center justify-center font-bold text-blue-400 text-xl">
                  {item.job.companyName.charAt(0)}
                </div>
                <div className="space-y-2">
                  <div className="flex items-center gap-3">
                    <h3 className="text-base font-bold text-white hover:text-blue-400 transition-colors">
                      <Link to={`/student/jobs/${item.job.id}`}>{item.job.title}</Link>
                    </h3>
                    <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-blue-500/15 text-blue-400 border border-blue-500/30">
                      {item.matchScore}% Match
                    </span>
                  </div>
                  <p className="text-xs text-gray-400">
                    {item.job.companyName} • {item.job.location} • {item.job.workMode}
                  </p>
                  <p className="text-xs text-gray-300">{item.summary}</p>

                  <div className="flex flex-wrap gap-2 pt-1">
                    {item.matchedSkills.map((s, idx) => (
                      <span
                        key={idx}
                        className="text-[11px] px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-300 border border-emerald-500/20"
                      >
                        ✓ {s}
                      </span>
                    ))}
                    {item.missingSkills.map((s, idx) => (
                      <span
                        key={idx}
                        className="text-[11px] px-2 py-0.5 rounded bg-amber-500/10 text-amber-300 border border-amber-500/20"
                      >
                        + Learn {s}
                      </span>
                    ))}
                  </div>
                </div>
              </div>

              <div className="self-end md:self-center">
                <Link
                  to={`/student/jobs/${item.job.id}`}
                  className="btn btn-primary text-xs px-5 py-2.5 inline-flex items-center gap-1.5 shadow-lg shadow-blue-500/20"
                >
                  Prepare & Apply
                  <ChevronRight className="w-4 h-4" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
export default JobRecommendationsPage;
