import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import {
  interviewApi,
  type InterviewQuestionDetailDto,
  type InterviewEvaluationResponse,
  type InterviewCategoryDto,
  type InterviewTopicDto,
} from '../../../api/interview';
import {
  HelpCircle, Send, CheckCircle2, XCircle, ChevronLeft, ChevronRight,
  Eye, Sparkles, Lightbulb, AlertTriangle, BookOpen, Star, Loader2, ArrowRight
} from 'lucide-react';
import toast from 'react-hot-toast';

export function InterviewQuestionPracticePage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();

  const [questions, setQuestions] = useState<any[]>([]);
  const [selectedQuestion, setSelectedQuestion] = useState<InterviewQuestionDetailDto | null>(null);
  const [userAnswer, setUserAnswer] = useState('');
  const [evaluating, setEvaluating] = useState(false);
  const [evalResult, setEvalResult] = useState<InterviewEvaluationResponse | null>(null);
  const [revealed, setRevealed] = useState(false);
  const [loading, setLoading] = useState(true);

  const topicId = searchParams.get('topicId');
  const questionIdParam = searchParams.get('questionId');

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        if (questionIdParam) {
          const q = await interviewApi.getQuestionDetail(Number(questionIdParam));
          setSelectedQuestion(q);
        } else {
          const res = await interviewApi.searchQuestions({
            topicId: topicId ? Number(topicId) : undefined,
            size: 20,
          });
          setQuestions(res.content);
          if (res.content.length > 0) {
            const first = await interviewApi.getQuestionDetail(res.content[0].id);
            setSelectedQuestion(first);
          }
        }
      } catch (err) {
        toast.error('Failed to load interview questions');
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [topicId, questionIdParam]);

  async function handleSelectQuestion(qId: number) {
    try {
      setLoading(true);
      setEvalResult(null);
      setRevealed(false);
      setUserAnswer('');
      const q = await interviewApi.getQuestionDetail(qId);
      setSelectedQuestion(q);
      setSearchParams({ questionId: String(qId) });
    } catch (err) {
      toast.error('Failed to load question');
    } finally {
      setLoading(false);
    }
  }

  async function handleEvaluate() {
    if (!selectedQuestion || !userAnswer.trim()) {
      toast.error('Please type your response before submitting');
      return;
    }
    try {
      setEvaluating(true);
      const res = await interviewApi.evaluatePracticeAnswer(selectedQuestion.id, userAnswer);
      setEvalResult(res);
      setRevealed(true);
      if (res.score >= 70) {
        toast.success(`Great answer! Score: ${res.score}/100`);
      } else {
        toast('Answer evaluated. Review the recommendations below.');
      }
    } catch (err) {
      toast.error('Failed to evaluate answer');
    } finally {
      setEvaluating(false);
    }
  }

  if (loading && !selectedQuestion) {
    return (
      <div className="flex h-72 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  let interviewPoints: string[] = [];
  try {
    if (selectedQuestion?.interviewPoints) interviewPoints = JSON.parse(selectedQuestion.interviewPoints);
  } catch {
    interviewPoints = selectedQuestion?.interviewPoints ? [selectedQuestion.interviewPoints] : [];
  }

  let commonMistakes: string[] = [];
  try {
    if (selectedQuestion?.commonMistakes) commonMistakes = JSON.parse(selectedQuestion.commonMistakes);
  } catch {
    commonMistakes = selectedQuestion?.commonMistakes ? [selectedQuestion.commonMistakes] : [];
  }

  let followUpQuestions: string[] = [];
  try {
    if (selectedQuestion?.followUpQuestions) followUpQuestions = JSON.parse(selectedQuestion.followUpQuestions);
  } catch {
    followUpQuestions = selectedQuestion?.followUpQuestions ? [selectedQuestion.followUpQuestions] : [];
  }

  return (
    <div className="space-y-4 max-w-7xl mx-auto pb-12">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Link
            to="/student/interview/topics"
            className="p-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600"
          >
            <ChevronLeft className="h-4 w-4" />
          </Link>
          <div>
            <h1 className="text-xl font-bold text-slate-800">Interview Q&A Practice</h1>
            <p className="text-xs text-slate-500">
              {selectedQuestion ? `${selectedQuestion.categoryName} • ${selectedQuestion.topicName}` : 'Practice mode'}
            </p>
          </div>
        </div>

        <Link
          to="/student/interview/mock"
          className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl bg-brand-600 text-white text-xs font-semibold hover:bg-brand-500 shadow-sm"
        >
          <Sparkles className="h-3.5 w-3.5" /> Start Full Mock Interview
        </Link>
      </div>

      {selectedQuestion && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-4">
          {/* Main Question & Answer Panel */}
          <div className="lg:col-span-8 space-y-4">
            {/* Question Card */}
            <div className="card p-6 space-y-4 border-l-4 border-l-brand-600">
              <div className="flex flex-wrap items-center gap-2">
                <span
                  className={`badge text-[10px] ${
                    selectedQuestion.difficulty === 'BASIC'
                      ? 'bg-emerald-100 text-emerald-700'
                      : selectedQuestion.difficulty === 'INTERMEDIATE'
                      ? 'bg-amber-100 text-amber-700'
                      : 'bg-red-100 text-red-700'
                  }`}
                >
                  {selectedQuestion.difficulty}
                </span>
                <span className="badge bg-indigo-50 text-indigo-700 text-[10px]">
                  {selectedQuestion.interviewRound.replace(/_/g, ' ')}
                </span>
                <span className="badge bg-slate-100 text-slate-600 text-[10px]">
                  {selectedQuestion.questionSource.replace(/_/g, ' ')}
                </span>
              </div>

              <h2 className="text-base sm:text-lg font-bold text-slate-900 leading-snug">
                {selectedQuestion.questionText}
              </h2>

              {selectedQuestion.sourceReference && (
                <p className="text-xs text-slate-400 italic">
                  Source: {selectedQuestion.sourceReference}
                </p>
              )}
            </div>

            {/* Answer Input Box */}
            <div className="card p-6 space-y-4">
              <div className="flex items-center justify-between">
                <label className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                  Your Answer (Type how you would articulate this in a real interview)
                </label>
                <span className="text-[11px] text-slate-400 font-mono">
                  {userAnswer.split(/\s+/).filter(Boolean).length} words
                </span>
              </div>

              <textarea
                className="input min-h-[140px] text-xs leading-relaxed"
                placeholder="Start answering with clear concepts, technical mechanisms, and examples..."
                value={userAnswer}
                onChange={(e) => setUserAnswer(e.target.value)}
              />

              <div className="flex flex-wrap items-center justify-between gap-3 pt-2">
                <button
                  onClick={() => setRevealed(!revealed)}
                  className="inline-flex items-center gap-1.5 px-3 py-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-50 text-xs font-semibold transition-colors"
                >
                  <Eye className="h-4 w-4" /> {revealed ? 'Hide Official Answer' : 'Reveal Official Answer'}
                </button>

                <button
                  onClick={handleEvaluate}
                  disabled={evaluating || !userAnswer.trim()}
                  className="inline-flex items-center gap-2 px-5 py-2 rounded-xl bg-brand-600 text-white hover:bg-brand-500 font-semibold text-xs transition-all shadow-md shadow-brand-600/20 disabled:opacity-50"
                >
                  {evaluating ? <Loader2 className="h-4 w-4 animate-spin" /> : <Send className="h-4 w-4" />}
                  Evaluate My Answer
                </button>
              </div>
            </div>

            {/* Evaluation Result Feedback */}
            {evalResult && (
              <div className="card p-6 space-y-4 border-2 border-brand-200 bg-brand-50/20 animate-fade-in">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Star className="h-5 w-5 text-amber-500 fill-amber-400" />
                    <h3 className="text-base font-bold text-slate-900">Evaluation Score: {evalResult.score} / 100</h3>
                  </div>
                </div>

                <p className="text-xs text-slate-700 leading-relaxed font-medium">{evalResult.feedback}</p>

                {evalResult.strengths.length > 0 && (
                  <div className="space-y-1.5">
                    <p className="text-xs font-bold text-emerald-800 flex items-center gap-1.5">
                      <CheckCircle2 className="h-3.5 w-3.5 text-emerald-600" /> Key Strengths
                    </p>
                    <ul className="list-disc pl-5 text-xs text-slate-700 space-y-0.5">
                      {evalResult.strengths.map((s, i) => (
                        <li key={i}>{s}</li>
                      ))}
                    </ul>
                  </div>
                )}

                {evalResult.missingPoints.length > 0 && (
                  <div className="space-y-1.5">
                    <p className="text-xs font-bold text-amber-800 flex items-center gap-1.5">
                      <Lightbulb className="h-3.5 w-3.5 text-amber-600" /> Concepts to Include
                    </p>
                    <div className="flex flex-wrap gap-1.5">
                      {evalResult.missingPoints.map((mp, i) => (
                        <span key={i} className="badge bg-amber-100 text-amber-900 text-[10px]">
                          {mp}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Revealed Official Answer & Takeaways */}
            {revealed && (
              <div className="card p-6 space-y-5 animate-slide-up">
                <div>
                  <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center gap-1.5">
                    <BookOpen className="h-4 w-4 text-brand-600" /> Ideal Expected Answer
                  </h3>
                  <p className="mt-2 text-xs text-slate-700 leading-relaxed whitespace-pre-wrap bg-slate-50 p-3.5 rounded-xl border border-slate-100">
                    {selectedQuestion.expectedAnswer}
                  </p>
                </div>

                <div>
                  <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">Detailed Explanation</h3>
                  <div className="mt-2 text-xs text-slate-700 leading-relaxed whitespace-pre-wrap">
                    {selectedQuestion.explanation}
                  </div>
                </div>

                {interviewPoints.length > 0 && (
                  <div className="space-y-1.5">
                    <h3 className="text-xs font-bold text-indigo-900 uppercase tracking-wider flex items-center gap-1.5">
                      <Sparkles className="h-4 w-4 text-indigo-600" /> Interview Takeaways & Key Concepts
                    </h3>
                    <ul className="list-disc pl-5 text-xs text-slate-700 space-y-1">
                      {interviewPoints.map((pt, idx) => (
                        <li key={idx}>{pt}</li>
                      ))}
                    </ul>
                  </div>
                )}

                {commonMistakes.length > 0 && (
                  <div className="p-4 rounded-xl bg-red-50/50 border border-red-100 space-y-1.5">
                    <h3 className="text-xs font-bold text-red-900 flex items-center gap-1.5">
                      <AlertTriangle className="h-4 w-4 text-red-600" /> Common Mistakes to Avoid
                    </h3>
                    <ul className="list-disc pl-5 text-xs text-red-800 space-y-0.5">
                      {commonMistakes.map((m, idx) => (
                        <li key={idx}>{m}</li>
                      ))}
                    </ul>
                  </div>
                )}

                {followUpQuestions.length > 0 && (
                  <div className="space-y-1.5">
                    <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                      Expected Follow-Up Questions
                    </h3>
                    <ul className="list-disc pl-5 text-xs text-slate-600 space-y-0.5">
                      {followUpQuestions.map((fq, idx) => (
                        <li key={idx}>{fq}</li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Right Sidebar: Other Questions in this topic */}
          <div className="lg:col-span-4 space-y-3">
            <div className="card p-4 space-y-3">
              <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                Related Interview Questions
              </h3>
              <div className="space-y-2">
                {questions.map((q) => (
                  <button
                    key={q.id}
                    onClick={() => handleSelectQuestion(q.id)}
                    className={`w-full text-left p-3 rounded-xl border text-xs transition-all flex items-start justify-between gap-2 ${
                      selectedQuestion.id === q.id
                        ? 'border-brand-500 bg-brand-50/30 font-semibold text-brand-900'
                        : 'border-slate-100 hover:border-slate-200 text-slate-700 hover:bg-slate-50'
                    }`}
                  >
                    <span className="line-clamp-2">{q.questionText}</span>
                    <ChevronRight className="h-4 w-4 text-slate-400 flex-shrink-0 mt-0.5" />
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
