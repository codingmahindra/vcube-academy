import React, { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { careerApi, type ActionRecommendation } from '../../api/career';
import {
  Bot, User, Send, Sparkles, AlertCircle, ArrowRight,
  RefreshCw, FileText, Code2, Brain, Calendar, ShieldCheck
} from 'lucide-react';

interface ChatMessage {
  id: string;
  sender: 'USER' | 'COPILOT';
  text: string;
  recommendedActions?: ActionRecommendation[];
  aiProvider?: string;
  timestamp: string;
}

const PROMPT_SUGGESTIONS = [
  'What skills am I missing for Java Developer roles?',
  'How do I improve my ATS Resume score for product companies?',
  'What DSA topics should I prioritize before TCS NQT exam?',
  'Create my target daily preparation plan for today',
  'What should I practice for Spring Boot Microservices interviews?',
];

export default function CareerCopilotPage() {
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: 'welcome',
      sender: 'COPILOT',
      text: "Hello! I am your AI Career Copilot for the VCUBE Java Full Stack Academy. I analyze your real academic progress, MCQ scores, DSA submissions, ATS resume keywords, and company interview requirements.\n\nHow can I accelerate your placement journey today?",
      aiProvider: 'RULE_BASED',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      recommendedActions: [
        { label: 'View Career Roadmap', actionType: 'ROADMAP', link: '/student/career/roadmap' },
        { label: 'Check Daily Plan', actionType: 'DAILY_PLAN', link: '/student/career/daily-plan' },
      ],
    },
  ]);
  const [inputText, setInputText] = useState('');
  const [conversationId, setConversationId] = useState<number | undefined>(undefined);
  const [isSending, setIsSending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const chatEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isSending]);

  const handleSend = async (e?: React.FormEvent, customPrompt?: string) => {
    if (e) e.preventDefault();
    const query = (customPrompt || inputText).trim();
    if (!query || isSending) return;

    const userMsg: ChatMessage = {
      id: 'user-' + Date.now(),
      sender: 'USER',
      text: query,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInputText('');
    setIsSending(true);
    setError(null);

    try {
      const resp = await careerApi.chatWithCopilot(query, conversationId);
      if (resp.conversationId) setConversationId(resp.conversationId);

      const copilotMsg: ChatMessage = {
        id: 'copilot-' + Date.now(),
        sender: 'COPILOT',
        text: resp.responseText,
        recommendedActions: resp.recommendedActions,
        aiProvider: resp.aiProvider,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };

      setMessages((prev) => [...prev, copilotMsg]);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to communicate with Career Copilot');
    } finally {
      setIsSending(false);
    }
  };

  const getActionIcon = (type: string) => {
    switch (type) {
      case 'RESUME':
        return <FileText className="h-3.5 w-3.5" />;
      case 'DSA':
        return <Code2 className="h-3.5 w-3.5" />;
      case 'INTERVIEW':
      case 'MOCK':
        return <Brain className="h-3.5 w-3.5" />;
      case 'DAILY_PLAN':
        return <Calendar className="h-3.5 w-3.5" />;
      default:
        return <ShieldCheck className="h-3.5 w-3.5" />;
    }
  };

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)] max-w-5xl mx-auto rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
      {/* Header */}
      <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200 bg-slate-50/80">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-600 text-white shadow-md">
            <Bot className="h-5 w-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-base font-bold text-slate-900">AI Career Copilot</h1>
              <span className="inline-flex items-center gap-1 rounded-full bg-emerald-100 px-2 py-0.5 text-[11px] font-semibold text-emerald-800">
                <Sparkles className="h-3 w-3" /> Live Context Aware
              </span>
            </div>
            <p className="text-xs text-slate-500">
              Grounded in your Academy performance, ATS keyword analysis & placement papers
            </p>
          </div>
        </div>

        <button
          onClick={() => {
            setMessages([messages[0]]);
            setConversationId(undefined);
          }}
          className="inline-flex items-center gap-1.5 rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-xs font-semibold text-slate-600 hover:bg-slate-50 transition-colors"
        >
          <RefreshCw className="h-3.5 w-3.5" /> Clear Session
        </button>
      </div>

      {/* Message Stream */}
      <div className="flex-1 overflow-y-auto p-6 space-y-6 bg-slate-50/30">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex gap-3.5 max-w-3xl ${
              msg.sender === 'USER' ? 'ml-auto flex-row-reverse' : ''
            }`}
          >
            <div
              className={`flex h-9 w-9 flex-shrink-0 items-center justify-center rounded-xl text-white shadow-sm ${
                msg.sender === 'USER' ? 'bg-slate-800' : 'bg-indigo-600'
              }`}
            >
              {msg.sender === 'USER' ? <User className="h-4 w-4" /> : <Bot className="h-4 w-4" />}
            </div>

            <div className="space-y-2">
              <div
                className={`rounded-2xl px-5 py-3.5 shadow-sm text-sm leading-relaxed ${
                  msg.sender === 'USER'
                    ? 'bg-indigo-600 text-white rounded-tr-none'
                    : 'bg-white border border-slate-200 text-slate-800 rounded-tl-none'
                }`}
              >
                <div className="whitespace-pre-wrap">{msg.text}</div>
              </div>

              {msg.recommendedActions && msg.recommendedActions.length > 0 && (
                <div className="flex flex-wrap gap-2 pt-1">
                  {msg.recommendedActions.map((action, idx) => (
                    <Link
                      key={idx}
                      to={action.link}
                      className="inline-flex items-center gap-1.5 rounded-lg border border-indigo-200 bg-indigo-50/70 px-3 py-1 text-xs font-semibold text-indigo-700 hover:bg-indigo-100 hover:border-indigo-300 transition-colors shadow-xs"
                    >
                      {getActionIcon(action.actionType)}
                      {action.label}
                      <ArrowRight className="h-3 w-3" />
                    </Link>
                  ))}
                </div>
              )}

              <div
                className={`flex items-center gap-2 text-[11px] text-slate-400 ${
                  msg.sender === 'USER' ? 'justify-end' : ''
                }`}
              >
                <span>{msg.timestamp}</span>
                {msg.aiProvider && (
                  <span className="font-mono text-[10px] uppercase text-slate-400">
                    • {msg.aiProvider}
                  </span>
                )}
              </div>
            </div>
          </div>
        ))}

        {isSending && (
          <div className="flex gap-3.5 max-w-3xl">
            <div className="flex h-9 w-9 flex-shrink-0 items-center justify-center rounded-xl bg-indigo-600 text-white">
              <Bot className="h-4 w-4" />
            </div>
            <div className="rounded-2xl rounded-tl-none border border-slate-200 bg-white px-5 py-3.5 shadow-sm">
              <div className="flex items-center gap-2 text-xs text-slate-500">
                <span className="h-2 w-2 animate-ping rounded-full bg-indigo-600"></span>
                Synthesizing recommendations from your academic record...
              </div>
            </div>
          </div>
        )}

        {error && (
          <div className="rounded-xl border border-red-200 bg-red-50 p-3 text-xs text-red-700 flex items-center gap-2">
            <AlertCircle className="h-4 w-4 flex-shrink-0" />
            {error}
          </div>
        )}

        <div ref={chatEndRef} />
      </div>

      {/* Suggested Prompts */}
      <div className="px-6 py-2.5 bg-slate-50 border-t border-slate-200 flex items-center gap-2 overflow-x-auto no-scrollbar">
        <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400 flex-shrink-0">
          Suggested:
        </span>
        {PROMPT_SUGGESTIONS.map((p, idx) => (
          <button
            key={idx}
            type="button"
            onClick={() => handleSend(undefined, p)}
            className="rounded-full border border-slate-200 bg-white px-3 py-1 text-xs text-slate-700 hover:border-indigo-400 hover:text-indigo-600 transition-colors whitespace-nowrap"
          >
            {p}
          </button>
        ))}
      </div>

      {/* Chat Input */}
      <form onSubmit={handleSend} className="p-4 border-t border-slate-200 bg-white flex gap-3">
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="Ask anything regarding your skill gaps, ATS keywords, interview rounds, or daily schedule..."
          className="flex-1 rounded-xl border border-slate-200 bg-slate-50/50 px-4 py-2.5 text-sm text-slate-800 placeholder-slate-400 focus:bg-white focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-100 transition-all"
        />
        <button
          type="submit"
          disabled={!inputText.trim() || isSending}
          className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white shadow-md hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <Send className="h-4 w-4" /> Send
        </button>
      </form>
    </div>
  );
}
