import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Briefcase,
  Search,
  MapPin,
  Building,
  Clock,
  DollarSign,
  Bookmark,
  BookmarkCheck,
  ChevronRight,
  Filter,
  CheckCircle2,
  ExternalLink,
  Sparkles,
} from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type { JobSummaryDto, JobCategoryDto, JobSkillDto, EmploymentType, ExperienceLevel, WorkMode } from '../../api/jobs';

export const JobSearchPage: React.FC = () => {
  const [jobs, setJobs] = useState<JobSummaryDto[]>([]);
  const [categories, setCategories] = useState<JobCategoryDto[]>([]);
  const [locations, setLocations] = useState<string[]>([]);
  const [skills, setSkills] = useState<JobSkillDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);

  // Filters
  const [keyword, setKeyword] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>();
  const [selectedLocation, setSelectedLocation] = useState<string>('');
  const [selectedEmpType, setSelectedEmpType] = useState<EmploymentType | undefined>();
  const [selectedExpLevel, setSelectedExpLevel] = useState<ExperienceLevel | undefined>();
  const [selectedWorkMode, setSelectedWorkMode] = useState<WorkMode | undefined>();
  const [selectedSkill, setSelectedSkill] = useState<number | undefined>();
  const [sortBy, setSortBy] = useState<string>('latest');
  const [page, setPage] = useState<number>(0);

  useEffect(() => {
    loadFiltersMetadata();
  }, []);

  useEffect(() => {
    fetchJobs();
  }, [page, selectedCategory, selectedLocation, selectedEmpType, selectedExpLevel, selectedWorkMode, selectedSkill, sortBy]);

  const loadFiltersMetadata = async () => {
    try {
      const [catRes, locRes, skillRes] = await Promise.all([
        jobsApi.getCategories(),
        jobsApi.getLocations(),
        jobsApi.getSkills(),
      ]);
      setCategories(catRes);
      setLocations(locRes);
      setSkills(skillRes);
    } catch (err) {
      console.error('Failed to load filter metadata:', err);
    }
  };

  const fetchJobs = async () => {
    try {
      setLoading(true);
      const res = await jobsApi.searchJobs({
        keyword: keyword || undefined,
        categoryId: selectedCategory,
        location: selectedLocation || undefined,
        employmentType: selectedEmpType,
        experienceLevel: selectedExpLevel,
        workMode: selectedWorkMode,
        skillId: selectedSkill,
        sortBy,
        page,
        size: 9,
      });
      setJobs(res.content);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    } catch (err) {
      console.error('Failed to fetch jobs:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    fetchJobs();
  };

  const toggleSaveJob = async (jobId: number, isCurrentlySaved?: boolean) => {
    try {
      if (isCurrentlySaved) {
        await jobsApi.unsaveJob(jobId);
      } else {
        await jobsApi.saveJob(jobId);
      }
      setJobs((prev) =>
        prev.map((j) => (j.id === jobId ? { ...j, isSaved: !isCurrentlySaved } : j))
      );
    } catch (err) {
      console.error('Failed to toggle save status:', err);
    }
  };

  const resetFilters = () => {
    setKeyword('');
    setSelectedCategory(undefined);
    setSelectedLocation('');
    setSelectedEmpType(undefined);
    setSelectedExpLevel(undefined);
    setSelectedWorkMode(undefined);
    setSelectedSkill(undefined);
    setSortBy('latest');
    setPage(0);
  };

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header Banner */}
      <div className="card bg-gradient-to-r from-blue-900/40 via-indigo-900/30 to-purple-900/20 border-blue-500/20 p-8 rounded-2xl relative overflow-hidden">
        <div className="relative z-10 max-w-3xl space-y-3">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-500/10 border border-blue-500/30 text-blue-400 text-xs font-semibold uppercase tracking-wider">
            <Briefcase className="w-3.5 h-3.5" />
            VCUBE Placement & Career Network
          </div>
          <h1 className="text-3xl font-bold text-white tracking-tight">
            Verified Java & Full Stack Opportunities
          </h1>
          <p className="text-gray-400 text-sm leading-relaxed">
            Discover verified campus and off-campus roles at top tier-1 product and tech enterprises.
            Get personalized skill gap analysis and direct preparation roadmaps tailored to each job description.
          </p>

          <div className="flex flex-wrap gap-4 pt-2">
            <Link
              to="/student/job-recommendations"
              className="btn btn-primary inline-flex items-center gap-2 text-sm shadow-lg shadow-blue-500/20"
            >
              <Sparkles className="w-4 h-4 text-amber-400" />
              View Recommended Jobs for You
            </Link>
            <Link
              to="/student/placements"
              className="btn btn-secondary inline-flex items-center gap-2 text-sm"
            >
              <Building className="w-4 h-4 text-blue-400" />
              Active Placement Drives
            </Link>
          </div>
        </div>
      </div>

      {/* Search & Filter Bar */}
      <div className="card p-6 border-gray-800 space-y-4">
        <form onSubmit={handleSearchSubmit} className="flex flex-col md:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search by role, company, keywords (e.g. Java, Spring Boot, AWS, TCS)..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="input pl-11 w-full bg-gray-900/80 border-gray-700 text-white placeholder-gray-500"
            />
          </div>
          <button type="submit" className="btn btn-primary px-6 flex items-center justify-center gap-2">
            <Search className="w-4 h-4" />
            Search Jobs
          </button>
        </form>

        {/* Dropdown Filters */}
        <div className="grid grid-cols-2 md:grid-cols-6 gap-3 pt-2">
          <select
            value={selectedCategory || ''}
            onChange={(e) => {
              setSelectedCategory(e.target.value ? Number(e.target.value) : undefined);
              setPage(0);
            }}
            className="input bg-gray-900 border-gray-700 text-xs text-gray-200"
          >
            <option value="">All Categories</option>
            {categories.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>

          <select
            value={selectedLocation}
            onChange={(e) => {
              setSelectedLocation(e.target.value);
              setPage(0);
            }}
            className="input bg-gray-900 border-gray-700 text-xs text-gray-200"
          >
            <option value="">All Locations</option>
            {locations.map((loc) => (
              <option key={loc} value={loc}>
                {loc}
              </option>
            ))}
          </select>

          <select
            value={selectedWorkMode || ''}
            onChange={(e) => {
              setSelectedWorkMode((e.target.value as WorkMode) || undefined);
              setPage(0);
            }}
            className="input bg-gray-900 border-gray-700 text-xs text-gray-200"
          >
            <option value="">Work Mode</option>
            <option value="ONSITE">Onsite</option>
            <option value="HYBRID">Hybrid</option>
            <option value="REMOTE">Remote</option>
          </select>

          <select
            value={selectedExpLevel || ''}
            onChange={(e) => {
              setSelectedExpLevel((e.target.value as ExperienceLevel) || undefined);
              setPage(0);
            }}
            className="input bg-gray-900 border-gray-700 text-xs text-gray-200"
          >
            <option value="">Experience</option>
            <option value="FRESHER">Fresher (0 yrs)</option>
            <option value="ENTRY_LEVEL">0-2 Years</option>
            <option value="MID_LEVEL">2-5 Years</option>
            <option value="SENIOR">5+ Years</option>
          </select>

          <select
            value={sortBy}
            onChange={(e) => {
              setSortBy(e.target.value);
              setPage(0);
            }}
            className="input bg-gray-900 border-gray-700 text-xs text-gray-200"
          >
            <option value="latest">Latest Posted</option>
            <option value="salary">Highest Package</option>
            <option value="deadline">Expiring Soon</option>
          </select>

          <button
            onClick={resetFilters}
            type="button"
            className="btn btn-secondary text-xs flex items-center justify-center gap-1 text-gray-400 hover:text-white"
          >
            <Filter className="w-3.5 h-3.5" />
            Reset
          </button>
        </div>
      </div>

      {/* Results Header */}
      <div className="flex items-center justify-between text-sm text-gray-400">
        <span>
          Showing <strong className="text-white">{jobs.length}</strong> of{' '}
          <strong className="text-white">{totalElements}</strong> open positions
        </span>
        <span className="text-xs">Page {page + 1} of {Math.max(1, totalPages)}</span>
      </div>

      {/* Jobs Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[1, 2, 3, 4, 5, 6].map((n) => (
            <div key={n} className="card p-6 border-gray-800 animate-pulse space-y-4">
              <div className="h-6 bg-gray-800 rounded w-3/4"></div>
              <div className="h-4 bg-gray-800 rounded w-1/2"></div>
              <div className="h-16 bg-gray-800 rounded"></div>
            </div>
          ))}
        </div>
      ) : jobs.length === 0 ? (
        <div className="card p-12 text-center border-gray-800 space-y-3">
          <Briefcase className="w-12 h-12 text-gray-600 mx-auto" />
          <h3 className="text-lg font-medium text-white">No job openings found</h3>
          <p className="text-sm text-gray-400 max-w-sm mx-auto">
            Try adjusting your search keywords or resetting active filters.
          </p>
          <button onClick={resetFilters} className="btn btn-secondary text-sm mt-2">
            Clear Filters
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {jobs.map((job) => (
            <div
              key={job.id}
              className="card border-gray-800 hover:border-blue-500/40 transition-all p-6 flex flex-col justify-between group relative bg-gray-900/60 hover:shadow-xl hover:shadow-blue-500/5"
            >
              <div className="space-y-4">
                {/* Header: Company & Bookmark */}
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-center gap-3">
                    <div className="w-11 h-11 rounded-xl bg-gray-800 border border-gray-700 flex items-center justify-center font-bold text-blue-400 text-lg">
                      {job.companyName.charAt(0)}
                    </div>
                    <div>
                      <h4 className="text-sm font-semibold text-white group-hover:text-blue-400 transition-colors">
                        {job.companyName}
                      </h4>
                      <span className="text-xs text-gray-400">{job.location}</span>
                    </div>
                  </div>
                  <button
                    onClick={() => toggleSaveJob(job.id, job.isSaved)}
                    className="p-1.5 rounded-lg text-gray-400 hover:text-blue-400 hover:bg-blue-500/10 transition-colors"
                    title={job.isSaved ? 'Remove Bookmark' : 'Save Job'}
                  >
                    {job.isSaved ? (
                      <BookmarkCheck className="w-5 h-5 text-blue-400" />
                    ) : (
                      <Bookmark className="w-5 h-5" />
                    )}
                  </button>
                </div>

                {/* Job Title */}
                <div>
                  <h3 className="text-base font-bold text-white line-clamp-1">
                    {job.title}
                  </h3>
                  <div className="flex flex-wrap items-center gap-2 pt-2 text-xs text-gray-400">
                    <span className="px-2 py-0.5 rounded bg-gray-800 border border-gray-700">
                      {job.workMode}
                    </span>
                    <span className="px-2 py-0.5 rounded bg-gray-800 border border-gray-700">
                      {job.experienceLevel}
                    </span>
                    {job.salaryText && (
                      <span className="px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-medium">
                        {job.salaryText}
                      </span>
                    )}
                  </div>
                </div>

                {/* Skills tags */}
                <div className="flex flex-wrap gap-1.5 pt-1">
                  {job.skills.slice(0, 4).map((sk) => (
                    <span
                      key={sk.id}
                      className="text-[11px] px-2 py-0.5 rounded-full bg-blue-900/30 text-blue-300 border border-blue-800/40"
                    >
                      {sk.name}
                    </span>
                  ))}
                  {job.skills.length > 4 && (
                    <span className="text-[11px] px-1.5 py-0.5 rounded-full bg-gray-800 text-gray-400">
                      +{job.skills.length - 4}
                    </span>
                  )}
                </div>
              </div>

              {/* Footer Actions */}
              <div className="pt-5 mt-4 border-t border-gray-800/80 flex items-center justify-between">
                <div className="flex items-center gap-1.5 text-xs text-gray-500">
                  <Clock className="w-3.5 h-3.5" />
                  <span>
                    {job.applicationDeadline
                      ? `Deadline: ${new Date(job.applicationDeadline).toLocaleDateString()}`
                      : 'Actively Hiring'}
                  </span>
                </div>
                <Link
                  to={`/student/jobs/${job.id}`}
                  className="inline-flex items-center gap-1 text-xs font-semibold text-blue-400 group-hover:text-blue-300 hover:underline"
                >
                  View Details & Match
                  <ChevronRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination Controls */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-2 pt-4">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            className="btn btn-secondary text-xs px-4 py-2 disabled:opacity-40"
          >
            Previous
          </button>
          <span className="text-xs text-gray-400 px-3">
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            className="btn btn-secondary text-xs px-4 py-2 disabled:opacity-40"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};
export default JobSearchPage;
