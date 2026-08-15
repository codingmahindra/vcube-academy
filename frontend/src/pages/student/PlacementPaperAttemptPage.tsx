import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import {
  careerApi,
  type PlacementPaperAttempt,
  type PlacementPaperResult,
  type PlacementPaperQuestion
} from '../../api/career';
import {
  Clock, CheckCircle2, XCircle, AlertCircle, ArrowRight,
  ArrowLeft, FileCheck, Award, RotateCcw
} from 'lucide-react';

export default function PlacementPaperAttemptPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [attempt, setAttempt] = useState<PlacementPaperAttempt | null>(null);
  const [result, setResult] = useState<PlacementPaperResult | null>(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState<Record<number, string>>({});
  const [timeLeftSeconds, setTimeLeftSeconds] = useState(3600);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Initialize Exam Session
  useEffect(() => {
    async function startExam() {
      if (!id) return;
      try {
        setLoading(true);
        const data = await careerApi.startPlacementPaperAttempt(Number(id));
        setAttempt(data);
        setTimeLeftSeconds(data.durationMinutes * 60);

        // Pre-populate any existing answers if resuming
        const initAnswers: Record<number, string> = {};
        data.questions.forEach((q) => {
          if (q.selectedOption) {
            initAnswers[q.id] = q.selectedOption;
          }
        });
        setSelectedAnswers(initAnswers);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to initialize examination session');
      } finally {
        setLoading(false);
      }
    }
    startExam();
  }, [id]);

  // Countdown Timer
  useEffect(() => {
    if (!attempt || result || timeLeftSeconds <= 0) return;
    const timer = setInterval(() => {
      setTimeLeftSeconds((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          handleSubmitExam();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, [attempt, result, timeLeftSeconds]);

  const handleSelectOption = async (questionId: number, option: string) => {
    if (result) return; // Exam finished

    setSelectedAnswers((prev) => ({ ...prev, [questionId]: option }));

    if (attempt) {
      try {
        await careerApi.submitPlacementPaperAnswer(attempt.paperId, {
          attemptId: attempt.id,
          questionId,
          selectedOption: option,
          timeTakenSeconds: 10,
        });
      } catch (e) {
        console.error('Failed to autosave answer', e);
      }
    }
  };

  const handleSubmitExam = async () => {
    if (!attempt || submitting) return;
    try {
      setSubmitting(true);
      const res = await careerApi.completePlacementPaperAttempt(attempt.paperId, attempt.id);
      setResult(res);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to complete examination');
    } finally {
      setSubmitting(false);
    }
  };

  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-24">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !attempt) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center text-red-700 max-w-xl mx-auto mt-12">
        <AlertCircle className="mx-auto h-8 w-8 mb-2" />
        <p className="font-semibold">{error || 'Unable to load test session.'}</p>
        <button
          onClick={() => navigate('/student/placement-papers')}
          className="mt-4 rounded-xl bg-red-600 px-4 py-2 text-xs font-semibold text-white hover:bg-red-500"
        >
          Return to Placement Papers
        </button>
      </div>
    );
  }

  // ── Result View After Completion ──────────────────────────────────────────
  if (result) {
    return (
      <div className="space-y-8 max-w-4xl mx-auto pb-12">
        {/* Results Banner */}
        <div
          className={`rounded-2xl p-8 text-white shadow-xl ${
            result.isPassed
              ? 'bg-gradient-to-r from-emerald-900 via-slate-900 to-emerald-950'
              : 'bg-gradient-to-r from-slate-900 via-rose-950 to-slate-900'
          }`}
        >
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div className="space-y-2">
              <span
                className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-bold ${
                  result.isPassed ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-400/30' : 'bg-rose-500/20 text-rose-300 border border-rose-400/30'
                }`}
              >
                {result.isPassed ? <CheckCircle2 className="h-4 w-4" /> : <XCircle className="h-4 w-4" />}
                {result.isPassed ? 'PASSED CUTOFF' : 'CUTOFF NOT MET'}
              </span>
              <h1 className="text-2xl font-bold">{result.paperTitle}</h1>
              <p className="text-xs text-slate-300">
                Completed on {new Date(result.completedAt).toLocaleString()} • Company: {result.companyName}
              </p>
            </div>

            <div className="rounded-2xl bg-white/10 backdrop-blur-md p-5 text-center min-w-[150px] border border-white/10">
              <p className="text-xs uppercase font-semibold text-slate-300">Final Score</p>
              <p className="text-3xl font-bold text-white mt-1">{result.scoreObtained}/{result.totalMarks}</p>
              <p className="text-xs text-emerald-300 font-semibold mt-0.5">{result.percentage}% Marks</p>
            </div>
          </div>
        </div>

        {/* Section Breakdown Grid */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm space-y-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
            <Award className="h-5 w-5 text-indigo-600" /> Sectional Performance
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
            {result.sectionScores.map((sec) => (
              <div key={sec.sectionName} className="rounded-xl border border-slate-200 bg-slate-50 p-4 space-y-2">
                <div className="flex justify-between items-center">
                  <span className="font-bold text-xs text-slate-800">{sec.sectionName}</span>
                  <span className="text-xs font-mono font-bold text-indigo-600">{sec.accuracyPercentage}%</span>
                </div>
                <div className="w-full bg-slate-200 rounded-full h-1.5 overflow-hidden">
                  <div
                    className="bg-indigo-600 h-full rounded-full"
                    style={{ width: `${sec.accuracyPercentage}%` }}
                  ></div>
                </div>
                <p className="text-[11px] text-slate-500">
                  {sec.correctAnswers} of {sec.totalQuestions} questions correct ({sec.score} Marks)
                </p>
              </div>
            ))}
          </div>
        </div>

        {/* Detailed Solutions and Answers */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm space-y-6">
          <h2 className="text-base font-bold text-slate-900">Question Solutions & Rationales</h2>
          <div className="space-y-6">
            {result.questions.map((q, idx) => (
              <div key={q.id} className="rounded-xl border border-slate-200 p-5 space-y-3 bg-slate-50/40">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-slate-700">Question {idx + 1} • {q.sectionName}</span>
                  <span
                    className={`inline-flex items-center gap-1 text-xs font-bold px-2 py-0.5 rounded-full ${
                      q.isCorrect
                        ? 'bg-emerald-100 text-emerald-800'
                        : q.selectedOption
                        ? 'bg-rose-100 text-rose-800'
                        : 'bg-slate-200 text-slate-600'
                    }`}
                  >
                    {q.isCorrect ? 'Correct (+2)' : q.selectedOption ? 'Incorrect (0)' : 'Unanswered (0)'}
                  </span>
                </div>

                <p className="text-sm font-semibold text-slate-900">{q.questionText}</p>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
                  <div className={`p-2.5 rounded-lg border ${q.selectedOption === 'A' ? (q.isCorrect ? 'border-emerald-400 bg-emerald-50' : 'border-rose-400 bg-rose-50') : 'border-slate-200 bg-white'}`}>
                    A. {q.optionA}
                  </div>
                  <div className={`p-2.5 rounded-lg border ${q.selectedOption === 'B' ? (q.isCorrect ? 'border-emerald-400 bg-emerald-50' : 'border-rose-400 bg-rose-50') : 'border-slate-200 bg-white'}`}>
                    B. {q.optionB}
                  </div>
                  <div className={`p-2.5 rounded-lg border ${q.selectedOption === 'C' ? (q.isCorrect ? 'border-emerald-400 bg-emerald-50' : 'border-rose-400 bg-rose-50') : 'border-slate-200 bg-white'}`}>
                    C. {q.optionC}
                  </div>
                  <div className={`p-2.5 rounded-lg border ${q.selectedOption === 'D' ? (q.isCorrect ? 'border-emerald-400 bg-emerald-50' : 'border-rose-400 bg-rose-50') : 'border-slate-200 bg-white'}`}>
                    D. {q.optionD}
                  </div>
                </div>

                {q.explanation && (
                  <div className="rounded-lg bg-indigo-50/60 border border-indigo-100 p-3 text-xs text-indigo-950">
                    <span className="font-bold text-indigo-700">Explanation: </span>
                    {q.explanation}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Retake / Return Actions */}
        <div className="flex justify-between items-center pt-4">
          <Link
            to="/student/placement-papers"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-600 hover:text-indigo-600"
          >
            <ArrowLeft className="h-4 w-4" /> Back to Placement Papers
          </Link>
          <button
            onClick={() => window.location.reload()}
            className="inline-flex items-center gap-1.5 rounded-xl bg-indigo-600 px-5 py-2.5 text-xs font-semibold text-white hover:bg-indigo-500 shadow-sm"
          >
            <RotateCcw className="h-4 w-4" /> Retake Exam
          </button>
        </div>
      </div>
    );
  }

  // ── Live Exam Taking View ──────────────────────────────────────────────────
  const currentQ: PlacementPaperQuestion = attempt.questions[currentIndex];
  const isAnswered = (qId: number) => Boolean(selectedAnswers[qId]);

  return (
    <div className="space-y-6 max-w-6xl mx-auto pb-12">
      {/* Exam Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <div>
          <span className="rounded-md bg-indigo-50 px-2.5 py-0.5 text-xs font-bold text-indigo-700">
            {currentQ.sectionName}
          </span>
          <h1 className="text-base font-bold text-slate-900 mt-1">{attempt.paperTitle}</h1>
        </div>

        <div className="flex items-center gap-4">
          <div
            className={`flex items-center gap-2 rounded-xl px-4 py-2 font-mono text-sm font-bold shadow-xs ${
              timeLeftSeconds < 300
                ? 'bg-rose-50 border border-rose-200 text-rose-700 animate-pulse'
                : 'bg-slate-100 text-slate-800'
            }`}
          >
            <Clock className="h-4 w-4 text-slate-500" />
            {formatTime(timeLeftSeconds)}
          </div>

          <button
            onClick={handleSubmitExam}
            disabled={submitting}
            className="inline-flex items-center gap-1.5 rounded-xl bg-emerald-600 px-5 py-2 text-xs font-bold text-white hover:bg-emerald-500 shadow-sm disabled:opacity-50 transition-colors"
          >
            <FileCheck className="h-4 w-4" /> Submit Exam
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Question Panel */}
        <div className="lg:col-span-3 rounded-2xl border border-slate-200 bg-white p-7 shadow-sm space-y-6">
          <div className="flex items-center justify-between pb-3 border-b border-slate-100">
            <span className="text-xs font-bold text-slate-500">
              Question {currentIndex + 1} of {attempt.totalQuestions}
            </span>
            <span className="text-xs font-semibold text-slate-400 font-mono">
              Marks: {currentQ.marks}
            </span>
          </div>

          <p className="text-base font-semibold text-slate-900 leading-relaxed whitespace-pre-wrap">
            {currentQ.questionText}
          </p>

          {/* Multiple Choice Options */}
          <div className="space-y-3 pt-2">
            {[
              { key: 'A', text: currentQ.optionA },
              { key: 'B', text: currentQ.optionB },
              { key: 'C', text: currentQ.optionC },
              { key: 'D', text: currentQ.optionD },
            ].map(({ key, text }) => {
              const selected = selectedAnswers[currentQ.id] === key;
              return (
                <button
                  key={key}
                  type="button"
                  onClick={() => handleSelectOption(currentQ.id, key)}
                  className={`flex w-full items-center gap-3.5 rounded-xl border p-4 text-left text-sm font-medium transition-all ${
                    selected
                      ? 'border-indigo-600 bg-indigo-50/70 text-indigo-950 shadow-xs ring-1 ring-indigo-600'
                      : 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50/60 text-slate-800'
                  }`}
                >
                  <span
                    className={`flex h-7 w-7 flex-shrink-0 items-center justify-center rounded-lg text-xs font-bold ${
                      selected ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-600'
                    }`}
                  >
                    {key}
                  </span>
                  <span className="flex-1">{text}</span>
                </button>
              );
            })}
          </div>

          {/* Navigation Controls */}
          <div className="flex justify-between items-center pt-6 border-t border-slate-100">
            <button
              onClick={() => setCurrentIndex((prev) => Math.max(0, prev - 1))}
              disabled={currentIndex === 0}
              className="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 px-4 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              <ArrowLeft className="h-3.5 w-3.5" /> Previous
            </button>

            <button
              onClick={() => setCurrentIndex((prev) => Math.min(attempt.totalQuestions - 1, prev + 1))}
              disabled={currentIndex === attempt.totalQuestions - 1}
              className="inline-flex items-center gap-1.5 rounded-xl bg-indigo-600 px-5 py-2 text-xs font-semibold text-white hover:bg-indigo-500 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              Next <ArrowRight className="h-3.5 w-3.5" />
            </button>
          </div>
        </div>

        {/* Question Palette Sidebar */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm space-y-4">
          <h2 className="text-xs font-bold uppercase tracking-wider text-slate-500">
            Question Palette
          </h2>

          <div className="grid grid-cols-5 gap-2">
            {attempt.questions.map((q, idx) => {
              const answered = isAnswered(q.id);
              const current = currentIndex === idx;
              return (
                <button
                  key={q.id}
                  onClick={() => setCurrentIndex(idx)}
                  className={`h-9 w-full rounded-lg text-xs font-bold transition-all ${
                    current
                      ? 'bg-indigo-600 text-white ring-2 ring-indigo-400'
                      : answered
                      ? 'bg-emerald-100 text-emerald-800 border border-emerald-300'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                  }`}
                >
                  {idx + 1}
                </button>
              );
            })}
          </div>

          <div className="space-y-2 pt-4 border-t border-slate-100 text-xs text-slate-500">
            <div className="flex items-center gap-2">
              <span className="h-3 w-3 rounded-sm bg-emerald-100 border border-emerald-300"></span>
              <span>Answered</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="h-3 w-3 rounded-sm bg-slate-100"></span>
              <span>Unanswered</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="h-3 w-3 rounded-sm bg-indigo-600"></span>
              <span>Current</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
