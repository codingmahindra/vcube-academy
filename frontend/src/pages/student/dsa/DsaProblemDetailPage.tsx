import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import {
  dsaApi, type DsaProblemDetail, type CodeExecutionResult, type DsaSubmissionResponse
} from '../../../api/dsa';
import {
  Play, Send, RotateCcw, HelpCircle, Lightbulb, CheckCircle2,
  XCircle, Clock, AlertTriangle, ChevronLeft, ArrowRight, Code2,
  BookOpen, Terminal, Check, Copy, Loader2, Sparkles, Lock, Eye
} from 'lucide-react';
import toast from 'react-hot-toast';

export function DsaProblemDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [problem, setProblem] = useState<DsaProblemDetail | null>(null);
  const [sourceCode, setSourceCode] = useState<string>('');
  const [loading, setLoading] = useState(true);

  // Execution state
  const [running, setRunning] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [execResult, setExecResult] = useState<CodeExecutionResult | null>(null);
  const [submissionResult, setSubmissionResult] = useState<DsaSubmissionResponse | null>(null);
  const [selectedTestCaseIndex, setSelectedTestCaseIndex] = useState(0);

  // Tabs on left pane
  const [leftTab, setLeftTab] = useState<'description' | 'hints' | 'solution'>('description');
  const [solutionUnlocked, setSolutionUnlocked] = useState(false);
  const [solutionData, setSolutionData] = useState<any>(null);
  const [loadingSolution, setLoadingSolution] = useState(false);

  useEffect(() => {
    async function loadProblem() {
      if (!id) return;
      try {
        setLoading(true);
        const data = await dsaApi.getProblemDetail(Number(id));
        setProblem(data);
        setSourceCode(data.javaStarterCode || '');
      } catch (err: any) {
        toast.error('Failed to load problem');
        navigate('/student/dsa/problems');
      } finally {
        setLoading(false);
      }
    }
    loadProblem();
  }, [id, navigate]);

  async function handleRunCode() {
    if (!problem) return;
    try {
      setRunning(true);
      setSubmissionResult(null);
      const res = await dsaApi.runCode(problem.id, sourceCode);
      setExecResult(res);
      if (res.status === 'ACCEPTED') {
        toast.success('Sample test cases passed!');
      } else if (res.status === 'COMPILATION_ERROR') {
        toast.error('Compilation Error');
      } else {
        toast.error(res.status.replace('_', ' '));
      }
    } catch (err: any) {
      toast.error('Failed to execute code');
    } finally {
      setRunning(false);
    }
  }

  async function handleSubmitCode() {
    if (!problem) return;
    try {
      setSubmitting(true);
      setExecResult(null);
      const res = await dsaApi.submitCode(problem.id, sourceCode);
      setSubmissionResult(res);
      if (res.status === 'ACCEPTED') {
        toast.success('🎉 Solution Accepted! Problem solved.');
        setProblem((prev) => (prev ? { ...prev, isSolved: true } : prev));
      } else if (res.status === 'WRONG_ANSWER') {
        toast.error(`Wrong Answer (${res.passedTestCases}/${res.totalTestCases} passed)`);
      } else if (res.status === 'COMPILATION_ERROR') {
        toast.error('Compilation Error');
      } else {
        toast.error(res.status.replace('_', ' '));
      }
    } catch (err: any) {
      toast.error('Failed to submit code');
    } finally {
      setSubmitting(false);
    }
  }

  async function handleUnlockSolution() {
    if (!problem) return;
    try {
      setLoadingSolution(true);
      const sol = await dsaApi.getSolution(problem.id);
      setSolutionData(sol);
      setSolutionUnlocked(true);
      toast.success('Solution unlocked');
    } catch (err) {
      toast.error('Failed to load solution');
    } finally {
      setLoadingSolution(false);
    }
  }

  function handleResetCode() {
    if (!problem) return;
    if (window.confirm('Reset code to default Java starter template?')) {
      setSourceCode(problem.javaStarterCode);
      toast.success('Code reset to default');
    }
  }

  if (loading || !problem) {
    return (
      <div className="flex h-96 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  let hintsList: string[] = [];
  try {
    if (problem.hints) hintsList = JSON.parse(problem.hints);
  } catch {
    hintsList = problem.hints ? [problem.hints] : [];
  }

  let interviewPoints: string[] = [];
  try {
    if (problem.interviewPoints) interviewPoints = JSON.parse(problem.interviewPoints);
  } catch {
    interviewPoints = problem.interviewPoints ? [problem.interviewPoints] : [];
  }

  let companyTags: string[] = [];
  try {
    if (problem.companyTags) companyTags = JSON.parse(problem.companyTags);
  } catch {
    companyTags = [];
  }

  const activeResults = execResult?.testCaseResults ?? submissionResult?.testCaseResults ?? [];

  return (
    <div className="flex flex-col h-[calc(100vh-6rem)] max-w-7xl mx-auto space-y-3">
      {/* Top Header */}
      <div className="flex items-center justify-between bg-white px-4 py-2.5 rounded-xl border border-slate-100 shadow-sm">
        <div className="flex items-center gap-3">
          <Link
            to="/student/dsa/problems"
            className="p-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 text-slate-600"
          >
            <ChevronLeft className="h-4 w-4" />
          </Link>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-sm font-bold text-slate-800">{problem.title}</h1>
              <span
                className={`badge text-[10px] ${
                  problem.difficulty === 'EASY'
                    ? 'bg-emerald-100 text-emerald-700'
                    : problem.difficulty === 'MEDIUM'
                    ? 'bg-amber-100 text-amber-700'
                    : 'bg-red-100 text-red-700'
                }`}
              >
                {problem.difficulty}
              </span>
              {problem.isSolved && (
                <span className="badge bg-emerald-50 text-emerald-600 text-[10px] gap-1">
                  <CheckCircle2 className="h-3 w-3" /> Solved
                </span>
              )}
            </div>
            <p className="text-[11px] text-slate-400 mt-0.5">{problem.categoryName} • {problem.subtopic ?? 'Algorithm'}</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={handleResetCode}
            className="p-2 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-50 text-xs font-semibold"
            title="Reset Code"
          >
            <RotateCcw className="h-3.5 w-3.5" />
          </button>
          <button
            onClick={handleRunCode}
            disabled={running || submitting}
            className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 font-semibold text-xs transition-colors disabled:opacity-50"
          >
            {running ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : <Play className="h-3.5 w-3.5 text-slate-600" />}
            Run
          </button>
          <button
            onClick={handleSubmitCode}
            disabled={running || submitting}
            className="inline-flex items-center gap-1.5 px-4 py-1.5 rounded-lg bg-brand-600 text-white hover:bg-brand-500 font-semibold text-xs shadow-md shadow-brand-600/20 transition-all disabled:opacity-50"
          >
            {submitting ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : <Send className="h-3.5 w-3.5" />}
            Submit
          </button>
        </div>
      </div>

      {/* Main Two-Pane Split */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-3 flex-1 min-h-0">
        {/* Left Pane: Description / Hints / Solution */}
        <div className="lg:col-span-6 bg-white rounded-xl border border-slate-100 shadow-sm flex flex-col overflow-hidden">
          {/* Tabs */}
          <div className="flex items-center border-b border-slate-100 px-3 bg-slate-50/50">
            <button
              onClick={() => setLeftTab('description')}
              className={`px-3 py-2.5 text-xs font-semibold border-b-2 flex items-center gap-1.5 ${
                leftTab === 'description'
                  ? 'border-brand-600 text-brand-700 bg-white'
                  : 'border-transparent text-slate-500 hover:text-slate-700'
              }`}
            >
              <BookOpen className="h-3.5 w-3.5" /> Problem
            </button>
            <button
              onClick={() => setLeftTab('hints')}
              className={`px-3 py-2.5 text-xs font-semibold border-b-2 flex items-center gap-1.5 ${
                leftTab === 'hints'
                  ? 'border-brand-600 text-brand-700 bg-white'
                  : 'border-transparent text-slate-500 hover:text-slate-700'
              }`}
            >
              <Lightbulb className="h-3.5 w-3.5" /> Hints ({hintsList.length})
            </button>
            <button
              onClick={() => setLeftTab('solution')}
              className={`px-3 py-2.5 text-xs font-semibold border-b-2 flex items-center gap-1.5 ${
                leftTab === 'solution'
                  ? 'border-brand-600 text-brand-700 bg-white'
                  : 'border-transparent text-slate-500 hover:text-slate-700'
              }`}
            >
              <Sparkles className="h-3.5 w-3.5" /> Solution
            </button>
          </div>

          {/* Left Content */}
          <div className="flex-1 overflow-y-auto p-4 text-xs space-y-4 text-slate-700 leading-relaxed">
            {leftTab === 'description' && (
              <>
                <div className="space-y-2">
                  <h2 className="text-sm font-bold text-slate-900">Description</h2>
                  <div className="whitespace-pre-wrap">{problem.description}</div>
                </div>

                {/* Examples */}
                {problem.sampleTestCases.length > 0 && (
                  <div className="space-y-3">
                    <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500">Examples</h3>
                    {problem.sampleTestCases.map((tc, idx) => (
                      <div key={tc.id || idx} className="rounded-lg bg-slate-50 border border-slate-100 p-3 space-y-1.5">
                        <p className="font-semibold text-slate-800 text-[11px]">Example {idx + 1}:</p>
                        <p><span className="font-semibold text-slate-600">Input:</span> <code className="bg-slate-200/60 px-1.5 py-0.5 rounded text-[11px]">{tc.input}</code></p>
                        <p><span className="font-semibold text-slate-600">Output:</span> <code className="bg-slate-200/60 px-1.5 py-0.5 rounded text-[11px]">{tc.expectedOutput}</code></p>
                        {tc.explanation && (
                          <p className="text-slate-500 text-[11px] mt-1"><span className="font-semibold">Explanation:</span> {tc.explanation}</p>
                        )}
                      </div>
                    ))}
                  </div>
                )}

                {/* Constraints */}
                {problem.constraints && (
                  <div className="space-y-1.5">
                    <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500">Constraints</h3>
                    <div className="rounded-lg bg-slate-50 p-2.5 border border-slate-100 whitespace-pre-wrap font-mono text-[11px] text-slate-600">
                      {problem.constraints}
                    </div>
                  </div>
                )}

                {/* Complexity Expectations */}
                {(problem.timeComplexity || problem.spaceComplexity) && (
                  <div className="p-3 rounded-lg bg-brand-50/50 border border-brand-100 space-y-1 text-slate-700">
                    <p className="font-semibold text-brand-900 text-xs">Target Complexity</p>
                    <div className="flex gap-4 text-[11px]">
                      {problem.timeComplexity && <span>Time: <strong>{problem.timeComplexity}</strong></span>}
                      {problem.spaceComplexity && <span>Space: <strong>{problem.spaceComplexity}</strong></span>}
                    </div>
                  </div>
                )}

                {/* Interview Points */}
                {interviewPoints.length > 0 && (
                  <div className="space-y-1.5">
                    <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500">Interview Takeaways</h3>
                    <ul className="list-disc pl-4 space-y-1 text-slate-600">
                      {interviewPoints.map((pt, i) => (
                        <li key={i}>{pt}</li>
                      ))}
                    </ul>
                  </div>
                )}

                {/* Company Tags */}
                {companyTags.length > 0 && (
                  <div className="pt-2">
                    <p className="text-[11px] text-slate-400 font-semibold mb-1.5">Asked in Companies:</p>
                    <div className="flex flex-wrap gap-1.5">
                      {companyTags.map((tag) => (
                        <span key={tag} className="badge bg-slate-100 text-slate-600 text-[10px]">
                          {tag}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </>
            )}

            {leftTab === 'hints' && (
              <div className="space-y-3">
                <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2">
                  <Lightbulb className="h-4 w-4 text-amber-500" /> Problem Hints
                </h2>
                {hintsList.length === 0 ? (
                  <p className="text-slate-400">No hints available for this problem. Try reading the constraints!</p>
                ) : (
                  hintsList.map((hint, idx) => (
                    <div key={idx} className="p-3 rounded-lg bg-amber-50/40 border border-amber-100 space-y-1">
                      <p className="font-bold text-amber-900 text-[11px]">Hint {idx + 1}</p>
                      <p className="text-slate-700">{hint}</p>
                    </div>
                  ))
                )}
              </div>
            )}

            {leftTab === 'solution' && (
              <div className="space-y-3">
                {!solutionUnlocked ? (
                  <div className="p-6 text-center space-y-3 bg-slate-50 rounded-xl border border-slate-100">
                    <Lock className="h-8 w-8 text-slate-400 mx-auto" />
                    <p className="text-xs font-semibold text-slate-700">Official Java Solution is Locked</p>
                    <p className="text-[11px] text-slate-400 max-w-sm mx-auto">
                      We encourage attempting the problem first before viewing the full solution code and explanation.
                    </p>
                    <button
                      onClick={handleUnlockSolution}
                      disabled={loadingSolution}
                      className="inline-flex items-center gap-2 px-4 py-2 rounded-lg bg-brand-600 text-white text-xs font-semibold hover:bg-brand-500 shadow-sm"
                    >
                      {loadingSolution ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : <Eye className="h-3.5 w-3.5" />}
                      Reveal Solution
                    </button>
                  </div>
                ) : (
                  <div className="space-y-4">
                    <div>
                      <h3 className="font-bold text-slate-900 text-xs uppercase tracking-wider">Approach</h3>
                      <p className="mt-1 text-slate-600">{solutionData?.expectedApproach || 'Optimal Java Solution'}</p>
                    </div>

                    <div>
                      <h3 className="font-bold text-slate-900 text-xs uppercase tracking-wider">Step-by-Step Explanation</h3>
                      <div className="mt-1 text-slate-600 whitespace-pre-wrap">{solutionData?.explanation}</div>
                    </div>

                    {solutionData?.solutionJavaCode && (
                      <div>
                        <h3 className="font-bold text-slate-900 text-xs uppercase tracking-wider mb-1">Java Solution Code</h3>
                        <pre className="p-3 rounded-lg bg-slate-900 text-emerald-300 font-mono text-[11px] overflow-x-auto">
                          {solutionData.solutionJavaCode}
                        </pre>
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Right Pane: Code Editor & Output Console */}
        <div className="lg:col-span-6 flex flex-col gap-3 min-h-0">
          {/* Java Code Editor */}
          <div className="flex-1 bg-slate-950 rounded-xl border border-slate-800 shadow-sm flex flex-col overflow-hidden min-h-[300px]">
            <div className="flex items-center justify-between px-4 py-2 bg-slate-900 border-b border-slate-800">
              <div className="flex items-center gap-2">
                <Code2 className="h-3.5 w-3.5 text-brand-400" />
                <span className="text-xs font-mono text-slate-300 font-semibold">Solution.java</span>
              </div>
              <span className="text-[10px] text-slate-500 font-mono">Java 21</span>
            </div>

            <textarea
              value={sourceCode}
              onChange={(e) => setSourceCode(e.target.value)}
              className="flex-1 w-full bg-transparent p-4 font-mono text-xs text-slate-200 resize-none focus:outline-none leading-relaxed selection:bg-brand-500/30"
              spellCheck={false}
              autoCapitalize="none"
              autoComplete="off"
            />
          </div>

          {/* Output / Test Case Results Box */}
          <div className="h-56 bg-white rounded-xl border border-slate-100 shadow-sm flex flex-col overflow-hidden">
            <div className="flex items-center justify-between px-3 py-2 bg-slate-50 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <Terminal className="h-3.5 w-3.5 text-slate-500" />
                <span className="text-xs font-bold text-slate-700">Test Execution Output</span>
              </div>

              {/* Status Tag */}
              {(execResult || submissionResult) && (
                <div className="flex items-center gap-2">
                  <span
                    className={`badge text-[10px] font-bold ${
                      (execResult?.status ?? submissionResult?.status) === 'ACCEPTED'
                        ? 'bg-emerald-100 text-emerald-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {execResult?.status ?? submissionResult?.status}
                  </span>
                  {(execResult?.executionTimeMs !== undefined || submissionResult?.executionTimeMs !== undefined) && (
                    <span className="text-[10px] text-slate-400 font-mono">
                      {execResult?.executionTimeMs ?? submissionResult?.executionTimeMs} ms
                    </span>
                  )}
                </div>
              )}
            </div>

            <div className="flex-1 overflow-y-auto p-3 text-xs">
              {!execResult && !submissionResult ? (
                <div className="h-full flex items-center justify-center text-slate-400 text-[11px]">
                  Click "Run" to test with sample inputs, or "Submit" to evaluate all test cases.
                </div>
              ) : (
                <div className="space-y-3">
                  {/* Compilation Error Display */}
                  {(execResult?.errorOutput || submissionResult?.errorOutput) && (
                    <div className="p-2.5 rounded-lg bg-red-50 border border-red-100 text-red-700 font-mono text-[11px] whitespace-pre-wrap">
                      {execResult?.errorOutput ?? submissionResult?.errorOutput}
                    </div>
                  )}

                  {/* Test Cases Tabs */}
                  {activeResults.length > 0 && (
                    <div className="space-y-2">
                      <div className="flex items-center gap-1.5 overflow-x-auto pb-1">
                        {activeResults.map((tc, idx) => (
                          <button
                            key={idx}
                            onClick={() => setSelectedTestCaseIndex(idx)}
                            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold flex items-center gap-1.5 transition-colors ${
                              selectedTestCaseIndex === idx
                                ? 'bg-slate-800 text-white'
                                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                            }`}
                          >
                            {tc.passed ? (
                              <Check className="h-3 w-3 text-emerald-400" />
                            ) : (
                              <XCircle className="h-3 w-3 text-red-400" />
                            )}
                            Case {idx + 1} {tc.isHidden && <span className="text-[9px] text-slate-400">(Hidden)</span>}
                          </button>
                        ))}
                      </div>

                      {/* Selected Case Detail */}
                      {activeResults[selectedTestCaseIndex] && (
                        <div className="rounded-lg bg-slate-50 p-2.5 border border-slate-100 space-y-1.5 font-mono text-[11px]">
                          <div>
                            <span className="text-slate-400 font-sans text-[10px] font-semibold">Input:</span>
                            <p className="text-slate-800">{activeResults[selectedTestCaseIndex].input}</p>
                          </div>
                          <div>
                            <span className="text-slate-400 font-sans text-[10px] font-semibold">Expected:</span>
                            <p className="text-emerald-700 font-bold">{activeResults[selectedTestCaseIndex].expectedOutput}</p>
                          </div>
                          <div>
                            <span className="text-slate-400 font-sans text-[10px] font-semibold">Output:</span>
                            <p className={activeResults[selectedTestCaseIndex].passed ? 'text-emerald-700 font-bold' : 'text-red-600 font-bold'}>
                              {activeResults[selectedTestCaseIndex].actualOutput}
                            </p>
                          </div>
                          {activeResults[selectedTestCaseIndex].error && (
                            <p className="text-red-500 text-[10px]">{activeResults[selectedTestCaseIndex].error}</p>
                          )}
                        </div>
                      )}
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
