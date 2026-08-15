import React, { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { resumeApi, type ResumeAnalysisDto } from '../../api/resume';
import { jobsApi } from '../../api/jobs';
import toast from 'react-hot-toast';
import {
  FileText, Upload, Sparkles, AlertCircle, CheckCircle2, XCircle,
  HelpCircle, BookOpen, Code2, Briefcase, ArrowRight, RefreshCw,
  Award, TrendingUp, Layers, Check, ChevronRight
} from 'lucide-react';

export function ResumeAnalyzerPage() {
  const [selectedVersionId, setSelectedVersionId] = useState<number | undefined>();
  const [selectedJobId, setSelectedJobId] = useState<number | undefined>();
  const [customJdText, setCustomJdText] = useState('');
  const [customResumeText, setCustomResumeText] = useState('');
  const [inputMode, setInputMode] = useState<'VERSION' | 'UPLOAD' | 'PASTE'>('VERSION');
  const [jdMode, setJdMode] = useState<'JOB_PORTAL' | 'CUSTOM'>('JOB_PORTAL');
  const [isUploading, setIsUploading] = useState(false);
  const [analysisResult, setAnalysisResult] = useState<ResumeAnalysisDto | null>(null);

  // Queries
  const { data: versions } = useQuery({
    queryKey: ['student-resume-versions'],
    queryFn: resumeApi.listVersions,
  });

  const { data: jobsData } = useQuery({
    queryKey: ['job-catalog-for-resume'],
    queryFn: () => jobsApi.searchJobs({ page: 0, size: 20 }),
  });

  // Upload handler
  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setIsUploading(true);
    try {
      const res = await resumeApi.uploadAndExtract(file);
      setCustomResumeText(res.extractedText);
      setInputMode('PASTE');
      toast.success('Resume parsed successfully');
    } catch (err) {
      toast.error('Failed to parse resume document');
    } finally {
      setIsUploading(false);
    }
  };

  // Analyze mutation
  const analyzeMutation = useMutation({
    mutationFn: resumeApi.analyzeResume,
    onSuccess: (data) => {
      setAnalysisResult(data);
      toast.success('ATS Analysis complete!');
    },
    onError: () => toast.error('Failed to analyze resume'),
  });

  const handleRunAnalysis = () => {
    if (inputMode === 'VERSION' && !selectedVersionId && versions && versions.length > 0) {
      setSelectedVersionId(versions[0].id);
    }

    const payload: any = {};
    if (inputMode === 'VERSION') {
      payload.versionId = selectedVersionId || (versions && versions[0]?.id);
    } else {
      payload.resumeText = customResumeText;
    }

    if (jdMode === 'JOB_PORTAL') {
      payload.jobId = selectedJobId || (jobsData?.content && jobsData.content[0]?.id);
    } else {
      payload.jobDescriptionText = customJdText;
    }

    analyzeMutation.mutate(payload);
  };

  return (
    <div className="space-y-6 animate-fade-in pb-16">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2.5">
            <Sparkles className="h-7 w-7 text-brand-600" />
            AI Resume Analyzer & ATS Scanner
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Compare your resume against real Job Descriptions, discover critical skill gaps, and get instant Academy preparation roadmaps.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Link to="/student/resume/builder" className="btn-secondary text-xs">
            Resume Builder
          </Link>
          <Link to="/student/resume/resumes" className="btn-primary text-xs">
            My Resumes
          </Link>
        </div>
      </div>

      {/* Input Configuration Card */}
      <div className="card p-6 space-y-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* LEFT: Resume Selection */}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wider">
                1. Select Resume Source
              </label>
              <div className="flex items-center gap-1 bg-slate-100 p-0.5 rounded-lg text-xs font-semibold">
                <button
                  onClick={() => setInputMode('VERSION')}
                  className={`px-2.5 py-1 rounded-md transition-colors ${
                    inputMode === 'VERSION' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'
                  }`}
                >
                  Saved Version
                </button>
                <button
                  onClick={() => setInputMode('UPLOAD')}
                  className={`px-2.5 py-1 rounded-md transition-colors ${
                    inputMode === 'UPLOAD' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'
                  }`}
                >
                  Upload File
                </button>
                <button
                  onClick={() => setInputMode('PASTE')}
                  className={`px-2.5 py-1 rounded-md transition-colors ${
                    inputMode === 'PASTE' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'
                  }`}
                >
                  Paste Text
                </button>
              </div>
            </div>

            {inputMode === 'VERSION' && (
              <div>
                <select
                  value={selectedVersionId}
                  onChange={(e) => setSelectedVersionId(Number(e.target.value))}
                  className="input text-xs"
                >
                  {versions?.map((v) => (
                    <option key={v.id} value={v.id}>
                      {v.versionTitle} (Score: {v.latestAtsScore || 'Not analyzed'})
                    </option>
                  )) || <option>No saved resumes found. Use upload or builder.</option>}
                </select>
              </div>
            )}

            {inputMode === 'UPLOAD' && (
              <div className="border-2 border-dashed border-slate-200 hover:border-brand-500 transition-colors rounded-2xl p-6 text-center cursor-pointer relative bg-slate-50/50">
                <input
                  type="file"
                  accept=".pdf,.docx,.txt,.md"
                  onChange={handleFileUpload}
                  className="absolute inset-0 opacity-0 cursor-pointer w-full h-full"
                />
                <Upload className="h-8 w-8 text-brand-600 mx-auto mb-2" />
                <p className="text-xs font-bold text-slate-800">Click or Drag & Drop Resume File</p>
                <p className="text-[11px] text-slate-400 mt-1">Supports PDF, DOCX, TXT (ATS Formatted)</p>
                {isUploading && <p className="text-xs text-brand-600 font-semibold mt-2">Parsing document...</p>}
              </div>
            )}

            {inputMode === 'PASTE' && (
              <textarea
                className="input min-h-[140px] text-xs font-mono"
                placeholder="Paste complete resume text here (Summary, Skills, Work History, Projects)..."
                value={customResumeText}
                onChange={(e) => setCustomResumeText(e.target.value)}
              />
            )}
          </div>

          {/* RIGHT: Job Description Selection */}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wider">
                2. Target Job Description
              </label>
              <div className="flex items-center gap-1 bg-slate-100 p-0.5 rounded-lg text-xs font-semibold">
                <button
                  onClick={() => setJdMode('JOB_PORTAL')}
                  className={`px-2.5 py-1 rounded-md transition-colors ${
                    jdMode === 'JOB_PORTAL' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'
                  }`}
                >
                  Job Portal Opening
                </button>
                <button
                  onClick={() => setJdMode('CUSTOM')}
                  className={`px-2.5 py-1 rounded-md transition-colors ${
                    jdMode === 'CUSTOM' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'
                  }`}
                >
                  Custom JD
                </button>
              </div>
            </div>

            {jdMode === 'JOB_PORTAL' && (
              <div>
                <select
                  value={selectedJobId}
                  onChange={(e) => setSelectedJobId(Number(e.target.value))}
                  className="input text-xs"
                >
                  {jobsData?.content.map((job) => (
                    <option key={job.id} value={job.id}>
                      {job.companyName} — {job.title} ({job.location})
                    </option>
                  )) || <option>Loading job openings...</option>}
                </select>
              </div>
            )}

            {jdMode === 'CUSTOM' && (
              <textarea
                className="input min-h-[140px] text-xs"
                placeholder="Paste the target job description, responsibilities, and required qualifications..."
                value={customJdText}
                onChange={(e) => setCustomJdText(e.target.value)}
              />
            )}
          </div>
        </div>

        <div className="flex justify-end pt-3 border-t border-slate-100">
          <button
            onClick={handleRunAnalysis}
            disabled={analyzeMutation.isPending}
            className="btn-primary flex items-center gap-2 text-xs"
          >
            {analyzeMutation.isPending ? (
              <>
                <RefreshCw className="h-4 w-4 animate-spin" />
                Analyzing ATS Compatibility...
              </>
            ) : (
              <>
                <Sparkles className="h-4 w-4" />
                Run In-Depth ATS Analysis
              </>
            )}
          </button>
        </div>
      </div>

      {/* ANALYSIS RESULTS DASHBOARD */}
      {analysisResult && (
        <div className="space-y-6 animate-fade-in">
          {/* Main Score & Component Metrics */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Overall ATS Gauge */}
            <div className="card p-6 bg-gradient-to-br from-slate-900 via-slate-800 to-indigo-950 text-white flex flex-col justify-between">
              <div>
                <span className="text-[11px] font-bold tracking-wider uppercase text-brand-300">
                  Overall ATS Compatibility
                </span>
                <div className="flex items-baseline gap-2 mt-2">
                  <span className="text-5xl font-black">{analysisResult.overallAtsScore}</span>
                  <span className="text-slate-400 text-lg font-bold">/ 100</span>
                </div>
                <p className="text-xs text-slate-300 mt-2 leading-relaxed">
                  {analysisResult.summaryFeedback}
                </p>
              </div>

              <div className="mt-4 pt-4 border-t border-white/10 flex items-center justify-between text-xs text-slate-400">
                <span>Target: {analysisResult.targetJobTitle}</span>
                <span>{analysisResult.targetCompanyName}</span>
              </div>
            </div>

            {/* Sub-scores Breakdown */}
            <div className="md:col-span-2 card p-6 space-y-4">
              <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                ATS Component Sub-Scores
              </h3>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                {[
                  { label: 'Keyword Match', score: analysisResult.keywordMatchScore, max: 25, color: 'bg-brand-500' },
                  { label: 'Technical Skills', score: analysisResult.skillsMatchScore, max: 20, color: 'bg-emerald-500' },
                  { label: 'Experience Match', score: analysisResult.experienceMatchScore, max: 15, color: 'bg-indigo-500' },
                  { label: 'Project Relevance', score: analysisResult.projectMatchScore, max: 15, color: 'bg-purple-500' },
                  { label: 'Education Match', score: analysisResult.educationMatchScore, max: 5, color: 'bg-amber-500' },
                  { label: 'Structure & Format', score: analysisResult.structureScore, max: 5, color: 'bg-cyan-500' },
                ].map((item) => (
                  <div key={item.label} className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                    <div className="flex justify-between text-xs font-semibold text-slate-600 mb-1">
                      <span>{item.label}</span>
                      <span className="font-bold text-slate-900">{item.score} / {item.max}</span>
                    </div>
                    <div className="w-full bg-slate-200 h-1.5 rounded-full overflow-hidden">
                      <div
                        className={`h-full ${item.color}`}
                        style={{ width: `${(item.score / item.max) * 100}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Keywords Match Matrix */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Matched Keywords */}
            <div className="card p-6 space-y-4">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="h-5 w-5 text-emerald-600" />
                <h3 className="text-sm font-bold text-slate-900">
                  Matched Technical Keywords ({analysisResult.matchedKeywords.length})
                </h3>
              </div>
              <div className="flex flex-wrap gap-2">
                {analysisResult.matchedKeywords.map((kw, i) => (
                  <span
                    key={i}
                    className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200"
                  >
                    <Check className="h-3.5 w-3.5" />
                    {kw.keywordName}
                    <span className="text-[10px] text-emerald-500 font-normal">({kw.occurrenceCount}x)</span>
                  </span>
                ))}
              </div>
            </div>

            {/* Missing Keywords */}
            <div className="card p-6 space-y-4">
              <div className="flex items-center gap-2">
                <XCircle className="h-5 w-5 text-red-600" />
                <h3 className="text-sm font-bold text-slate-900">
                  Missing Keywords from JD ({analysisResult.missingKeywords.length})
                </h3>
              </div>
              <div className="flex flex-wrap gap-2">
                {analysisResult.missingKeywords.map((kw, i) => (
                  <span
                    key={i}
                    className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold bg-red-50 text-red-700 border border-red-200"
                  >
                    ✕ {kw.keywordName}
                  </span>
                ))}
              </div>
            </div>
          </div>

          {/* Critical Missing Skills & Direct Academy Learning Roadmaps */}
          {analysisResult.criticalMissingSkills && analysisResult.criticalMissingSkills.length > 0 && (
            <div className="card p-6 space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
                    <BookOpen className="h-5 w-5 text-brand-600" />
                    Skill Gap Analysis & Immediate Academy Preparation
                  </h3>
                  <p className="text-xs text-slate-500 mt-0.5">
                    Targeted modules from VCUBE Academy directly mapped to address your resume gaps
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {analysisResult.criticalMissingSkills.map((gap, i) => (
                  <div key={i} className="p-4 rounded-2xl border border-slate-200 bg-slate-50/50 space-y-3 flex flex-col justify-between">
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="badge bg-red-100 text-red-800 font-bold">{gap.skillName}</span>
                        <span className="text-[10px] font-semibold text-slate-400 uppercase">{gap.category}</span>
                      </div>
                      <p className="text-xs text-slate-600 mt-2 leading-relaxed">{gap.whyItMatters}</p>
                    </div>

                    <div className="space-y-2 pt-2 border-t border-slate-200 text-xs">
                      {gap.recommendedCourse && (
                        <Link
                          to={`/student/courses/${gap.recommendedCourse.slug || gap.recommendedCourse.id}`}
                          className="flex items-center justify-between text-brand-600 hover:text-brand-700 font-semibold"
                        >
                          <span className="flex items-center gap-1.5">
                            <BookOpen className="h-3.5 w-3.5" />
                            Course: {gap.recommendedCourse.title}
                          </span>
                          <ChevronRight className="h-3.5 w-3.5" />
                        </Link>
                      )}

                      {gap.recommendedDsaProblem && (
                        <Link
                          to={`/student/dsa/${gap.recommendedDsaProblem.id}`}
                          className="flex items-center justify-between text-indigo-600 hover:text-indigo-700 font-semibold"
                        >
                          <span className="flex items-center gap-1.5">
                            <Code2 className="h-3.5 w-3.5" />
                            DSA: {gap.recommendedDsaProblem.title}
                          </span>
                          <ChevronRight className="h-3.5 w-3.5" />
                        </Link>
                      )}

                      {gap.recommendedInterviewQuestion && (
                        <Link
                          to="/student/interview/questions"
                          className="flex items-center justify-between text-purple-600 hover:text-purple-700 font-semibold"
                        >
                          <span className="flex items-center gap-1.5">
                            <HelpCircle className="h-3.5 w-3.5" />
                            Q&A: {gap.recommendedInterviewQuestion.questionText.slice(0, 30)}...
                          </span>
                          <ChevronRight className="h-3.5 w-3.5" />
                        </Link>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Quality Recommendations & Weakness Detector */}
          {analysisResult.recommendations && analysisResult.recommendations.length > 0 && (
            <div className="card p-6 space-y-4">
              <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
                <AlertCircle className="h-5 w-5 text-amber-600" />
                Resume Quality Polish & Actionable Fixes
              </h3>
              <div className="space-y-3">
                {analysisResult.recommendations.map((rec, i) => (
                  <div
                    key={i}
                    className={`p-4 rounded-xl border ${
                      rec.severity === 'CRITICAL'
                        ? 'border-red-200 bg-red-50/40'
                        : 'border-amber-200 bg-amber-50/40'
                    } space-y-1.5`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-bold text-slate-900">{rec.title}</span>
                      <span
                        className={`badge ${
                          rec.severity === 'CRITICAL' ? 'bg-red-100 text-red-800' : 'bg-amber-100 text-amber-800'
                        }`}
                      >
                        {rec.severity}
                      </span>
                    </div>
                    <p className="text-xs text-slate-600">{rec.message}</p>
                    {rec.actionableFix && (
                      <p className="text-xs font-semibold text-slate-800 bg-white/80 p-2 rounded-lg border border-slate-200/60 mt-1">
                        💡 Fix: {rec.actionableFix}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
