import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { quizApi } from '../../api/quiz';
import type { QuizAttemptDto, AnswerFeedbackDto } from '../../types/quiz';
import { Loader2, AlertCircle, CheckCircle2, XCircle, ChevronRight, Brain } from 'lucide-react';

type Phase =
  | { type: 'loading' }
  | { type: 'starting' }
  | { type: 'question'; attempt: QuizAttemptDto }
  | { type: 'feedback'; attempt: QuizAttemptDto; feedback: AnswerFeedbackDto }
  | { type: 'completing' }
  | { type: 'error'; message: string };

function ProgressBar({ current, total }: { current: number; total: number }) {
  const pct = total > 0 ? Math.min(100, (current / total) * 100) : 0;
  return (
    <div className="w-full">
      <div className="flex justify-between text-xs text-slate-500 mb-1">
        <span>Question {current} of {total}</span>
        <span>{Math.round(pct)}% done</span>
      </div>
      <div className="h-1.5 w-full rounded-full bg-slate-100 overflow-hidden">
        <div
          className="h-full rounded-full bg-brand-500 transition-all duration-500"
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  );
}

export function QuizPage() {
  const { topicId, courseId } = useParams<{ topicId?: string; courseId?: string }>();
  const navigate = useNavigate();
  const [phase, setPhase] = useState<Phase>({ type: 'starting' });
  const [selectedOptionId, setSelectedOptionId] = useState<number | null>(null);

  // Start quiz on mount
  const startMutation = useMutation({
    mutationFn: () =>
      quizApi.start({
        quizType: topicId ? 'TOPIC_QUIZ' : courseId ? 'COURSE_QUIZ' : 'RANDOM_QUIZ',
        topicId: topicId ? Number(topicId) : undefined,
        courseId: courseId ? Number(courseId) : undefined,
      }),
    onSuccess: (data) => {
      setPhase({ type: 'question', attempt: data });
      setSelectedOptionId(null);
    },
    onError: (err: any) => {
      setPhase({
        type: 'error',
        message: err?.response?.data?.message ?? 'Failed to start quiz.',
      });
    },
  });

  useEffect(() => {
    startMutation.mutate();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const submitMutation = useMutation({
    mutationFn: ({
      attemptId,
      questionId,
      optionId,
    }: {
      attemptId: number;
      questionId: number;
      optionId: number;
    }) => quizApi.submitAnswer(attemptId, { questionId, selectedOptionId: optionId }),
    onSuccess: (feedback, vars) => {
      if (phase.type === 'question') {
        setPhase({ type: 'feedback', attempt: phase.attempt, feedback });
      }
    },
    onError: (err: any) => {
      setPhase({
        type: 'error',
        message: err?.response?.data?.message ?? 'Failed to submit answer.',
      });
    },
  });

  const completeMutation = useMutation({
    mutationFn: (attemptId: number) => quizApi.complete(attemptId),
    onSuccess: (result) => {
      navigate(`/student/quiz/result/${result.attemptId}`, { state: result });
    },
    onError: (err: any) => {
      setPhase({
        type: 'error',
        message: err?.response?.data?.message ?? 'Failed to complete quiz.',
      });
    },
  });

  function handleSubmit() {
    if (phase.type !== 'question' || selectedOptionId === null) return;
    submitMutation.mutate({
      attemptId: phase.attempt.attemptId,
      questionId: phase.attempt.currentQuestion.id,
      optionId: selectedOptionId,
    });
  }

  function handleNext() {
    if (phase.type !== 'feedback') return;
    const { feedback, attempt } = phase;
    if (feedback.isLastQuestion) {
      setPhase({ type: 'completing' });
      completeMutation.mutate(attempt.attemptId);
    } else {
      // Fetch next question
      quizApi.getCurrentQuestion(attempt.attemptId).then((q) => {
        setPhase({
          type: 'question',
          attempt: { ...attempt, currentQuestion: q, currentIndex: feedback.currentIndex + 1 },
        });
        setSelectedOptionId(null);
      });
    }
  }

  // ─── Render states ─────────────────────────────────────────────────────────

  if (phase.type === 'starting' || phase.type === 'loading' || phase.type === 'completing') {
    return (
      <div className="flex flex-col items-center justify-center py-24 gap-4">
        <Loader2 className="h-8 w-8 animate-spin text-brand-500" />
        <p className="text-sm text-slate-500">
          {phase.type === 'completing' ? 'Calculating results…' : 'Loading quiz…'}
        </p>
      </div>
    );
  }

  if (phase.type === 'error') {
    return (
      <div className="flex flex-col items-center justify-center py-20 gap-4 text-center max-w-sm mx-auto">
        <div className="flex h-14 w-14 items-center justify-center rounded-full bg-red-50">
          <AlertCircle className="h-7 w-7 text-red-500" />
        </div>
        <div>
          <p className="font-semibold text-slate-800">Quiz Error</p>
          <p className="text-sm text-slate-500 mt-1">{phase.message}</p>
        </div>
        <button onClick={() => navigate(-1)} className="btn-secondary text-sm">
          ← Go Back
        </button>
      </div>
    );
  }

  const { attempt } = phase;
  const question = attempt.currentQuestion;
  const isFeedback = phase.type === 'feedback';
  const feedback = isFeedback ? phase.feedback : null;
  const answeredCount = attempt.currentIndex + (isFeedback ? 1 : 0);

  return (
    <div className="max-w-2xl mx-auto space-y-5 animate-fade-in">
      {/* Quiz header */}
      <div className="flex items-center gap-3">
        <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-brand-100">
          <Brain className="h-5 w-5 text-brand-600" />
        </div>
        <div>
          <p className="text-xs text-slate-500">
            {attempt.topicTitle
              ? `Topic Quiz — ${attempt.topicTitle}`
              : attempt.courseTitle
              ? `Course Quiz — ${attempt.courseTitle}`
              : 'Random Quiz'}
          </p>
        </div>
      </div>

      {/* Progress */}
      <ProgressBar current={answeredCount} total={attempt.totalQuestions} />

      {/* Question card */}
      <div className="card space-y-5">
        {/* Difficulty badge */}
        <div className="flex items-center justify-between">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wide">
            Q{attempt.currentIndex + 1}
          </span>
          <span
            className={`badge text-xs ${
              question.difficulty === 'HARD'
                ? 'bg-red-100 text-red-700'
                : question.difficulty === 'MEDIUM'
                ? 'bg-amber-100 text-amber-700'
                : 'bg-emerald-100 text-emerald-700'
            }`}
          >
            {question.difficulty.charAt(0) + question.difficulty.slice(1).toLowerCase()}
          </span>
        </div>

        {/* Question text */}
        <p className="text-base font-semibold text-slate-900 leading-relaxed">
          {question.questionText}
        </p>

        {/* Options */}
        <div className="space-y-2.5">
          {question.options.map((opt) => {
            let optClass =
              'flex items-start gap-3 w-full rounded-xl border border-slate-200 px-4 py-3 text-sm text-left transition-all duration-150 cursor-pointer hover:border-brand-300 hover:bg-brand-50';

            if (isFeedback && feedback) {
              if (opt.id === feedback.correctOptionId) {
                optClass =
                  'flex items-start gap-3 w-full rounded-xl border-2 border-emerald-400 bg-emerald-50 px-4 py-3 text-sm text-left';
              } else if (
                opt.id === feedback.selectedOptionId &&
                opt.id !== feedback.correctOptionId
              ) {
                optClass =
                  'flex items-start gap-3 w-full rounded-xl border-2 border-red-400 bg-red-50 px-4 py-3 text-sm text-left';
              } else {
                optClass =
                  'flex items-start gap-3 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-left opacity-60';
              }
            } else if (selectedOptionId === opt.id) {
              optClass =
                'flex items-start gap-3 w-full rounded-xl border-2 border-brand-500 bg-brand-50 px-4 py-3 text-sm text-left';
            }

            return (
              <button
                key={opt.id}
                className={optClass}
                disabled={isFeedback}
                onClick={() => !isFeedback && setSelectedOptionId(opt.id)}
              >
                <span className="flex h-5 w-5 flex-shrink-0 items-center justify-center rounded-full border text-xs font-bold border-current">
                  {opt.optionLabel}
                </span>
                <span className="flex-1 leading-relaxed">{opt.optionText}</span>
                {isFeedback && opt.id === feedback?.correctOptionId && (
                  <CheckCircle2 className="h-4 w-4 text-emerald-500 flex-shrink-0 mt-0.5" />
                )}
                {isFeedback &&
                  opt.id === feedback?.selectedOptionId &&
                  opt.id !== feedback?.correctOptionId && (
                    <XCircle className="h-4 w-4 text-red-500 flex-shrink-0 mt-0.5" />
                  )}
              </button>
            );
          })}
        </div>

        {/* Submit button (before answer) */}
        {!isFeedback && (
          <button
            className="btn-primary w-full justify-center"
            disabled={selectedOptionId === null || submitMutation.isPending}
            onClick={handleSubmit}
          >
            {submitMutation.isPending ? (
              <><Loader2 className="h-4 w-4 animate-spin" /> Submitting…</>
            ) : (
              'Submit Answer'
            )}
          </button>
        )}

        {/* Feedback section (after answer) */}
        {isFeedback && feedback && (
          <div
            className={`rounded-xl p-4 space-y-2 ${
              feedback.isCorrect ? 'bg-emerald-50 border border-emerald-200' : 'bg-red-50 border border-red-200'
            }`}
          >
            <div className="flex items-center gap-2">
              {feedback.isCorrect ? (
                <CheckCircle2 className="h-5 w-5 text-emerald-600" />
              ) : (
                <XCircle className="h-5 w-5 text-red-500" />
              )}
              <p className={`font-semibold text-sm ${feedback.isCorrect ? 'text-emerald-700' : 'text-red-700'}`}>
                {feedback.isCorrect ? 'Correct!' : `Incorrect — Correct answer: ${feedback.correctOptionLabel}`}
              </p>
            </div>
            {feedback.explanation && (
              <p className="text-sm text-slate-700 leading-relaxed">{feedback.explanation}</p>
            )}
            {feedback.interviewPoint && (
              <div className="mt-2 p-3 bg-brand-50 rounded-lg border border-brand-100">
                <p className="text-xs font-semibold text-brand-600 mb-0.5">Interview Point</p>
                <p className="text-xs text-slate-700 leading-relaxed">{feedback.interviewPoint}</p>
              </div>
            )}
          </div>
        )}

        {/* Next / Finish button */}
        {isFeedback && feedback && (
          <button
            className="btn-primary w-full justify-center"
            onClick={handleNext}
            disabled={completeMutation.isPending}
          >
            {completeMutation.isPending ? (
              <><Loader2 className="h-4 w-4 animate-spin" /> Finishing…</>
            ) : feedback.isLastQuestion ? (
              'Finish Quiz'
            ) : (
              <>Next Question <ChevronRight className="h-4 w-4" /></>
            )}
          </button>
        )}
      </div>
    </div>
  );
}
