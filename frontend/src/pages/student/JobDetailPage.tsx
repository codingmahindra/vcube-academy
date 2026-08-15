import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  Briefcase,
  Building,
  MapPin,
  Clock,
  DollarSign,
  Bookmark,
  BookmarkCheck,
  CheckCircle2,
  AlertCircle,
  ExternalLink,
  BookOpen,
  Code,
  MessageSquare,
  Sparkles,
  ArrowLeft,
  Calendar,
  Layers,
  Send,
} from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type { JobDetailDto, ApplicationStatus } from '../../api/jobs';

export const JobDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [job, setJob] = useState<JobDetailDto | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [isApplying, setIsApplying] = useState<boolean>(false);
  const [appNotes, setAppNotes] = useState<string>('');
  const [appStatus, setAppStatus] = useState<ApplicationStatus>('APPLIED');
  const [submittingApp, setSubmittingApp] = useState<boolean>(false);
  const [appSuccess, setAppSuccess] = useState<boolean>(false);

  useEffect(() => {
    if (id) {
      loadJobDetail(Number(id));
    }
  }, [id]);

  const loadJobDetail = async (jobId: number) => {
    try {
      setLoading(true);
      const res = await jobsApi.getJobDetail(jobId);
      setJob(res);
    } catch (err) {
      console.error('Failed to load job detail:', err);
    } finally {
      setLoading(false);
    }
  };

  const toggleSave = async () => {
    if (!job) return;
    try {
      if (job.isSaved) {
        await jobsApi.unsaveJob(job.id);
        setJob({ ...job, isSaved: false });
      } else {
        await jobsApi.saveJob(job.id);
        setJob({ ...job, isSaved: true });
      }
    } catch (err) {
      console.error('Save toggle failed:', err);
    }
  };

  const handleTrackApplication = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!job) return;
    try {
      setSubmittingApp(true);
      await jobsApi.createApplication({
        jobId: job.id,
        status: appStatus,
        notes: appNotes || 'Applied directly via company career page.',
      });
      setJob({ ...job, hasApplied: true, applicationStatus: appStatus });
      setAppSuccess(true);
      setTimeout(() => {
        setIsApplying(false);
        setAppSuccess(false);
      }, 1500);
    } catch (err) {
      console.error('Application tracking failed:', err);
    } finally {
      setSubmittingApp(false);
    }
  };

  if (loading) {
    return (
      <div className="card p-12 border-gray-800 animate-pulse space-y-6">
        <div className="h-8 bg-gray-800 rounded w-1/3"></div>
        <div className="h-4 bg-gray-800 rounded w-1/4"></div>
        <div className="h-48 bg-gray-800 rounded"></div>
      </div>
    );
  }

  if (!job) {
    return (
      <div className="card p-12 text-center border-gray-800 space-y-3">
        <AlertCircle className="w-12 h-12 text-rose-500 mx-auto" />
        <h2 className="text-xl font-bold text-white">Job Not Found</h2>
        <p className="text-gray-400 text-sm">The opportunity you are looking for does not exist or has closed.</p>
        <Link to="/student/jobs" className="btn btn-secondary text-sm inline-flex items-center gap-2">
          <ArrowLeft className="w-4 h-4" /> Back to Job Listings
        </Link>
      </div>
    );
  }

  const match = job.matchResult;
  const roadmap = job.preparationRoadmap;

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Top Back Nav */}
      <div className="flex items-center justify-between">
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-2 text-sm text-gray-400 hover:text-white transition-colors"
        >
          <ArrowLeft className="w-4 h-4" /> Back
        </button>

        <div className="flex items-center gap-3">
          <button
            onClick={toggleSave}
            className={`btn btn-secondary inline-flex items-center gap-2 text-sm ${
              job.isSaved ? 'text-blue-400 border-blue-500/40 bg-blue-500/10' : ''
            }`}
          >
            {job.isSaved ? <BookmarkCheck className="w-4 h-4" /> : <Bookmark className="w-4 h-4" />}
            {job.isSaved ? 'Saved in Watchlist' : 'Save Job'}
          </button>

          {job.sourceUrl && (
            <a
              href={job.sourceUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="btn btn-primary inline-flex items-center gap-2 text-sm shadow-lg shadow-blue-500/20"
            >
              <ExternalLink className="w-4 h-4" />
              Apply on Company Portal
            </a>
          )}

          <button
            onClick={() => setIsApplying(true)}
            className={`btn ${
              job.hasApplied ? 'btn-secondary text-emerald-400 border-emerald-500/30' : 'btn-primary'
            } inline-flex items-center gap-2 text-sm`}
          >
            <Send className="w-4 h-4" />
            {job.hasApplied ? `Tracked (${job.applicationStatus})` : 'Track Application'}
          </button>
        </div>
      </div>

      {/* Main Header Card */}
      <div className="card p-8 border-gray-800 bg-gray-900/70 space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="flex items-start gap-4">
            <div className="w-16 h-16 rounded-2xl bg-gray-800 border border-gray-700 flex items-center justify-center font-bold text-blue-400 text-2xl">
              {job.companyName.charAt(0)}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-2xl font-bold text-white">{job.title}</h1>
                {job.companyTier && (
                  <span className="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-blue-500/10 text-blue-400 border border-blue-500/20">
                    {job.companyTier}
                  </span>
                )}
              </div>
              <h2 className="text-base text-gray-300 font-medium mt-1">{job.companyName}</h2>
              <div className="flex flex-wrap items-center gap-4 text-xs text-gray-400 mt-2">
                <span className="inline-flex items-center gap-1.5">
                  <MapPin className="w-3.5 h-3.5 text-gray-500" />
                  {job.location}
                </span>
                <span className="inline-flex items-center gap-1.5">
                  <Briefcase className="w-3.5 h-3.5 text-gray-500" />
                  {job.employmentType} • {job.workMode}
                </span>
                <span className="inline-flex items-center gap-1.5">
                  <Clock className="w-3.5 h-3.5 text-gray-500" />
                  {job.experienceLevel}
                </span>
                {job.salaryText && (
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-semibold">
                    <DollarSign className="w-3.5 h-3.5" />
                    {job.salaryText}
                  </span>
                )}
              </div>
            </div>
          </div>

          <div className="bg-gray-800/60 border border-gray-700/60 p-4 rounded-xl text-xs space-y-2 min-w-[200px]">
            <div className="flex justify-between">
              <span className="text-gray-400">Source:</span>
              <span className="text-gray-200 font-medium">{job.source}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">Posted:</span>
              <span className="text-gray-200 font-medium">{new Date(job.postedDate).toLocaleDateString()}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">Deadline:</span>
              <span className="text-amber-400 font-medium">
                {job.applicationDeadline ? new Date(job.applicationDeadline).toLocaleDateString() : 'Rolling'}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 2 Columns: Job Description & Details */}
        <div className="lg:col-span-2 space-y-6">
          {/* Required Skills Card */}
          <div className="card p-6 border-gray-800 space-y-3">
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <Layers className="w-4 h-4 text-blue-400" />
              Required & Preferred Skills
            </h3>
            <div className="flex flex-wrap gap-2 pt-2">
              {job.skills.map((sk) => (
                <span
                  key={sk.id}
                  className={`text-xs px-3 py-1 rounded-full font-medium border ${
                    sk.isRequired
                      ? 'bg-blue-500/15 text-blue-300 border-blue-500/30'
                      : 'bg-gray-800 text-gray-300 border-gray-700'
                  }`}
                >
                  {sk.name} {sk.isRequired ? '(Required)' : '(Preferred)'}
                </span>
              ))}
            </div>
          </div>

          {/* Job Description */}
          <div className="card p-6 border-gray-800 space-y-4">
            <h3 className="text-base font-bold text-white">About the Role</h3>
            <div className="text-sm text-gray-300 leading-relaxed whitespace-pre-line">
              {job.description}
            </div>

            {job.responsibilities && (
              <div className="pt-4 border-t border-gray-800 space-y-2">
                <h4 className="text-sm font-semibold text-white">Key Responsibilities</h4>
                <div className="text-sm text-gray-300 whitespace-pre-line leading-relaxed">
                  {job.responsibilities}
                </div>
              </div>
            )}

            {job.qualification && (
              <div className="pt-4 border-t border-gray-800 space-y-2">
                <h4 className="text-sm font-semibold text-white">Eligibility & Qualifications</h4>
                <div className="text-sm text-gray-300 whitespace-pre-line leading-relaxed">
                  {job.qualification}
                </div>
              </div>
            )}

            {job.selectionProcess && (
              <div className="pt-4 border-t border-gray-800 space-y-2">
                <h4 className="text-sm font-semibold text-white">Selection Process</h4>
                <div className="text-sm text-gray-300 whitespace-pre-line leading-relaxed">
                  {job.selectionProcess}
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Right 1 Column: Match Evaluation & Preparation Roadmap */}
        <div className="space-y-6">
          {/* Skill & Match Evaluation */}
          {match && (
            <div className="card p-6 border-blue-500/30 bg-gradient-to-b from-blue-950/40 to-gray-900/80 space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
                  <Sparkles className="w-4 h-4 text-amber-400" />
                  Your Profile Match
                </h3>
                <span className="text-2xl font-black text-blue-400">{match.matchPercentage}%</span>
              </div>

              {/* Progress Bar */}
              <div className="w-full bg-gray-800 rounded-full h-2 overflow-hidden">
                <div
                  className={`h-2 rounded-full transition-all duration-500 ${
                    match.matchPercentage >= 75
                      ? 'bg-emerald-500'
                      : match.matchPercentage >= 50
                      ? 'bg-blue-500'
                      : 'bg-amber-500'
                  }`}
                  style={{ width: `${match.matchPercentage}%` }}
                ></div>
              </div>

              <p className="text-xs text-gray-300 leading-normal">{match.summary}</p>

              {/* Matched Skills */}
              {match.matchedSkills.length > 0 && (
                <div className="space-y-1.5 pt-2">
                  <span className="text-xs font-semibold text-emerald-400 flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5" /> Matched Skills ({match.matchedSkills.length})
                  </span>
                  <div className="flex flex-wrap gap-1">
                    {match.matchedSkills.map((s, i) => (
                      <span key={i} className="text-[11px] px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-300 border border-emerald-500/20">
                        {s}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {/* Missing Skills */}
              {match.missingSkills.length > 0 && (
                <div className="space-y-1.5 pt-2">
                  <span className="text-xs font-semibold text-amber-400 flex items-center gap-1">
                    <AlertCircle className="w-3.5 h-3.5" /> Skills to Strengthen ({match.missingSkills.length})
                  </span>
                  <div className="flex flex-wrap gap-1">
                    {match.missingSkills.map((s, i) => (
                      <span key={i} className="text-[11px] px-2 py-0.5 rounded bg-amber-500/10 text-amber-300 border border-amber-500/20">
                        {s}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Integrated Preparation Roadmap */}
          {roadmap && (
            <div className="card p-6 border-gray-800 bg-gray-900/80 space-y-4">
              <h3 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
                <BookOpen className="w-4 h-4 text-purple-400" />
                Targeted Preparation Plan
              </h3>

              {/* Recommended Courses */}
              {roadmap.recommendedCourses.length > 0 && (
                <div className="space-y-2">
                  <span className="text-xs font-semibold text-gray-400">Core Course Topics:</span>
                  <div className="space-y-2">
                    {roadmap.recommendedCourses.map((c) => (
                      <Link
                        key={c.id}
                        to={`/student/courses/${c.slug}`}
                        className="p-2.5 rounded-lg bg-gray-800/80 hover:bg-gray-800 border border-gray-700/60 flex items-center justify-between text-xs text-gray-200 group transition-all"
                      >
                        <span className="font-medium group-hover:text-blue-400">{c.title}</span>
                        <span className="text-[10px] px-2 py-0.5 rounded bg-gray-900 text-gray-400">{c.difficulty}</span>
                      </Link>
                    ))}
                  </div>
                </div>
              )}

              {/* Recommended DSA */}
              {roadmap.recommendedDsaProblems.length > 0 && (
                <div className="space-y-2 pt-2 border-t border-gray-800">
                  <span className="text-xs font-semibold text-gray-400">DSA Practice Problems:</span>
                  <div className="space-y-2">
                    {roadmap.recommendedDsaProblems.map((p) => (
                      <Link
                        key={p.id}
                        to={`/student/dsa/problems/${p.id}`}
                        className="p-2.5 rounded-lg bg-gray-800/80 hover:bg-gray-800 border border-gray-700/60 flex items-center justify-between text-xs text-gray-200 group transition-all"
                      >
                        <span className="font-medium group-hover:text-blue-400">{p.title}</span>
                        <span className="text-[10px] px-2 py-0.5 rounded bg-blue-900/40 text-blue-300">{p.difficulty}</span>
                      </Link>
                    ))}
                  </div>
                </div>
              )}

              {/* Recommended Interview Questions */}
              {roadmap.recommendedInterviewQuestions.length > 0 && (
                <div className="space-y-2 pt-2 border-t border-gray-800">
                  <span className="text-xs font-semibold text-gray-400">Company Interview Questions:</span>
                  <div className="space-y-2">
                    {roadmap.recommendedInterviewQuestions.map((q) => (
                      <Link
                        key={q.id}
                        to={`/student/interview/practice/${q.id}`}
                        className="p-2.5 rounded-lg bg-gray-800/80 hover:bg-gray-800 border border-gray-700/60 block text-xs text-gray-200 group transition-all"
                      >
                        <p className="line-clamp-1 font-medium group-hover:text-blue-400">{q.questionText}</p>
                        <span className="text-[10px] text-gray-500 mt-1 block">{q.round} • {q.difficulty}</span>
                      </Link>
                    ))}
                  </div>
                </div>
              )}

              {/* Simulated Mock Interview CTA */}
              <div className="pt-2 border-t border-gray-800">
                <Link
                  to="/student/interview/mock/setup"
                  className="btn btn-secondary w-full text-xs flex items-center justify-center gap-2 text-indigo-300 border-indigo-500/30 hover:bg-indigo-500/10"
                >
                  <MessageSquare className="w-3.5 h-3.5" />
                  Launch Simulated Mock Interview
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Track Application Modal */}
      {isApplying && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fade-in">
          <div className="card p-6 border-gray-700 bg-gray-900 max-w-md w-full space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-white">Track Application Progress</h3>
              <button
                onClick={() => setIsApplying(false)}
                className="text-gray-400 hover:text-white text-sm"
              >
                ✕
              </button>
            </div>

            {appSuccess ? (
              <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-sm flex items-center gap-2">
                <CheckCircle2 className="w-5 h-5" /> Application added to your career tracker!
              </div>
            ) : (
              <form onSubmit={handleTrackApplication} className="space-y-4">
                <div>
                  <label className="text-xs font-semibold text-gray-300 block mb-1.5">Application Status</label>
                  <select
                    value={appStatus}
                    onChange={(e) => setAppStatus(e.target.value as ApplicationStatus)}
                    className="input w-full bg-gray-800 border-gray-700 text-sm text-white"
                  >
                    <option value="APPLIED">Applied</option>
                    <option value="ASSESSMENT">Assessment Scheduled</option>
                    <option value="INTERVIEW">Interview Round</option>
                    <option value="OFFER">Offer Received</option>
                    <option value="REJECTED">Rejected</option>
                    <option value="SAVED">Saved / Draft</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-semibold text-gray-300 block mb-1.5">Notes & Reference</label>
                  <textarea
                    rows={3}
                    placeholder="e.g. Applied via TCS iBegin with Resume v3. Awaiting test invite."
                    value={appNotes}
                    onChange={(e) => setAppNotes(e.target.value)}
                    className="input w-full bg-gray-800 border-gray-700 text-sm text-white"
                  ></textarea>
                </div>

                <div className="flex items-center justify-end gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setIsApplying(false)}
                    className="btn btn-secondary text-xs px-4"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={submittingApp}
                    className="btn btn-primary text-xs px-5 flex items-center gap-1.5"
                  >
                    {submittingApp ? 'Saving...' : 'Save to Tracker'}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
export default JobDetailPage;
