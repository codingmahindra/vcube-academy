import React, { useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { topicsApi } from '../../api/topics';
import toast from 'react-hot-toast';
import {
  ArrowLeft, Loader2, AlertCircle, CheckCircle2, Brain, Code2,
  Lightbulb, MessageSquare, Star, ChevronDown, ChevronUp,
} from 'lucide-react';

function Section({
  title,
  icon: Icon,
  children,
  defaultOpen = true,
}: {
  title: string;
  icon: React.ElementType;
  children: React.ReactNode;
  defaultOpen?: boolean;
}) {
  const [open, setOpen] = useState(defaultOpen);
  return (
    <div className="card p-0 overflow-hidden">
      <button
        onClick={() => setOpen((v) => !v)}
        className="flex w-full items-center justify-between gap-3 px-5 py-4 bg-slate-50 hover:bg-slate-100 transition-colors text-left"
      >
        <div className="flex items-center gap-2.5">
          <Icon className="h-4.5 w-4.5 text-brand-600 flex-shrink-0" style={{ width: 18, height: 18 }} />
          <span className="font-semibold text-slate-800 text-sm">{title}</span>
        </div>
        {open ? <ChevronUp className="h-4 w-4 text-slate-400" /> : <ChevronDown className="h-4 w-4 text-slate-400" />}
      </button>
      {open && <div className="px-5 py-4">{children}</div>}
    </div>
  );
}

export function TopicViewerPage() {
  const { id } = useParams<{ id: string }>();
  const topicId = Number(id);
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { data: topic, isLoading, isError, error } = useQuery({
    queryKey: ['topic', topicId],
    queryFn: () => topicsApi.getById(topicId),
    enabled: !!topicId,
  });

  const { data: isCompleted, refetch: refetchCompletion } = useQuery({
    queryKey: ['topic-completion', topicId],
    queryFn: () => topicsApi.checkCompletion(topicId),
    enabled: !!topicId,
  });

  const completeMutation = useMutation({
    mutationFn: () => topicsApi.markComplete(topicId),
    onSuccess: () => {
      toast.success('Topic marked as complete! 🎉');
      refetchCompletion();
      queryClient.invalidateQueries({ queryKey: ['progress'] });
    },
    onError: () => toast.error('Failed to mark as complete'),
  });

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 gap-4">
        <Loader2 className="h-8 w-8 animate-spin text-brand-500" />
        <p className="text-sm text-slate-500">Loading topic…</p>
      </div>
    );
  }

  if (isError || !topic) {
    return (
      <div className="flex flex-col items-center justify-center py-20 gap-4 text-center">
        <div className="flex h-14 w-14 items-center justify-center rounded-full bg-red-50">
          <AlertCircle className="h-7 w-7 text-red-500" />
        </div>
        <div>
          <p className="font-semibold text-slate-800">Failed to load topic</p>
          <p className="text-sm text-slate-500 mt-1">
            {(error as any)?.response?.data?.message ?? 'Topic not found.'}
          </p>
        </div>
        <button onClick={() => navigate(-1)} className="btn-secondary text-sm">
          ← Go Back
        </button>
      </div>
    );
  }

  const content = topic.content;

  return (
    <div className="space-y-5 animate-fade-in max-w-3xl mx-auto">
      {/* Breadcrumb */}
      <div className="flex items-center gap-2 text-xs text-slate-400 flex-wrap">
        <Link to="/student/courses" className="hover:text-brand-600 transition-colors">
          Courses
        </Link>
        <span>/</span>
        <Link
          to={`/student/courses/${topic.courseId}`}
          className="hover:text-brand-600 transition-colors"
        >
          {topic.courseTitle}
        </Link>
        <span>/</span>
        <span className="text-slate-600 font-medium truncate">{topic.title}</span>
      </div>

      {/* Topic Header */}
      <div className="card">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div className="space-y-1">
            <span className="badge bg-brand-50 text-brand-600 text-xs">{topic.moduleTitle}</span>
            <h1 className="text-lg font-bold text-slate-900 mt-1">{topic.title}</h1>
          </div>
          <div className="flex flex-col items-end gap-2">
            {isCompleted ? (
              <span className="flex items-center gap-1.5 badge bg-emerald-50 text-emerald-700 px-3 py-1.5">
                <CheckCircle2 className="h-4 w-4" /> Completed
              </span>
            ) : (
              <button
                className="btn-primary py-2 px-4 text-sm"
                onClick={() => completeMutation.mutate()}
                disabled={completeMutation.isPending}
              >
                {completeMutation.isPending ? (
                  <><Loader2 className="h-4 w-4 animate-spin" /> Marking…</>
                ) : (
                  <><CheckCircle2 className="h-4 w-4" /> Mark Complete</>
                )}
              </button>
            )}
            {topic.questionCount > 0 && (
              <Link
                to={`/student/quiz/topic/${topic.id}`}
                className="btn-secondary py-2 px-4 text-sm"
              >
                <Brain className="h-4 w-4" /> Take Quiz ({topic.questionCount} Qs)
              </Link>
            )}
          </div>
        </div>

        <div className="mt-3 flex items-center gap-3 text-xs text-slate-500">
          <span className="badge bg-slate-100 text-slate-600 capitalize">
            {topic.difficulty.charAt(0) + topic.difficulty.slice(1).toLowerCase()}
          </span>
          {topic.estimatedMinutes && (
            <span>~{topic.estimatedMinutes} min</span>
          )}
        </div>
      </div>

      {/* Content Sections */}
      {content ? (
        <>
          {content.explanation && (
            <Section title="Explanation" icon={Lightbulb}>
              <p className="text-sm text-slate-700 leading-relaxed whitespace-pre-wrap">
                {content.explanation}
              </p>
            </Section>
          )}

          {content.simpleExplanation && (
            <Section title="Simple Explanation (ELI5)" icon={MessageSquare} defaultOpen={false}>
              <p className="text-sm text-slate-700 leading-relaxed whitespace-pre-wrap">
                {content.simpleExplanation}
              </p>
            </Section>
          )}

          {content.realWorldExample && (
            <Section title="Real World Example" icon={Star} defaultOpen={false}>
              <p className="text-sm text-slate-700 leading-relaxed whitespace-pre-wrap">
                {content.realWorldExample}
              </p>
            </Section>
          )}

          {(content.syntaxExample || content.codeExample) && (
            <Section title="Code Example" icon={Code2} defaultOpen={false}>
              {content.syntaxExample && (
                <div className="mb-3">
                  <p className="text-xs font-semibold text-slate-500 mb-1.5 uppercase tracking-wide">Syntax</p>
                  <pre className="bg-slate-900 text-emerald-400 rounded-xl p-4 text-xs overflow-x-auto font-mono leading-relaxed">
                    {content.syntaxExample}
                  </pre>
                </div>
              )}
              {content.codeExample && (
                <div>
                  <p className="text-xs font-semibold text-slate-500 mb-1.5 uppercase tracking-wide">
                    {content.codeLanguage ?? 'java'} Example
                  </p>
                  <pre className="bg-slate-900 text-cyan-300 rounded-xl p-4 text-xs overflow-x-auto font-mono leading-relaxed">
                    {content.codeExample}
                  </pre>
                </div>
              )}
            </Section>
          )}

          {content.interviewPoints && (
            <Section title="Interview Points" icon={Star} defaultOpen={false}>
              <div className="space-y-1.5">
                {content.interviewPoints.split('\n').filter(Boolean).map((pt, i) => (
                  <div key={i} className="flex gap-2 text-sm text-slate-700">
                    <span className="text-brand-500 font-bold flex-shrink-0">•</span>
                    <span className="leading-relaxed">{pt.replace(/^[-•*]\s*/, '')}</span>
                  </div>
                ))}
              </div>
            </Section>
          )}

          {content.commonMistakes && (
            <Section title="Common Mistakes" icon={AlertCircle} defaultOpen={false}>
              <div className="space-y-1.5">
                {content.commonMistakes.split('\n').filter(Boolean).map((pt, i) => (
                  <div key={i} className="flex gap-2 text-sm text-slate-700">
                    <span className="text-red-400 font-bold flex-shrink-0">✕</span>
                    <span className="leading-relaxed">{pt.replace(/^[-•*]\s*/, '')}</span>
                  </div>
                ))}
              </div>
            </Section>
          )}

          {content.practiceQuestions && (
            <Section title="Practice Questions" icon={Brain} defaultOpen={false}>
              <div className="space-y-1.5">
                {content.practiceQuestions.split('\n').filter(Boolean).map((pt, i) => (
                  <div key={i} className="flex gap-2 text-sm text-slate-700">
                    <span className="text-brand-500 font-bold flex-shrink-0">{i + 1}.</span>
                    <span className="leading-relaxed">{pt.replace(/^\d+\.\s*/, '')}</span>
                  </div>
                ))}
              </div>
            </Section>
          )}
        </>
      ) : (
        <div className="card text-center py-10 text-sm text-slate-400">
          Content for this topic is being prepared. Check back soon!
        </div>
      )}

      {/* Bottom action row */}
      <div className="flex items-center justify-between pt-2 pb-6">
        <button onClick={() => navigate(-1)} className="btn-ghost text-sm">
          <ArrowLeft className="h-4 w-4" /> Go Back
        </button>
        {!isCompleted && (
          <button
            className="btn-primary text-sm"
            onClick={() => completeMutation.mutate()}
            disabled={completeMutation.isPending}
          >
            {completeMutation.isPending ? (
              <><Loader2 className="h-4 w-4 animate-spin" /> Marking…</>
            ) : (
              <><CheckCircle2 className="h-4 w-4" /> Mark Complete</>
            )}
          </button>
        )}
      </div>
    </div>
  );
}
