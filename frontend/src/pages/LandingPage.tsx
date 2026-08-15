import { Link } from 'react-router-dom';
import {
  ArrowRight, CheckCircle2, Code2, Brain, FileText, Mic,
  Briefcase, Trophy, Users, BookOpen, Zap, Star,
  ChevronRight, GraduationCap, Target, Layers, BarChart2,
} from 'lucide-react';

// ─── Data ─────────────────────────────────────────────────────────────────────

const ROADMAP_STEPS = [
  { step: '01', title: 'Core Java',        desc: 'OOP, Collections, Exception Handling, Streams, Lambdas' },
  { step: '02', title: 'Advanced Java',     desc: 'JDBC, Servlets, JSP, Maven, Build Tools' },
  { step: '03', title: 'Spring Framework',  desc: 'Spring Core, MVC, Boot, Security, Data JPA' },
  { step: '04', title: 'Microservices',     desc: 'REST APIs, Spring Cloud, Docker, Kubernetes Basics' },
  { step: '05', title: 'Frontend',          desc: 'HTML, CSS, JavaScript, React, TypeScript' },
  { step: '06', title: 'Database & Cloud',  desc: 'MySQL, PostgreSQL, AWS, CI/CD, Git' },
  { step: '07', title: 'DSA & Problem Solving', desc: 'Arrays, LinkedList, Trees, Graphs, DP' },
  { step: '08', title: 'Interview Prep',    desc: 'Mock Interviews, System Design, HR Rounds, Placement' },
];

const WHY_VCUBE = [
  'Industry-relevant Java Full Stack curriculum',
  'Hands-on project-based learning',
  'Expert trainers with 10+ years experience',
  '1-on-1 mentorship & doubt clearing sessions',
  'Resume building & ATS optimization',
  'Mock interviews with real company questions',
  'Job referrals & placement support',
  'Lifetime access to recorded sessions',
];

const MODULES = [
  { icon: Code2,    title: 'Core & Advanced Java',  color: 'text-brand-600',   bg: 'bg-brand-50'   },
  { icon: Layers,   title: 'Spring Boot & REST',     color: 'text-violet-600',  bg: 'bg-violet-50'  },
  { icon: BookOpen, title: 'React & TypeScript',     color: 'text-cyan-600',    bg: 'bg-cyan-50'    },
  { icon: Target,   title: 'DSA & Problem Solving',  color: 'text-orange-600',  bg: 'bg-orange-50'  },
  { icon: Brain,    title: 'System Design',          color: 'text-emerald-600', bg: 'bg-emerald-50' },
  { icon: Mic,      title: 'Interview Preparation',  color: 'text-pink-600',    bg: 'bg-pink-50'    },
  { icon: FileText, title: 'Resume & ATS',           color: 'text-yellow-600',  bg: 'bg-yellow-50'  },
  { icon: Briefcase,title: 'Job & Placement',        color: 'text-slate-600',   bg: 'bg-slate-100'  },
];

const TRAINERS = [
  {
    name: 'SriKanth',
    role: 'Java Full Stack Expert',
    bio: '10+ years building enterprise Java applications. Former lead developer at top MNCs. Expertise in Spring Boot, Microservices, and Cloud technologies.',
  },
  {
    name: 'Viswanath',
    role: 'Full Stack & DSA Specialist',
    bio: '8+ years in software development and training. Specialized in Data Structures, System Design, React, and helping students crack top company interviews.',
  },
];

const STATS = [
  { value: '500+', label: 'Students Trained' },
  { value: '95%',  label: 'Placement Rate' },
  { value: '50+',  label: 'Hiring Partners' },
  { value: '10+',  label: 'Years Experience' },
];

// ─── Section Components ───────────────────────────────────────────────────────

function SectionHeading({ tag, title, sub }: { tag: string; title: string; sub?: string }) {
  return (
    <div className="text-center max-w-2xl mx-auto mb-12">
      <span className="inline-block mb-3 rounded-full bg-brand-50 px-3 py-1 text-xs font-semibold text-brand-700">
        {tag}
      </span>
      <h2 className="text-3xl sm:text-4xl font-bold text-slate-900 text-balance">{title}</h2>
      {sub && <p className="mt-3 text-slate-500 text-base">{sub}</p>}
    </div>
  );
}

// ─── Landing Page ─────────────────────────────────────────────────────────────

export function LandingPage() {
  return (
    <div className="overflow-x-hidden">

      {/* ── Hero ─────────────────────────────────────────────────────────────── */}
      <section className="relative min-h-[90vh] flex items-center bg-gradient-to-br from-brand-900 via-brand-800 to-brand-700 overflow-hidden">
        {/* Background texture */}
        <div className="absolute inset-0 opacity-5" style={{
          backgroundImage: 'radial-gradient(circle at 25% 25%, white 1px, transparent 0), radial-gradient(circle at 75% 75%, white 1px, transparent 0)',
          backgroundSize: '40px 40px',
        }} />

        <div className="relative mx-auto max-w-7xl px-4 sm:px-6 py-20 text-center">
          <div className="inline-flex items-center gap-2 rounded-full bg-white/10 border border-white/20 px-4 py-2 mb-8">
            <Zap className="h-3.5 w-3.5 text-accent-400" />
            <span className="text-xs text-white font-medium">India's Premier Java Full Stack Training</span>
          </div>

          <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold text-white leading-tight text-balance">
            VCUBE Java Full Stack<br />
            <span className="text-accent-400">Career Academy</span>
          </h1>

          <p className="mt-6 text-lg text-brand-200 max-w-2xl mx-auto text-balance">
            From Java basics to enterprise-grade Spring Boot apps — master the complete Full Stack roadmap with SriKanth &amp; Viswanath and land your dream tech job.
          </p>

          <div className="mt-10 flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/register" id="hero-cta-primary" className="btn-primary py-3.5 px-8 text-base bg-accent-500 hover:bg-accent-600">
              Start Learning Free <ArrowRight className="h-5 w-5" />
            </Link>
            <a href="#roadmap" className="btn-secondary py-3.5 px-8 text-base bg-white/10 border-white/20 text-white hover:bg-white/20">
              View Roadmap <ChevronRight className="h-5 w-5" />
            </a>
          </div>

          {/* Stats */}
          <div className="mt-16 grid grid-cols-2 sm:grid-cols-4 gap-6 max-w-3xl mx-auto">
            {STATS.map((s) => (
              <div key={s.label} className="text-center">
                <p className="text-3xl font-bold text-accent-400">{s.value}</p>
                <p className="text-xs text-brand-300 mt-1">{s.label}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Roadmap ───────────────────────────────────────────────────────────── */}
      <section id="roadmap" className="py-20 bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <SectionHeading
            tag="Learning Path"
            title="Java Full Stack Roadmap"
            sub="A structured 6-month journey from beginner to job-ready full stack developer"
          />
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {ROADMAP_STEPS.map((s) => (
              <div key={s.step} className="card group hover:border-brand-200 hover:scale-[1.02] transition-all duration-200">
                <span className="text-4xl font-bold text-brand-100 group-hover:text-brand-200 transition-colors">
                  {s.step}
                </span>
                <h3 className="mt-2 text-sm font-bold text-slate-900">{s.title}</h3>
                <p className="mt-1 text-xs text-slate-500">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Why VCUBE ─────────────────────────────────────────────────────────── */}
      <section className="py-20 bg-slate-50">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div>
              <span className="inline-block mb-3 rounded-full bg-brand-50 px-3 py-1 text-xs font-semibold text-brand-700">
                Why Choose Us
              </span>
              <h2 className="text-3xl sm:text-4xl font-bold text-slate-900 text-balance">
                Why VCUBE Software Solutions?
              </h2>
              <p className="mt-4 text-slate-500">
                We don't just teach code — we build careers. Our industry-first approach combines technical excellence with real-world placement support.
              </p>
            </div>
            <div className="grid sm:grid-cols-2 gap-3">
              {WHY_VCUBE.map((item) => (
                <div key={item} className="flex items-start gap-2.5 rounded-xl border border-slate-100 bg-white p-3 shadow-sm">
                  <CheckCircle2 className="h-4 w-4 text-emerald-500 flex-shrink-0 mt-0.5" />
                  <p className="text-xs font-medium text-slate-700">{item}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* ── Course Modules ────────────────────────────────────────────────────── */}
      <section id="modules" className="py-20 bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <SectionHeading
            tag="Curriculum"
            title="Complete Course Modules"
            sub="Everything you need from zero to placement-ready in one comprehensive program"
          />
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {MODULES.map((m) => {
              const Icon = m.icon;
              return (
                <div key={m.title} className="card text-center hover:scale-[1.02] transition-transform duration-200">
                  <div className={`mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-2xl ${m.bg}`}>
                    <Icon className={`h-6 w-6 ${m.color}`} />
                  </div>
                  <h3 className="text-sm font-semibold text-slate-800">{m.title}</h3>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* ── Practice + Interview + DSA row ───────────────────────────────────── */}
      <section className="py-20 bg-gradient-to-br from-brand-50 to-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <SectionHeading tag="Preparation" title="Complete Interview Preparation" />
          <div className="grid gap-6 sm:grid-cols-3">
            {[
              {
                icon: Target,
                title: 'Practice',
                color: 'text-brand-600',
                bg: 'bg-brand-50',
                items: ['Topic-wise coding exercises', '500+ curated Java problems', 'Real-time code execution', 'Solution explanations'],
              },
              {
                icon: Mic,
                title: 'Interview Prep',
                color: 'text-violet-600',
                bg: 'bg-violet-50',
                items: ['1000+ interview questions', 'Role-specific mock interviews', 'HR round preparation', 'System design walkthroughs'],
              },
              {
                icon: Code2,
                title: 'DSA Mastery',
                color: 'text-emerald-600',
                bg: 'bg-emerald-50',
                items: ['Arrays, LinkedList, Trees', 'Graphs & Dynamic Programming', 'LeetCode-style problems', 'Company-specific patterns'],
              },
            ].map((col) => {
              const Icon = col.icon;
              return (
                <div key={col.title} className="card">
                  <div className={`flex h-12 w-12 items-center justify-center rounded-2xl ${col.bg} mb-4`}>
                    <Icon className={`h-6 w-6 ${col.color}`} />
                  </div>
                  <h3 className="text-base font-bold text-slate-900 mb-3">{col.title}</h3>
                  <ul className="space-y-2">
                    {col.items.map((i) => (
                      <li key={i} className="flex items-center gap-2 text-xs text-slate-600">
                        <CheckCircle2 className="h-3.5 w-3.5 text-emerald-500 flex-shrink-0" />
                        {i}
                      </li>
                    ))}
                  </ul>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* ── Resume & ATS ─────────────────────────────────────────────────────── */}
      <section className="py-20 bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div>
              <span className="inline-block mb-3 rounded-full bg-yellow-50 px-3 py-1 text-xs font-semibold text-yellow-700">
                Career Support
              </span>
              <h2 className="text-3xl sm:text-4xl font-bold text-slate-900 text-balance">
                Resume Building &amp; ATS Optimization
              </h2>
              <p className="mt-4 text-slate-500">
                A great resume opens doors. Our expert team helps you craft a powerful resume that passes ATS filters and impresses hiring managers at top companies.
              </p>
              <ul className="mt-6 space-y-3">
                {['Professional resume templates', 'ATS keyword optimization', 'LinkedIn profile review', 'Cover letter guidance', 'Portfolio building advice'].map(i => (
                  <li key={i} className="flex items-center gap-2 text-sm text-slate-700">
                    <Star className="h-4 w-4 text-accent-500 flex-shrink-0" />
                    {i}
                  </li>
                ))}
              </ul>
            </div>
            <div className="rounded-3xl bg-gradient-to-br from-yellow-50 to-orange-50 border border-yellow-100 p-8">
              <div className="space-y-4">
                {[
                  { label: 'ATS Score',      value: 92, color: 'bg-emerald-500' },
                  { label: 'Keyword Match',  value: 88, color: 'bg-brand-500' },
                  { label: 'Format Quality', value: 95, color: 'bg-violet-500' },
                  { label: 'Impact Score',   value: 85, color: 'bg-orange-500' },
                ].map((bar) => (
                  <div key={bar.label}>
                    <div className="flex justify-between text-xs mb-1">
                      <span className="font-medium text-slate-700">{bar.label}</span>
                      <span className="font-bold text-slate-800">{bar.value}%</span>
                    </div>
                    <div className="h-2 rounded-full bg-slate-200">
                      <div
                        className={`h-2 rounded-full ${bar.color} transition-all`}
                        style={{ width: `${bar.value}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
              <p className="mt-6 text-xs text-slate-500 text-center">Sample ATS Analysis Report</p>
            </div>
          </div>
        </div>
      </section>

      {/* ── Jobs + Placement ─────────────────────────────────────────────────── */}
      <section className="py-20 bg-slate-50">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <SectionHeading
            tag="Placement"
            title="Latest Java Jobs & Placement"
            sub="We connect you directly with hiring companies actively looking for Java Full Stack developers"
          />
          <div className="grid sm:grid-cols-3 gap-6">
            {[
              { icon: Briefcase, title: 'Java Job Board',      desc: '500+ curated Java Full Stack job openings updated daily from top companies', color: 'text-brand-600', bg: 'bg-brand-50' },
              { icon: Users,     title: 'Placement Papers',    desc: 'Company-specific interview questions, coding rounds & HR questions from past drives', color: 'text-violet-600', bg: 'bg-violet-50' },
              { icon: Trophy,    title: 'Success Stories',     desc: 'Our students are placed at TCS, Infosys, Wipro, Capgemini, and 50+ product companies', color: 'text-emerald-600', bg: 'bg-emerald-50' },
            ].map((card) => {
              const Icon = card.icon;
              return (
                <div key={card.title} className="card text-center hover:scale-[1.01] transition-transform duration-200">
                  <div className={`mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl ${card.bg}`}>
                    <Icon className={`h-7 w-7 ${card.color}`} />
                  </div>
                  <h3 className="text-base font-bold text-slate-900">{card.title}</h3>
                  <p className="mt-2 text-xs text-slate-500">{card.desc}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* ── Trainers ─────────────────────────────────────────────────────────── */}
      <section id="trainers" className="py-20 bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <SectionHeading tag="Expert Faculty" title="Meet Your Trainers" />
          <div className="grid sm:grid-cols-2 gap-8 max-w-3xl mx-auto">
            {TRAINERS.map((t) => (
              <div key={t.name} className="card text-center hover:scale-[1.01] transition-transform duration-200">
                <div className="mx-auto mb-4 flex h-20 w-20 items-center justify-center rounded-3xl bg-brand-100">
                  <span className="text-3xl font-bold text-brand-700">{t.name[0]}</span>
                </div>
                <h3 className="text-lg font-bold text-slate-900">{t.name}</h3>
                <p className="text-xs font-semibold text-brand-600 mt-1">{t.role}</p>
                <p className="mt-3 text-sm text-slate-500">{t.bio}</p>
                <div className="mt-4 flex justify-center gap-2">
                  <span className="badge bg-brand-50 text-brand-700">Java Expert</span>
                  <span className="badge bg-emerald-50 text-emerald-700">Full Stack</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA ──────────────────────────────────────────────────────────────── */}
      <section className="py-20 bg-gradient-to-r from-brand-800 to-brand-600">
        <div className="mx-auto max-w-3xl px-4 sm:px-6 text-center">
          <GraduationCap className="mx-auto h-14 w-14 text-accent-400 mb-6" />
          <h2 className="text-3xl sm:text-4xl font-bold text-white text-balance">
            Ready to launch your Java Full Stack career?
          </h2>
          <p className="mt-4 text-brand-200 text-base">
            Join 500+ students who transformed their careers with VCUBE. Enroll today and get access to all courses, live sessions, and placement support.
          </p>
          <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/register" id="cta-enroll" className="btn-primary py-3.5 px-8 text-base bg-accent-500 hover:bg-accent-600">
              Enroll Now — It's Free <ArrowRight className="h-5 w-5" />
            </Link>
            <Link to="/login" className="btn-secondary py-3.5 px-8 text-base bg-white/10 border-white/20 text-white hover:bg-white/20">
              Sign In
            </Link>
          </div>
        </div>
      </section>

      {/* ── Footer ───────────────────────────────────────────────────────────── */}
      <footer className="bg-slate-900 text-slate-400 py-12">
        <div className="mx-auto max-w-7xl px-4 sm:px-6">
          <div className="grid sm:grid-cols-3 gap-8 mb-8">
            <div>
              <div className="flex items-center gap-2.5 mb-3">
                <GraduationCap className="h-6 w-6 text-brand-400" />
                <span className="font-bold text-white text-sm">VCUBE Software Solutions</span>
              </div>
              <p className="text-xs leading-relaxed">
                India's leading Java Full Stack training institute. Empowering developers to build, deploy, and succeed.
              </p>
            </div>
            <div>
              <h4 className="text-xs font-semibold text-white uppercase tracking-wider mb-3">Program</h4>
              <ul className="space-y-2 text-xs">
                {['Java Full Stack', 'DSA & Problem Solving', 'System Design', 'Interview Prep', 'Placement Support'].map(l => (
                  <li key={l}><a href="#" className="hover:text-white transition-colors">{l}</a></li>
                ))}
              </ul>
            </div>
            <div>
              <h4 className="text-xs font-semibold text-white uppercase tracking-wider mb-3">Company</h4>
              <ul className="space-y-2 text-xs">
                {['About Us', 'Trainers', 'Success Stories', 'Contact'].map(l => (
                  <li key={l}><a href="#" className="hover:text-white transition-colors">{l}</a></li>
                ))}
              </ul>
            </div>
          </div>
          <div className="border-t border-slate-800 pt-6 flex flex-col sm:flex-row items-center justify-between gap-2 text-xs">
            <p>© {new Date().getFullYear()} VCUBE Software Solutions. All rights reserved.</p>
            <p>Trainers: SriKanth &amp; Viswanath</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
