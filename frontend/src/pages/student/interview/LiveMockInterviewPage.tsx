import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  interviewApi,
  type MockInterviewResponse,
  type MockInterviewQuestionDto,
} from '../../../api/interview';
import {
  Clock, Send, ArrowRight, CheckCircle2, AlertTriangle,
  Lightbulb, Star, Loader2, Sparkles, HelpCircle
} from 'lucide-react';
import toast from 'react-hot-toast';

export function LiveMockInterviewPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [mock, setMock] = useState<MockInterviewResponse | null>(null);
  const [currentIdx, setCurrentIdx] = useState(0);
  const [userAnswer, setUserAnswer] = useState('');
  const [evaluating, setEvaluating] = useState(false);
  const [completing, setCompleting] = useState(false);
  const [loading, setLoading] = useState(true);
  const [questionFeedback, setQuestionFeedback] = useState<MockInterviewQuestionDto | null>(null);
  const [timerSeconds, setTimerSeconds] = useState(0);

  useEffect(() => {
    async function loadMock() {
      if (!id) return;
      try {
        setLoading(true);
        const data = await interviewApi.getMockInterview(Number(id));
        if (data.status === 'COMPLETED') {
          navigate(`/student/interview/result/${data.id}`);
          return;
        }
        setMock(data);
      } catch (err) {
        toast.error('Failed to load mock interview');
        navigate('/student/interview/mock');
      } finally {
        setLoading(false);
      }
    }
    loadMock();
  }, [id, navigate]);

  // Timer counter
  useEffect(() => {
    const interval = setInterval(() => {
      setTimerSeconds((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  async function handleSubmitAnswer() {
    if (!mock || !userAnswer.trim()) {
      toast.error('Please articulate your response before submitting');
      return;
    }
    const currentQ = mock.questions[currentIdx];
    try {
      setEvaluating(true);
      const res = await interviewApi.answerMockQuestion(
        mock.id,
        currentQ.questionOrder,
        userAnswer,
        timerSeconds
      );
      setQuestionFeedback(res);
      toast.success('Answer evaluated!');
    } catch (err) {
      toast.error('Failed to evaluate answer');
    } finally {
      setEvaluating(false);
    }
  }

  async function handleNextQuestion() {
    if (!mock) return;
    if (currentIdx + 1 < mock.questions.length) {
      setCurrentIdx((prev) => prev + 1);
      setUserAnswer('');
      setQuestionFeedback(null);
      setTimerSeconds(0);
    } else {
      // Complete mock interview
      try {
        setCompleting(true);
        const result = await interviewApi.completeMockInterview(mock.id);
        toast.success('Mock interview completed! Generating report...');
        navigate(`/student/interview/result/${mock.id}`);
      } catch (err) {
        toast.error('Failed to complete mock interview');
      } finally {
        setCompleting(false);
      }
    }
  }

  if (loading || !mock) {
    return (
      <div className="flex h-72 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-brand-600" />
      </div>
    );
  }

  const currentQ = mock.questions[currentIdx];
  const isLastQuestion = currentIdx + 1 === mock.questions.length;
  const minutes = Math.floor(timerSeconds / 60);
  const seconds = timerSeconds % 60;
  const timeFormatted = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

  return (
    <div className="space-y-6 max-w-4xl mx-auto pb-12">
      {/* Session Top Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-4 rounded-2xl bg-white border border-slate-100 shadow-sm">
        <div>
          <span className="text-[11px] font-bold text-brand-600 uppercase tracking-wider">
            {mock.targetCompanyName || 'Technical Practice Track'}
          </span>
          <h1 className="text-base font-bold text-slate-800">{mock.title}</h1>
        </div>

        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-slate-100 text-slate-700 text-xs font-mono font-semibold">
            <Clock className="h-3.5 w-3.5 text-slate-500" />
            {timeFormatted}
          </div>
          <span className="badge bg-indigo-50 text-indigo-700 text-xs font-bold">
            Question {currentIdx + 1} of {mock.totalQuestions}
          </span>
        </div>
      </div>

      {/* Question Card */}
      <div className="card p-6 space-y-4 border-l-4 border-l-brand-600">
        <div className="flex flex-wrap items-center gap-2 text-xs">
          <span className="badge bg-slate-100 text-slate-700">{currentQ.categoryName}</span>
          <span className="badge bg-slate-100 text-slate-700">{currentQ.topicName}</span>
          <span
            className={`badge text-[10px] ${
              currentQ.difficulty === 'BASIC'
                ? 'bg-emerald-100 text-emerald-700'
                : currentQ.difficulty === 'INTERMEDIATE'
                ? 'bg-amber-100 text-amber-700'
                : 'bg-red-100 text-red-700'
            }`}
          >
            {currentQ.difficulty}
          </span>
        </div>

        <h2 className="text-lg font-bold text-slate-900 leading-snug">
          {currentQ.questionText}
        </h2>
      </div>

      {/* Answer Workspace */}
      <div className="card p-6 space-y-4">
        <label className="block text-xs font-bold text-slate-800 uppercase tracking-wider">
          Your Response (Speak / Type with structure & key terminology)
        </label>
        <textarea
          className="input min-h-[160px] text-xs leading-relaxed"
          placeholder="Explain the concept clearly, covering definitions, internal mechanisms, code patterns, and practical trade-offs..."
          value={userAnswer}
          disabled={!!questionFeedback}
          onChange={(e) => setUserAnswer(e.target.value)}
        />

        {!questionFeedback ? (
          <div className="flex justify-end pt-2">
            <button
              onClick={handleSubmitAnswer}
              disabled={evaluating || !userAnswer.trim()}
              className="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-brand-600 text-white font-semibold text-xs hover:bg-brand-500 shadow-md shadow-brand-600/30 transition-all disabled:opacity-50"
            >
              {evaluating ? <Loader2 className="h-4 w-4 animate-spin" /> : <Send className="h-4 w-4" />}
              Submit Response
            </button>
          </div>
        ) : null}
      </div>

      {/* Immediate Evaluation Feedback */}
      {questionFeedback && (
        <div className="card p-6 space-y-4 border-2 border-brand-200 bg-brand-50/20 animate-fade-in">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Star className="h-5 w-5 text-amber-500 fill-amber-400" />
              <h3 className="text-base font-bold text-slate-900">
                Evaluation Score: {questionFeedback.score} / 100
              </h3>
            </div>
            <button
              onClick={handleNextQuestion}
              disabled={completing}
              className="inline-flex items-center gap-2 px-5 py-2 rounded-xl bg-brand-600 text-white font-semibold text-xs hover:bg-brand-500 shadow-md transition-all disabled:opacity-50"
            >
              {completing ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : isLastQuestion ? (
                'Finish & View Final Report'
              ) : (
                <>Next Question <ArrowRight className="h-4 w-4" /></>
              )}
            </button>
          </div>

          <p className="text-xs text-slate-700 leading-relaxed font-medium">
            {questionFeedback.feedback}
          </p>

          {questionFeedback.missingPoints && questionFeedback.missingPoints.length > 0 && (
            <div className="space-y-1.5">
              <p className="text-xs font-bold text-amber-800 flex items-center gap-1.5">
                <Lightbulb className="h-3.5 w-3.5 text-amber-600" /> Concepts to Mention in Live Rounds
              </p>
              <div className="flex flex-wrap gap-1.5">
                {questionFeedback.missingPoints.map((mp, i) => (
                  <span key={i} className="badge bg-amber-100 text-amber-900 text-[10px]">
                    {mp}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
