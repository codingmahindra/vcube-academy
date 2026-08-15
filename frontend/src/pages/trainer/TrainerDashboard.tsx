import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuth } from '../../hooks/useAuth';
import { trainerApi, type QuestionAdmin, type QuestionRequestData } from '../../api/trainer';
import { coursesApi } from '../../api/courses';
import { topicsApi } from '../../api/topics';
import { dsaApi, type DsaProblemSummary, type DsaCategory } from '../../api/dsa';
import { interviewApi } from '../../api/interview';
import { jobsApi } from '../../api/jobs';
import toast from 'react-hot-toast';
import {
  Users, BookOpen, Brain, Clock, Plus, Trash2, Edit3,
  CheckCircle2, XCircle, ChevronRight, BarChart2, Layers,
  FileCode2, Loader2, Sparkles, Check, AlertCircle, Code2, HelpCircle, Briefcase, ExternalLink
} from 'lucide-react';

export function TrainerDashboard() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState<'overview' | 'courses' | 'topics' | 'questions' | 'dsa' | 'interview' | 'jobs'>('overview');

  // Queries
  const { data: dashboard, isLoading: dashLoading } = useQuery({
    queryKey: ['trainer-dashboard'],
    queryFn: trainerApi.getDashboard,
  });

  const { data: interviewQuestions, isLoading: interviewLoading } = useQuery({
    queryKey: ['trainer-interview-questions'],
    queryFn: () => interviewApi.searchQuestions({ size: 100 }),
  });

  const { data: courses, isLoading: coursesLoading } = useQuery({
    queryKey: ['courses'],
    queryFn: () => coursesApi.getAll(),
  });

  const { data: questions, isLoading: questionsLoading } = useQuery({
    queryKey: ['trainer-questions'],
    queryFn: () => trainerApi.getQuestions(),
  });

  const { data: dsaProblemsData, isLoading: dsaLoading } = useQuery({
    queryKey: ['trainer-dsa-problems'],
    queryFn: () => dsaApi.getProblems({ size: 100 }),
  });

  const { data: dsaCategories } = useQuery({
    queryKey: ['trainer-dsa-categories'],
    queryFn: () => dsaApi.getCategories(),
  });

  const { data: jobsData, isLoading: jobsLoading } = useQuery({
    queryKey: ['trainer-jobs'],
    queryFn: () => jobsApi.searchJobs({ size: 100 }),
  });

  const { data: companiesData } = useQuery({
    queryKey: ['trainer-companies'],
    queryFn: () => jobsApi.getCompanies(),
  });

  // Modals / forms state
  const [showCourseModal, setShowCourseModal] = useState(false);
  const [newCourse, setNewCourse] = useState({ title: '', slug: '', description: '', difficulty: 'BEGINNER', isPublished: true });

  const [showJobModal, setShowJobModal] = useState(false);
  const [newJob, setNewJob] = useState({
    companyId: 1,
    title: '',
    description: '',
    location: 'Hyderabad, India',
    employmentType: 'FULL_TIME',
    experienceLevel: 'FRESHER',
    workMode: 'HYBRID',
    salaryText: '4.0 - 7.0 LPA',
    sourceUrl: '',
    responsibilities: '',
    qualification: 'BE / B.Tech / MCA (60%+)',
  });

  const createJobMutation = useMutation({
    mutationFn: (data: any) => jobsApi.createJob(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trainer-jobs'] });
      setShowJobModal(false);
      setNewJob({
        companyId: 1,
        title: '',
        description: '',
        location: 'Hyderabad, India',
        employmentType: 'FULL_TIME',
        experienceLevel: 'FRESHER',
        workMode: 'HYBRID',
        salaryText: '4.0 - 7.0 LPA',
        sourceUrl: '',
        responsibilities: '',
        qualification: 'BE / B.Tech / MCA (60%+)',
      });
      toast.success('Job opportunity published successfully!');
    },
    onError: () => toast.error('Failed to post job opportunity'),
  });

  const [showQuestionModal, setShowQuestionModal] = useState(false);
  const [newQuestion, setNewQuestion] = useState<QuestionRequestData>({
    questionText: '',
    difficulty: 'MEDIUM',
    explanation: '',
    interviewPoint: '',
    options: [
      { optionLabel: 'A', optionText: '', isCorrect: true, whyWrong: '' },
      { optionLabel: 'B', optionText: '', isCorrect: false, whyWrong: '' },
      { optionLabel: 'C', optionText: '', isCorrect: false, whyWrong: '' },
      { optionLabel: 'D', optionText: '', isCorrect: false, whyWrong: '' },
    ],
  });

  const [showDsaModal, setShowDsaModal] = useState(false);
  const [newDsa, setNewDsa] = useState({
    title: '',
    categoryId: 1,
    difficulty: 'EASY',
    subtopic: '',
    description: '',
    timeComplexity: 'O(N)',
    spaceComplexity: 'O(1)',
    javaStarterCode: 'public class Solution {\n    // Write your solution here\n}',
    sampleInput: '',
    sampleOutput: '',
  });

  // Mutations
  const createCourseMutation = useMutation({
    mutationFn: trainerApi.createCourse,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['courses'] });
      queryClient.invalidateQueries({ queryKey: ['trainer-dashboard'] });
      setShowCourseModal(false);
      setNewCourse({ title: '', slug: '', description: '', difficulty: 'BEGINNER', isPublished: true });
      toast.success('Course created successfully!');
    },
    onError: () => toast.error('Failed to create course'),
  });

  const createDsaMutation = useMutation({
    mutationFn: (data: any) => dsaApi.createProblem(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trainer-dsa-problems'] });
      setShowDsaModal(false);
      setNewDsa({
        title: '',
        categoryId: dsaCategories?.[0]?.id ?? 1,
        difficulty: 'EASY',
        subtopic: '',
        description: '',
        timeComplexity: 'O(N)',
        spaceComplexity: 'O(1)',
        javaStarterCode: 'public class Solution {\n    // Write your solution here\n}',
        sampleInput: '',
        sampleOutput: '',
      });
      toast.success('DSA Problem created successfully!');
    },
    onError: () => toast.error('Failed to create DSA problem'),
  });

  const deleteDsaMutation = useMutation({
    mutationFn: (id: number) => dsaApi.deleteProblem(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trainer-dsa-problems'] });
      toast.success('DSA Problem deleted');
    },
    onError: () => toast.error('Failed to delete DSA problem'),
  });

  const createQuestionMutation = useMutation({
    mutationFn: trainerApi.createQuestion,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trainer-questions'] });
      queryClient.invalidateQueries({ queryKey: ['trainer-dashboard'] });
      setShowQuestionModal(false);
      setNewQuestion({
        questionText: '',
        difficulty: 'MEDIUM',
        explanation: '',
        interviewPoint: '',
        options: [
          { optionLabel: 'A', optionText: '', isCorrect: true, whyWrong: '' },
          { optionLabel: 'B', optionText: '', isCorrect: false, whyWrong: '' },
          { optionLabel: 'C', optionText: '', isCorrect: false, whyWrong: '' },
          { optionLabel: 'D', optionText: '', isCorrect: false, whyWrong: '' },
        ],
      });
      toast.success('Question added to question bank!');
    },
    onError: () => toast.error('Failed to create question'),
  });

  const deleteQuestionMutation = useMutation({
    mutationFn: trainerApi.deleteQuestion,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trainer-questions'] });
      queryClient.invalidateQueries({ queryKey: ['trainer-dashboard'] });
      toast.success('Question removed');
    },
    onError: () => toast.error('Failed to delete question'),
  });

  const deleteCourseMutation = useMutation({
    mutationFn: trainerApi.deleteCourse,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['courses'] });
      queryClient.invalidateQueries({ queryKey: ['trainer-dashboard'] });
      toast.success('Course deleted');
    },
    onError: () => toast.error('Failed to delete course'),
  });

  return (
    <div className="space-y-6 animate-fade-in pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Trainer Dashboard</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Manage VCUBE Academy curriculum, student progress, and MCQ test bank.
          </p>
        </div>
        <div className="flex items-center gap-2 text-xs text-slate-400">
          <Clock className="h-3.5 w-3.5" />
          {new Date().toLocaleDateString('en-IN', { weekday: 'long', day: 'numeric', month: 'long' })}
        </div>
      </div>

      {/* Navigation tabs */}
      <div className="flex border-b border-slate-200 overflow-x-auto gap-2">
        {[
          { id: 'overview', label: 'Overview & Performance', icon: BarChart2 },
          { id: 'courses', label: 'Courses & Curriculum', icon: BookOpen },
          { id: 'questions', label: 'MCQ Question Bank', icon: Brain },
          { id: 'dsa', label: 'DSA Problem Bank', icon: Code2 },
          { id: 'interview', label: 'Interview Q&A Bank', icon: HelpCircle },
          { id: 'jobs', label: 'Job & Placement Postings', icon: Briefcase },
        ].map((tab) => {
          const Icon = tab.icon;
          const active = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`flex items-center gap-2 px-4 py-3 text-sm font-semibold border-b-2 transition-colors whitespace-nowrap ${
                active
                  ? 'border-brand-600 text-brand-700 bg-brand-50/50'
                  : 'border-transparent text-slate-500 hover:text-slate-700 hover:bg-slate-50'
              }`}
            >
              <Icon className="h-4 w-4" />
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* TAB: OVERVIEW */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          {/* Top Metrics Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="card p-5 border-l-4 border-l-brand-500">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs font-semibold text-slate-400 uppercase">Enrolled Students</p>
                  <p className="text-2xl font-bold text-slate-900 mt-1">{dashboard?.totalStudents ?? 0}</p>
                </div>
                <div className="h-10 w-10 rounded-xl bg-brand-50 flex items-center justify-center text-brand-600">
                  <Users className="h-5 w-5" />
                </div>
              </div>
            </div>

            <div className="card p-5 border-l-4 border-l-emerald-500">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs font-semibold text-slate-400 uppercase">Active Courses</p>
                  <p className="text-2xl font-bold text-slate-900 mt-1">{dashboard?.totalCourses ?? 0}</p>
                </div>
                <div className="h-10 w-10 rounded-xl bg-emerald-50 flex items-center justify-center text-emerald-600">
                  <BookOpen className="h-5 w-5" />
                </div>
              </div>
            </div>

            <div className="card p-5 border-l-4 border-l-violet-500">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs font-semibold text-slate-400 uppercase">Total Questions</p>
                  <p className="text-2xl font-bold text-slate-900 mt-1">{dashboard?.totalQuestions ?? 0}</p>
                </div>
                <div className="h-10 w-10 rounded-xl bg-violet-50 flex items-center justify-center text-violet-600">
                  <Brain className="h-5 w-5" />
                </div>
              </div>
            </div>

            <div className="card p-5 border-l-4 border-l-amber-500">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs font-semibold text-slate-400 uppercase">Quiz Submissions</p>
                  <p className="text-2xl font-bold text-slate-900 mt-1">{dashboard?.totalAttempts ?? 0}</p>
                </div>
                <div className="h-10 w-10 rounded-xl bg-amber-50 flex items-center justify-center text-amber-600">
                  <Sparkles className="h-5 w-5" />
                </div>
              </div>
            </div>
          </div>

          {/* Student Progress Overview */}
          <div className="card p-6">
            <h2 className="text-base font-bold text-slate-900 mb-4">Student Course Progress</h2>
            {dashboard?.studentProgress && dashboard.studentProgress.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                  <thead>
                    <tr className="border-b border-slate-100 text-xs uppercase font-semibold text-slate-400">
                      <th className="py-3 px-2">Student</th>
                      <th className="py-3 px-2">Course</th>
                      <th className="py-3 px-2">Completed Topics</th>
                      <th className="py-3 px-2">Completion Rate</th>
                      <th className="py-3 px-2">Quiz Accuracy</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {dashboard.studentProgress.map((p) => (
                      <tr key={p.id} className="hover:bg-slate-50">
                        <td className="py-3 px-2 font-medium text-slate-800">{p.studentName}</td>
                        <td className="py-3 px-2 text-slate-600">{p.courseTitle}</td>
                        <td className="py-3 px-2 text-slate-600">{p.completedTopicsCount} topics</td>
                        <td className="py-3 px-2">
                          <div className="flex items-center gap-2">
                            <div className="w-24 bg-slate-100 rounded-full h-2 overflow-hidden">
                              <div className="bg-brand-500 h-2 rounded-full" style={{ width: `${p.completionPercentage}%` }} />
                            </div>
                            <span className="text-xs font-semibold text-slate-700">{p.completionPercentage}%</span>
                          </div>
                        </td>
                        <td className="py-3 px-2 font-semibold text-emerald-600">{p.quizAverageScore}%</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="text-sm text-slate-400 py-6 text-center">No student progress recorded yet.</p>
            )}
          </div>

          {/* Recent Quiz Attempts */}
          <div className="card p-6">
            <h2 className="text-base font-bold text-slate-900 mb-4">Recent Quiz Submissions</h2>
            {dashboard?.recentResults && dashboard.recentResults.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                  <thead>
                    <tr className="border-b border-slate-100 text-xs uppercase font-semibold text-slate-400">
                      <th className="py-3 px-2">Student</th>
                      <th className="py-3 px-2">Score</th>
                      <th className="py-3 px-2">Percentage</th>
                      <th className="py-3 px-2">Status</th>
                      <th className="py-3 px-2">Date</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {dashboard.recentResults.map((r) => (
                      <tr key={r.id} className="hover:bg-slate-50">
                        <td className="py-3 px-2">
                          <p className="font-medium text-slate-800">{r.studentName}</p>
                          <p className="text-xs text-slate-400">{r.studentEmail}</p>
                        </td>
                        <td className="py-3 px-2 text-slate-600">{r.correctAnswers} / {r.totalQuestions}</td>
                        <td className="py-3 px-2 font-bold text-slate-800">{r.scorePercentage}%</td>
                        <td className="py-3 px-2">
                          {r.passed ? (
                            <span className="badge bg-emerald-50 text-emerald-700 font-semibold">Passed</span>
                          ) : (
                            <span className="badge bg-red-50 text-red-700 font-semibold">Needs Review</span>
                          )}
                        </td>
                        <td className="py-3 px-2 text-xs text-slate-400">
                          {new Date(r.createdAt).toLocaleDateString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="text-sm text-slate-400 py-6 text-center">No recent quiz attempts found.</p>
            )}
          </div>
        </div>
      )}

      {/* TAB: COURSES */}
      {activeTab === 'courses' && (
        <div className="space-y-6">
          <div className="flex justify-between items-center">
            <h2 className="text-lg font-bold text-slate-900">Manage Courses & Curricula</h2>
            <button
              onClick={() => setShowCourseModal(true)}
              className="btn-primary flex items-center gap-2"
            >
              <Plus className="h-4 w-4" /> Add New Course
            </button>
          </div>

          {coursesLoading ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin text-brand-600" />
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {courses?.map((c) => (
                <div key={c.id} className="card flex flex-col justify-between p-5">
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <span className="badge bg-brand-50 text-brand-700 font-medium">
                        {c.difficulty}
                      </span>
                      {c.isPublished ? (
                        <span className="badge bg-emerald-50 text-emerald-700 font-medium">Published</span>
                      ) : (
                        <span className="badge bg-slate-100 text-slate-600 font-medium">Draft</span>
                      )}
                    </div>
                    <h3 className="font-bold text-slate-900 text-base">{c.title}</h3>
                    <p className="text-xs text-slate-500 mt-1 line-clamp-2">{c.description}</p>
                    <div className="mt-3 flex items-center gap-4 text-xs text-slate-500">
                      <span>{c.moduleCount ?? 0} Modules</span>
                      <span>•</span>
                      <span>{c.topicCount ?? 0} Topics</span>
                    </div>
                  </div>

                  <div className="mt-5 pt-3 border-t border-slate-100 flex items-center justify-between">
                    <span className="text-xs text-slate-400 font-mono">slug: {c.slug}</span>
                    <button
                      onClick={() => {
                        if (confirm(`Delete course "${c.title}"?`)) {
                          deleteCourseMutation.mutate(c.id);
                        }
                      }}
                      className="p-1.5 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                      title="Delete Course"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB: QUESTIONS */}
      {activeTab === 'questions' && (
        <div className="space-y-6">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-lg font-bold text-slate-900">MCQ Test Bank</h2>
              <p className="text-xs text-slate-500">Manage interactive multiple-choice questions & explanations</p>
            </div>
            <button
              onClick={() => setShowQuestionModal(true)}
              className="btn-primary flex items-center gap-2"
            >
              <Plus className="h-4 w-4" /> Add Question
            </button>
          </div>

          {questionsLoading ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin text-brand-600" />
            </div>
          ) : (
            <div className="space-y-4">
              {questions?.map((q, idx) => (
                <div key={q.id} className="card p-5 space-y-3">
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-bold text-slate-400">Q{idx + 1}.</span>
                      <span className={`badge ${
                        q.difficulty === 'EASY' ? 'bg-emerald-50 text-emerald-700' :
                        q.difficulty === 'HARD' ? 'bg-red-50 text-red-700' :
                        'bg-amber-50 text-amber-700'
                      }`}>
                        {q.difficulty}
                      </span>
                      {q.topicTitle && (
                        <span className="text-xs font-medium text-brand-600 bg-brand-50 px-2 py-0.5 rounded-full">
                          {q.topicTitle}
                        </span>
                      )}
                    </div>
                    <button
                      onClick={() => {
                        if (confirm('Delete this question?')) {
                          deleteQuestionMutation.mutate(q.id);
                        }
                      }}
                      className="text-slate-400 hover:text-red-600 p-1 rounded-lg"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>

                  <p className="text-sm font-semibold text-slate-800">{q.questionText}</p>

                  {/* Options */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 pt-2">
                    {q.options?.map((opt) => (
                      <div
                        key={opt.optionLabel}
                        className={`flex items-start gap-2 p-2.5 rounded-xl border text-xs ${
                          opt.isCorrect
                            ? 'bg-emerald-50/70 border-emerald-200 text-emerald-900 font-medium'
                            : 'bg-slate-50 border-slate-100 text-slate-600'
                        }`}
                      >
                        <span className={`h-5 w-5 rounded-full flex items-center justify-center text-[10px] font-bold flex-shrink-0 ${
                          opt.isCorrect ? 'bg-emerald-600 text-white' : 'bg-slate-200 text-slate-600'
                        }`}>
                          {opt.optionLabel}
                        </span>
                        <span className="flex-1">{opt.optionText}</span>
                        {opt.isCorrect && <Check className="h-3.5 w-3.5 text-emerald-600 flex-shrink-0 mt-0.5" />}
                      </div>
                    ))}
                  </div>

                  {q.explanation && (
                    <div className="p-3 bg-slate-50 rounded-xl text-xs text-slate-600 border border-slate-100">
                      <span className="font-semibold text-slate-700">Explanation: </span>
                      {q.explanation}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB: DSA PROBLEMS */}
      {activeTab === 'dsa' && (
        <div className="space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
            <div>
              <h2 className="text-base font-bold text-slate-800">DSA Problem Bank</h2>
              <p className="text-xs text-slate-500">Create, manage, and curate coding challenges and test cases</p>
            </div>
            <button
              onClick={() => setShowDsaModal(true)}
              className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-brand-600 text-white hover:bg-brand-500 font-semibold text-xs transition-colors shadow-sm self-start"
            >
              <Plus className="h-4 w-4" /> Add DSA Problem
            </button>
          </div>

          <div className="rounded-2xl bg-white border border-slate-100 shadow-sm overflow-hidden">
            {dsaLoading ? (
              <div className="flex h-64 items-center justify-center">
                <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
              </div>
            ) : !dsaProblemsData?.content?.length ? (
              <div className="p-12 text-center text-slate-400 text-xs">No DSA problems created yet.</div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="bg-slate-50 border-b border-slate-100 text-slate-500 font-semibold uppercase text-[10px]">
                    <tr>
                      <th className="px-5 py-3">Title</th>
                      <th className="px-5 py-3">Category</th>
                      <th className="px-5 py-3">Difficulty</th>
                      <th className="px-5 py-3">Subtopic</th>
                      <th className="px-5 py-3 text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-50">
                    {dsaProblemsData.content.map((p: any) => (
                      <tr key={p.id} className="hover:bg-slate-50">
                        <td className="px-5 py-3.5 font-bold text-slate-800">{p.title}</td>
                        <td className="px-5 py-3.5 text-slate-600">{p.categoryName}</td>
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
                        <td className="px-5 py-3.5 text-slate-500">{p.subtopic || '-'}</td>
                        <td className="px-5 py-3.5 text-right">
                          <button
                            onClick={() => {
                              if (window.confirm(`Delete DSA problem "${p.title}"?`)) {
                                deleteDsaMutation.mutate(p.id);
                              }
                            }}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50"
                            title="Delete"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* CREATE COURSE MODAL */}
      {showCourseModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="card max-w-md w-full p-6 space-y-4 animate-slide-up">
            <h3 className="text-lg font-bold text-slate-900">Create New Course</h3>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Course Title</label>
              <input
                className="input"
                placeholder="e.g. Advanced Java & Spring Microservices"
                value={newCourse.title}
                onChange={(e) => {
                  const title = e.target.value;
                  const slug = title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
                  setNewCourse({ ...newCourse, title, slug });
                }}
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Slug</label>
              <input
                className="input font-mono text-xs"
                value={newCourse.slug}
                onChange={(e) => setNewCourse({ ...newCourse, slug: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Description</label>
              <textarea
                className="input min-h-[80px]"
                placeholder="Detailed curriculum overview..."
                value={newCourse.description}
                onChange={(e) => setNewCourse({ ...newCourse, description: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Level</label>
              <select
                className="input"
                value={newCourse.difficulty}
                onChange={(e) => setNewCourse({ ...newCourse, difficulty: e.target.value })}
              >
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="ADVANCED">Advanced</option>
              </select>
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setShowCourseModal(false)}
                className="btn-ghost"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={!newCourse.title || createCourseMutation.isPending}
                onClick={() => createCourseMutation.mutate(newCourse)}
                className="btn-primary"
              >
                {createCourseMutation.isPending ? 'Saving...' : 'Create Course'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* CREATE QUESTION MODAL */}
      {showQuestionModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 overflow-y-auto">
          <div className="card max-w-xl w-full p-6 space-y-4 my-8 animate-slide-up">
            <h3 className="text-lg font-bold text-slate-900">Add Question to Question Bank</h3>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Question Text</label>
              <textarea
                className="input min-h-[70px]"
                placeholder="What is the result of executing..."
                value={newQuestion.questionText}
                onChange={(e) => setNewQuestion({ ...newQuestion, questionText: e.target.value })}
              />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Difficulty</label>
                <select
                  className="input"
                  value={newQuestion.difficulty}
                  onChange={(e) => setNewQuestion({ ...newQuestion, difficulty: e.target.value })}
                >
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Company Tags</label>
                <input
                  className="input text-xs"
                  placeholder="Amazon, TCS, Infosys"
                  value={newQuestion.companyTags ?? ''}
                  onChange={(e) => setNewQuestion({ ...newQuestion, companyTags: e.target.value })}
                />
              </div>
            </div>

            {/* Options list */}
            <div className="space-y-2">
              <label className="block text-xs font-semibold text-slate-700">Options (Select the correct answer)</label>
              {newQuestion.options.map((opt, idx) => (
                <div key={opt.optionLabel} className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={() => {
                      const updated = newQuestion.options.map((o, i) => ({
                        ...o,
                        isCorrect: i === idx,
                      }));
                      setNewQuestion({ ...newQuestion, options: updated });
                    }}
                    className={`h-8 w-8 rounded-xl font-bold text-xs flex items-center justify-center transition-colors flex-shrink-0 ${
                      opt.isCorrect ? 'bg-emerald-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    {opt.optionLabel}
                  </button>
                  <input
                    className="input py-2 text-xs"
                    placeholder={`Option ${opt.optionLabel} text...`}
                    value={opt.optionText}
                    onChange={(e) => {
                      const updated = [...newQuestion.options];
                      updated[idx].optionText = e.target.value;
                      setNewQuestion({ ...newQuestion, options: updated });
                    }}
                  />
                </div>
              ))}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Explanation</label>
              <textarea
                className="input min-h-[60px] text-xs"
                placeholder="Explain why the answer is correct..."
                value={newQuestion.explanation ?? ''}
                onChange={(e) => setNewQuestion({ ...newQuestion, explanation: e.target.value })}
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setShowQuestionModal(false)}
                className="btn-ghost"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={!newQuestion.questionText || createQuestionMutation.isPending}
                onClick={() => createQuestionMutation.mutate(newQuestion)}
                className="btn-primary"
              >
                {createQuestionMutation.isPending ? 'Saving...' : 'Add Question'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* TAB: INTERVIEW QUESTIONS */}
      {activeTab === 'interview' && (
        <div className="space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <h2 className="text-base font-bold text-slate-800">Interview Q&A Question Bank</h2>
              <p className="text-xs text-slate-500">Manage technical, conceptual, and company-mapped interview questions</p>
            </div>
          </div>

          <div className="card overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50 border-b border-slate-100 text-slate-500 font-semibold uppercase text-[10px]">
                  <tr>
                    <th className="px-5 py-3">Question</th>
                    <th className="px-5 py-3">Category / Topic</th>
                    <th className="px-5 py-3">Difficulty</th>
                    <th className="px-5 py-3">Round</th>
                    <th className="px-5 py-3">Source Classification</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {interviewLoading ? (
                    <tr>
                      <td colSpan={5} className="px-5 py-8 text-center text-slate-400">
                        <Loader2 className="h-5 w-5 animate-spin mx-auto text-brand-600 mb-1" />
                        Loading interview questions...
                      </td>
                    </tr>
                  ) : interviewQuestions?.content && interviewQuestions.content.length > 0 ? (
                    interviewQuestions.content.map((q) => (
                      <tr key={q.id} className="hover:bg-slate-50">
                        <td className="px-5 py-3 font-semibold text-slate-800 max-w-sm truncate">
                          {q.questionText}
                        </td>
                        <td className="px-5 py-3 text-slate-600">
                          {q.categoryName} • {q.topicName}
                        </td>
                        <td className="px-5 py-3">
                          <span
                            className={`badge text-[10px] ${
                              q.difficulty === 'BASIC'
                                ? 'bg-emerald-100 text-emerald-700'
                                : q.difficulty === 'INTERMEDIATE'
                                ? 'bg-amber-100 text-amber-700'
                                : 'bg-red-100 text-red-700'
                            }`}
                          >
                            {q.difficulty}
                          </span>
                        </td>
                        <td className="px-5 py-3 text-slate-500">{q.interviewRound.replace(/_/g, ' ')}</td>
                        <td className="px-5 py-3">
                          <span className="badge bg-slate-100 text-slate-700 text-[10px]">
                            {q.questionSource.replace(/_/g, ' ')}
                          </span>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={5} className="px-5 py-8 text-center text-slate-400">
                        No interview questions found in database.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* CREATE DSA PROBLEM MODAL */}
      {showDsaModal && (
        <div className="fixed inset-0 z-50 bg-black/60 flex items-center justify-center p-4 backdrop-blur-xs">
          <div className="bg-white rounded-2xl max-w-2xl w-full border border-slate-100 shadow-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-800">Add New DSA Problem</h3>
              <button onClick={() => setShowDsaModal(false)} className="text-slate-400 hover:text-slate-600">
                &times;
              </button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div className="sm:col-span-2">
                <label className="block text-xs font-semibold text-slate-700 mb-1">Problem Title</label>
                <input
                  className="input text-xs"
                  placeholder="e.g., Valid Anagram"
                  value={newDsa.title}
                  onChange={(e) => setNewDsa({ ...newDsa, title: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Category</label>
                <select
                  className="input text-xs"
                  value={newDsa.categoryId}
                  onChange={(e) => setNewDsa({ ...newDsa, categoryId: Number(e.target.value) })}
                >
                  {dsaCategories?.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Difficulty</label>
                <select
                  className="input text-xs"
                  value={newDsa.difficulty}
                  onChange={(e) => setNewDsa({ ...newDsa, difficulty: e.target.value })}
                >
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-semibold text-slate-700 mb-1">Problem Description</label>
                <textarea
                  className="input min-h-[80px] text-xs"
                  placeholder="Given two strings s and t, return true if t is an anagram of s..."
                  value={newDsa.description}
                  onChange={(e) => setNewDsa({ ...newDsa, description: e.target.value })}
                />
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-semibold text-slate-700 mb-1">Java Starter Code</label>
                <textarea
                  className="input min-h-[90px] text-xs font-mono"
                  value={newDsa.javaStarterCode}
                  onChange={(e) => setNewDsa({ ...newDsa, javaStarterCode: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Sample Input</label>
                <input
                  className="input text-xs font-mono"
                  placeholder="anagram nagaram"
                  value={newDsa.sampleInput}
                  onChange={(e) => setNewDsa({ ...newDsa, sampleInput: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Sample Expected Output</label>
                <input
                  className="input text-xs font-mono"
                  placeholder="true"
                  value={newDsa.sampleOutput}
                  onChange={(e) => setNewDsa({ ...newDsa, sampleOutput: e.target.value })}
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
              <button onClick={() => setShowDsaModal(false)} className="btn-ghost">
                Cancel
              </button>
              <button
                disabled={!newDsa.title || !newDsa.description || createDsaMutation.isPending}
                onClick={() => {
                  createDsaMutation.mutate({
                    categoryId: newDsa.categoryId,
                    title: newDsa.title,
                    difficulty: newDsa.difficulty,
                    description: newDsa.description,
                    timeComplexity: newDsa.timeComplexity,
                    spaceComplexity: newDsa.spaceComplexity,
                    javaStarterCode: newDsa.javaStarterCode,
                    testCases: newDsa.sampleInput
                      ? [{ input: newDsa.sampleInput, expectedOutput: newDsa.sampleOutput, isSample: true, isHidden: false }]
                      : [],
                  });
                }}
                className="btn-primary"
              >
                {createDsaMutation.isPending ? 'Saving...' : 'Create Problem'}
              </button>
            </div>
          </div>
        </div>
      )}
      {/* TAB: JOB & PLACEMENT POSTINGS */}
      {activeTab === 'jobs' && (
        <div className="space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <h2 className="text-base font-bold text-slate-800">Job Opportunities & Placement Hub</h2>
              <p className="text-xs text-slate-500">Post verified roles, manage requirements, and review student hiring pipelines</p>
            </div>
            <button
              onClick={() => setShowJobModal(true)}
              className="btn-primary flex items-center gap-1.5 self-start sm:self-auto text-xs"
            >
              <Plus className="h-4 w-4" />
              Post Job Opening
            </button>
          </div>

          <div className="card overflow-hidden">
            {jobsLoading ? (
              <div className="p-8 text-center text-slate-400">Loading jobs...</div>
            ) : jobsData?.content && jobsData.content.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead>
                    <tr className="border-b border-slate-100 bg-slate-50 text-[10px] uppercase font-semibold text-slate-400">
                      <th className="py-3 px-4">Company</th>
                      <th className="py-3 px-4">Job Title</th>
                      <th className="py-3 px-4">Location</th>
                      <th className="py-3 px-4">Work Mode</th>
                      <th className="py-3 px-4">Experience</th>
                      <th className="py-3 px-4">Salary</th>
                      <th className="py-3 px-4">Source</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {jobsData.content.map((job) => (
                      <tr key={job.id} className="hover:bg-slate-50/60 transition-colors">
                        <td className="py-3 px-4 font-semibold text-slate-800">{job.companyName}</td>
                        <td className="py-3 px-4 font-medium text-slate-900">{job.title}</td>
                        <td className="py-3 px-4 text-slate-500">{job.location}</td>
                        <td className="py-3 px-4">
                          <span className="badge bg-slate-100 text-slate-700">{job.workMode}</span>
                        </td>
                        <td className="py-3 px-4 text-slate-600">{job.experienceLevel}</td>
                        <td className="py-3 px-4 font-semibold text-emerald-600">{job.salaryText || 'Competitive'}</td>
                        <td className="py-3 px-4 text-slate-400">{job.source}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="p-8 text-center text-slate-400">No job openings currently posted.</div>
            )}
          </div>
        </div>
      )}

      {/* MODAL: CREATE JOB */}
      {showJobModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-2xl max-w-xl w-full p-6 shadow-2xl space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="text-base font-bold text-slate-900">Post New Job Opening</h3>
              <button onClick={() => setShowJobModal(false)} className="text-slate-400 hover:text-slate-600">
                ✕
              </button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                <label className="block text-xs font-semibold text-slate-700 mb-1">Company</label>
                <select
                  value={newJob.companyId}
                  onChange={(e) => setNewJob({ ...newJob, companyId: Number(e.target.value) })}
                  className="input text-xs"
                >
                  {companiesData?.map((comp: any) => (
                    <option key={comp.id} value={comp.id}>
                      {comp.name}
                    </option>
                  )) || <option value={1}>Tata Consultancy Services</option>}
                </select>
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-semibold text-slate-700 mb-1">Job Title</label>
                <input
                  className="input text-xs"
                  placeholder="e.g. Associate Java Software Engineer"
                  value={newJob.title}
                  onChange={(e) => setNewJob({ ...newJob, title: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Location</label>
                <input
                  className="input text-xs"
                  value={newJob.location}
                  onChange={(e) => setNewJob({ ...newJob, location: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Salary Package</label>
                <input
                  className="input text-xs"
                  value={newJob.salaryText}
                  onChange={(e) => setNewJob({ ...newJob, salaryText: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Work Mode</label>
                <select
                  value={newJob.workMode}
                  onChange={(e) => setNewJob({ ...newJob, workMode: e.target.value })}
                  className="input text-xs"
                >
                  <option value="ONSITE">Onsite</option>
                  <option value="HYBRID">Hybrid</option>
                  <option value="REMOTE">Remote</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Experience Level</label>
                <select
                  value={newJob.experienceLevel}
                  onChange={(e) => setNewJob({ ...newJob, experienceLevel: e.target.value })}
                  className="input text-xs"
                >
                  <option value="FRESHER">Fresher</option>
                  <option value="ENTRY_LEVEL">0-2 Years</option>
                  <option value="MID_LEVEL">2-5 Years</option>
                </select>
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-semibold text-slate-700 mb-1">Official Career Page URL</label>
                <input
                  className="input text-xs"
                  placeholder="https://company.com/careers/job-123"
                  value={newJob.sourceUrl}
                  onChange={(e) => setNewJob({ ...newJob, sourceUrl: e.target.value })}
                />
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-semibold text-slate-700 mb-1">Job Description</label>
                <textarea
                  className="input min-h-[80px] text-xs"
                  placeholder="Describe the role, responsibilities, and team..."
                  value={newJob.description}
                  onChange={(e) => setNewJob({ ...newJob, description: e.target.value })}
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
              <button onClick={() => setShowJobModal(false)} className="btn-ghost">
                Cancel
              </button>
              <button
                disabled={!newJob.title || !newJob.description || createJobMutation.isPending}
                onClick={() => createJobMutation.mutate(newJob)}
                className="btn-primary"
              >
                {createJobMutation.isPending ? 'Publishing...' : 'Publish Job'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

