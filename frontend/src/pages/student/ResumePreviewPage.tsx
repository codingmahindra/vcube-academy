import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { useParams, Link } from 'react-router-dom';
import { resumeApi } from '../../api/resume';
import {
  FileText, Download, Printer, Edit3, ArrowLeft,
  Sparkles, CheckCircle2, Award, Briefcase, BookOpen, GraduationCap
} from 'lucide-react';

export function ResumePreviewPage() {
  const { id } = useParams<{ id: string }>();
  const versionId = Number(id);

  const { data: resume, isLoading } = useQuery({
    queryKey: ['resume-version-preview', versionId],
    queryFn: () => resumeApi.getVersionDetail(versionId),
    enabled: !isNaN(versionId),
  });

  if (isLoading) {
    return <div className="card p-12 text-center text-slate-400">Loading ATS Resume...</div>;
  }

  if (!resume) {
    return <div className="card p-12 text-center text-red-500">Resume version not found.</div>;
  }

  return (
    <div className="space-y-6 animate-fade-in pb-16">
      {/* Navigation & Action Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <Link to="/student/resume/resumes" className="btn-ghost text-xs flex items-center gap-1">
            <ArrowLeft className="h-4 w-4" /> Back to Resumes
          </Link>
          <div>
            <h1 className="text-xl font-bold text-slate-900">{resume.versionTitle}</h1>
            <p className="text-xs text-slate-500">
              Template: <span className="font-semibold">{resume.template}</span> • ATS Score:{' '}
              <span className="font-bold text-emerald-600">{resume.latestAtsScore || 'Not analyzed'}</span>
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <Link
            to={`/student/resume/builder?id=${resume.id}`}
            className="btn-secondary text-xs flex items-center gap-1.5"
          >
            <Edit3 className="h-4 w-4" /> Edit Resume
          </Link>
          <button
            onClick={() => window.print()}
            className="btn-secondary text-xs flex items-center gap-1.5"
          >
            <Printer className="h-4 w-4" /> Print
          </button>
          <a
            href={resumeApi.getPdfUrl(resume.id)}
            target="_blank"
            rel="noreferrer"
            className="btn-primary text-xs flex items-center gap-1.5"
          >
            <Download className="h-4 w-4" /> Download PDF
          </a>
        </div>
      </div>

      {/* A4 ATS Resume Preview Document */}
      <div className="max-w-4xl mx-auto bg-white shadow-xl rounded-2xl border border-slate-200 p-8 sm:p-12 space-y-6 print:shadow-none print:border-none print:p-0">
        {/* HEADER */}
        <div className="border-b border-slate-300 pb-4 text-center space-y-1">
          <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-slate-900 uppercase">
            {resume.fullName}
          </h1>
          <div className="flex flex-wrap items-center justify-center gap-x-3 gap-y-1 text-xs text-slate-600 font-medium">
            <span>{resume.email}</span>
            {resume.phone && <span>• {resume.phone}</span>}
            {resume.location && <span>• {resume.location}</span>}
          </div>
          <div className="flex flex-wrap items-center justify-center gap-x-3 gap-y-1 text-xs text-brand-700 font-medium">
            {resume.linkedinUrl && (
              <a href={resume.linkedinUrl} target="_blank" rel="noreferrer" className="hover:underline">
                LinkedIn
              </a>
            )}
            {resume.githubUrl && (
              <a href={resume.githubUrl} target="_blank" rel="noreferrer" className="hover:underline">
                • GitHub
              </a>
            )}
            {resume.portfolioUrl && (
              <a href={resume.portfolioUrl} target="_blank" rel="noreferrer" className="hover:underline">
                • Portfolio
              </a>
            )}
          </div>
        </div>

        {/* PROFESSIONAL SUMMARY */}
        {resume.professionalSummary && (
          <div className="space-y-1.5">
            <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider border-b border-slate-200 pb-1">
              Professional Summary
            </h2>
            <p className="text-xs text-slate-700 leading-relaxed">{resume.professionalSummary}</p>
          </div>
        )}

        {/* TECHNICAL SKILLS */}
        {resume.technicalSkills && resume.technicalSkills.length > 0 && (
          <div className="space-y-1.5">
            <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider border-b border-slate-200 pb-1">
              Technical Skills
            </h2>
            <p className="text-xs text-slate-700 font-medium leading-relaxed">
              {resume.technicalSkills.join(', ')}
            </p>
          </div>
        )}

        {/* EXPERIENCE / INTERNSHIPS */}
        {resume.experiences && resume.experiences.length > 0 && (
          <div className="space-y-3">
            <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider border-b border-slate-200 pb-1">
              Professional Experience
            </h2>
            {resume.experiences.map((exp, idx) => (
              <div key={idx} className="space-y-1 text-xs">
                <div className="flex justify-between items-baseline font-bold text-slate-900">
                  <span>{exp.roleTitle} — <span className="font-semibold text-slate-700">{exp.companyName}</span></span>
                  <span className="text-slate-500 font-normal">{exp.startDate} - {exp.endDate || 'Present'}</span>
                </div>
                {exp.description && <p className="text-slate-600 italic">{exp.description}</p>}
                {exp.bulletPoints && exp.bulletPoints.length > 0 && (
                  <ul className="list-disc list-inside space-y-0.5 text-slate-700 ml-1">
                    {exp.bulletPoints.map((bp, bidx) => (
                      <li key={bidx}>{bp}</li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        )}

        {/* KEY PROJECTS */}
        {resume.projects && resume.projects.length > 0 && (
          <div className="space-y-3">
            <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider border-b border-slate-200 pb-1">
              Key Technical Projects
            </h2>
            {resume.projects.map((proj, idx) => (
              <div key={idx} className="space-y-1 text-xs">
                <div className="flex justify-between items-baseline font-bold text-slate-900">
                  <span>{proj.title} {proj.techStack && <span className="font-normal text-slate-500">[{proj.techStack}]</span>}</span>
                  {proj.githubUrl && (
                    <a href={proj.githubUrl} target="_blank" rel="noreferrer" className="text-brand-600 hover:underline font-normal">
                      GitHub
                    </a>
                  )}
                </div>
                {proj.description && <p className="text-slate-600">{proj.description}</p>}
                {proj.bulletPoints && proj.bulletPoints.length > 0 && (
                  <ul className="list-disc list-inside space-y-0.5 text-slate-700 ml-1">
                    {proj.bulletPoints.map((bp, bidx) => (
                      <li key={bidx}>{bp}</li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        )}

        {/* EDUCATION */}
        {resume.educations && resume.educations.length > 0 && (
          <div className="space-y-2">
            <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider border-b border-slate-200 pb-1">
              Education
            </h2>
            {resume.educations.map((ed, idx) => (
              <div key={idx} className="flex justify-between items-baseline text-xs">
                <div>
                  <span className="font-bold text-slate-900">{ed.degree}</span> •{' '}
                  <span className="text-slate-700">{ed.institution}</span>
                </div>
                <span className="text-slate-500">
                  {ed.startYear} - {ed.endYear} {ed.scoreOrCgpa ? `(${ed.scoreOrCgpa})` : ''}
                </span>
              </div>
            ))}
          </div>
        )}

        {/* CERTIFICATIONS */}
        {resume.certifications && resume.certifications.length > 0 && (
          <div className="space-y-1.5">
            <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider border-b border-slate-200 pb-1">
              Certifications
            </h2>
            <ul className="list-disc list-inside space-y-0.5 text-xs text-slate-700">
              {resume.certifications.map((c, idx) => (
                <li key={idx}>
                  <span className="font-semibold text-slate-900">{c.name}</span> — {c.issuingOrganization} ({c.issueDate})
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}
