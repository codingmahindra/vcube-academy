import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  resumeApi,
  type ResumeTemplate,
  type ResumeDataRequest,
  type ResumeExperienceDto,
  type ResumeEducationDto,
  type ResumeProjectDto,
  type ResumeCertificationDto
} from '../../api/resume';
import { useAuth } from '../../hooks/useAuth';
import toast from 'react-hot-toast';
import {
  FileText, Plus, Trash2, Save, Sparkles, Eye, Download,
  CheckCircle2, BookOpen, Layers, Briefcase, GraduationCap, Award
} from 'lucide-react';

export function ResumeBuilderPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [searchParams] = useSearchParams();
  const versionIdParam = searchParams.get('id');

  const [fullName, setFullName] = useState(user?.fullName || '');
  const [email, setEmail] = useState(user?.email || '');
  const [phone, setPhone] = useState('+91 98765 43210');
  const [location, setLocation] = useState('Hyderabad, India');
  const [linkedinUrl, setLinkedinUrl] = useState('https://linkedin.com/in/');
  const [githubUrl, setGithubUrl] = useState('https://github.com/');
  const [portfolioUrl, setPortfolioUrl] = useState('');
  const [professionalSummary, setProfessionalSummary] = useState(
    'Passionate Java Full Stack Developer skilled in Java 17, Spring Boot, Microservices, and PostgreSQL. Experienced in building high-throughput REST APIs and scalable distributed systems.'
  );

  const [versionTitle, setVersionTitle] = useState('Java Full Stack Resume');
  const [targetRole, setTargetRole] = useState('Java Developer');
  const [targetCompany, setTargetCompany] = useState('General Placement');
  const [template, setTemplate] = useState<ResumeTemplate>('JAVA_FULLSTACK');

  const [experiences, setExperiences] = useState<ResumeExperienceDto[]>([
    {
      companyName: 'VCUBE Software Solutions',
      roleTitle: 'Java Full Stack Intern',
      location: 'Hyderabad, India',
      startDate: 'Jan 2024',
      endDate: 'Jun 2024',
      isCurrent: false,
      description: 'Built enterprise backend microservices using Spring Boot and PostgreSQL.',
      bulletPoints: [
        'Architected 15+ RESTful endpoints with sub-100ms response latencies',
        'Implemented JWT stateless authentication and security authorization filters',
      ],
    },
  ]);

  const [educations, setEducations] = useState<ResumeEducationDto[]>([
    {
      institution: 'JNTU Hyderabad',
      degree: 'Bachelor of Technology (B.Tech)',
      fieldOfStudy: 'Computer Science & Engineering',
      startYear: '2020',
      endYear: '2024',
      scoreOrCgpa: '8.4 CGPA',
    },
  ]);

  const [projects, setProjects] = useState<ResumeProjectDto[]>([
    {
      title: 'Distributed E-Commerce Microservices Engine',
      techStack: 'Java 17, Spring Boot, PostgreSQL, Docker, Kafka',
      liveUrl: 'https://demo.vcube-ecommerce.com',
      githubUrl: 'https://github.com/vcube-student/ecommerce-microservices',
      description: 'Distributed microservices architecture for order and product management.',
      bulletPoints: [
        'Implemented service discovery and gateway routing using Spring Cloud',
        'Containerized multi-service ecosystem using Docker Compose',
      ],
    },
  ]);

  const [certifications, setCertifications] = useState<ResumeCertificationDto[]>([
    {
      name: 'Oracle Certified Professional: Java SE 17 Developer',
      issuingOrganization: 'Oracle Corporation',
      issueDate: '2024',
      credentialUrl: '',
    },
  ]);

  // Load existing version if ID passed
  const { data: existingVersion } = useQuery({
    queryKey: ['resume-version', versionIdParam],
    queryFn: () => resumeApi.getVersionDetail(Number(versionIdParam)),
    enabled: !!versionIdParam,
  });

  useEffect(() => {
    if (existingVersion) {
      setFullName(existingVersion.fullName || '');
      setEmail(existingVersion.email || '');
      setPhone(existingVersion.phone || '');
      setLocation(existingVersion.location || '');
      setLinkedinUrl(existingVersion.linkedinUrl || '');
      setGithubUrl(existingVersion.githubUrl || '');
      setPortfolioUrl(existingVersion.portfolioUrl || '');
      setProfessionalSummary(existingVersion.professionalSummary || '');
      setVersionTitle(existingVersion.versionTitle || '');
      setTargetRole(existingVersion.targetJobTitle || '');
      setTargetCompany(existingVersion.targetCompany || '');
      setTemplate(existingVersion.template || 'JAVA_FULLSTACK');
      if (existingVersion.experiences?.length) setExperiences(existingVersion.experiences);
      if (existingVersion.educations?.length) setEducations(existingVersion.educations);
      if (existingVersion.projects?.length) setProjects(existingVersion.projects);
      if (existingVersion.certifications?.length) setCertifications(existingVersion.certifications);
    }
  }, [existingVersion]);

  // Save Mutation
  const saveMutation = useMutation({
    mutationFn: (data: ResumeDataRequest) => {
      if (versionIdParam) {
        return resumeApi.updateVersion(Number(versionIdParam), data);
      }
      return resumeApi.createVersion(data);
    },
    onSuccess: (saved) => {
      queryClient.invalidateQueries({ queryKey: ['student-resume-versions'] });
      toast.success('Resume version saved successfully!');
      navigate(`/student/resume/preview/${saved.id}`);
    },
    onError: () => toast.error('Failed to save resume'),
  });

  const handleSave = () => {
    const payload: ResumeDataRequest = {
      fullName,
      email,
      phone,
      location,
      linkedinUrl,
      githubUrl,
      portfolioUrl,
      professionalSummary,
      versionTitle,
      targetRole,
      targetCompany,
      template,
      experiences,
      educations,
      projects,
      certifications,
      isPrimary: true,
    };
    saveMutation.mutate(payload);
  };

  return (
    <div className="space-y-6 animate-fade-in pb-16">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2.5">
            <FileText className="h-7 w-7 text-brand-600" />
            ATS Resume Builder & Editor
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Build ATS-compliant resumes with verifiable technical accomplishments and standard formatting.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={handleSave}
            disabled={saveMutation.isPending}
            className="btn-primary flex items-center gap-1.5 text-xs"
          >
            <Save className="h-4 w-4" />
            {saveMutation.isPending ? 'Saving...' : 'Save & Preview'}
          </button>
        </div>
      </div>

      {/* Version & Template Settings */}
      <div className="card p-6 grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">Resume Version Title</label>
          <input
            className="input text-xs"
            value={versionTitle}
            onChange={(e) => setVersionTitle(e.target.value)}
            placeholder="e.g. Java Full Stack — TCS"
          />
        </div>
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">Target Role</label>
          <input
            className="input text-xs"
            value={targetRole}
            onChange={(e) => setTargetRole(e.target.value)}
            placeholder="e.g. Graduate Java Engineer"
          />
        </div>
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">Target Company</label>
          <input
            className="input text-xs"
            value={targetCompany}
            onChange={(e) => setTargetCompany(e.target.value)}
            placeholder="e.g. Infosys"
          />
        </div>
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">ATS Template</label>
          <select
            className="input text-xs font-semibold"
            value={template}
            onChange={(e) => setTemplate(e.target.value as any)}
          >
            <option value="ATS_CLASSIC">ATS Classic (Standard)</option>
            <option value="ATS_MODERN">ATS Modern (Clean Header)</option>
            <option value="JAVA_FULLSTACK">Java Full Stack Specialist</option>
            <option value="BACKEND_DEVELOPER">Backend Developer</option>
            <option value="FRESHER">Fresher / Graduate</option>
          </select>
        </div>
      </div>

      {/* SECTION 1: Personal Contact Info */}
      <div className="card p-6 space-y-4">
        <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider border-b border-slate-100 pb-2">
          1. Header & Contact Information
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Full Name</label>
            <input className="input text-xs" value={fullName} onChange={(e) => setFullName(e.target.value)} />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Email</label>
            <input className="input text-xs" value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Phone</label>
            <input className="input text-xs" value={phone} onChange={(e) => setPhone(e.target.value)} />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Location</label>
            <input className="input text-xs" value={location} onChange={(e) => setLocation(e.target.value)} />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">LinkedIn Profile</label>
            <input className="input text-xs" value={linkedinUrl} onChange={(e) => setLinkedinUrl(e.target.value)} />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">GitHub Profile</label>
            <input className="input text-xs" value={githubUrl} onChange={(e) => setGithubUrl(e.target.value)} />
          </div>
        </div>
      </div>

      {/* SECTION 2: Professional Summary */}
      <div className="card p-6 space-y-3">
        <div className="flex items-center justify-between border-b border-slate-100 pb-2">
          <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider">
            2. Professional Summary
          </h2>
          <span className="text-[11px] text-slate-400">3-4 impactful sentences</span>
        </div>
        <textarea
          className="input min-h-[90px] text-xs leading-relaxed"
          value={professionalSummary}
          onChange={(e) => setProfessionalSummary(e.target.value)}
        />
      </div>

      {/* SECTION 3: Work Experience / Internships */}
      <div className="card p-6 space-y-4">
        <div className="flex items-center justify-between border-b border-slate-100 pb-2">
          <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
            <Briefcase className="h-4 w-4 text-brand-600" />
            3. Experience & Internships
          </h2>
          <button
            onClick={() =>
              setExperiences([
                ...experiences,
                { companyName: '', roleTitle: '', startDate: '', endDate: '', bulletPoints: [''] },
              ])
            }
            className="btn-secondary text-xs flex items-center gap-1"
          >
            <Plus className="h-3.5 w-3.5" /> Add Experience
          </button>
        </div>

        {experiences.map((exp, idx) => (
          <div key={idx} className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-3">
            <div className="grid grid-cols-1 sm:grid-cols-4 gap-3">
              <div>
                <label className="block text-[11px] font-semibold text-slate-600 mb-1">Company</label>
                <input
                  className="input text-xs"
                  value={exp.companyName}
                  onChange={(e) => {
                    const copy = [...experiences];
                    copy[idx].companyName = e.target.value;
                    setExperiences(copy);
                  }}
                />
              </div>
              <div>
                <label className="block text-[11px] font-semibold text-slate-600 mb-1">Role Title</label>
                <input
                  className="input text-xs"
                  value={exp.roleTitle}
                  onChange={(e) => {
                    const copy = [...experiences];
                    copy[idx].roleTitle = e.target.value;
                    setExperiences(copy);
                  }}
                />
              </div>
              <div>
                <label className="block text-[11px] font-semibold text-slate-600 mb-1">Dates</label>
                <input
                  className="input text-xs"
                  placeholder="Jan 2024 - Present"
                  value={exp.startDate}
                  onChange={(e) => {
                    const copy = [...experiences];
                    copy[idx].startDate = e.target.value;
                    setExperiences(copy);
                  }}
                />
              </div>
              <div className="flex items-end justify-end">
                <button
                  onClick={() => setExperiences(experiences.filter((_, i) => i !== idx))}
                  className="text-red-500 hover:text-red-700 text-xs p-2"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-600 mb-1">
                Bullet Points (one per line, STAR format)
              </label>
              <textarea
                className="input min-h-[60px] text-xs font-mono"
                value={exp.bulletPoints?.join('\n') || ''}
                onChange={(e) => {
                  const copy = [...experiences];
                  copy[idx].bulletPoints = e.target.value.split('\n');
                  setExperiences(copy);
                }}
              />
            </div>
          </div>
        ))}
      </div>

      {/* SECTION 4: Technical Projects */}
      <div className="card p-6 space-y-4">
        <div className="flex items-center justify-between border-b border-slate-100 pb-2">
          <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2">
            <BookOpen className="h-4 w-4 text-indigo-600" />
            4. Portfolio Projects
          </h2>
          <button
            onClick={() =>
              setProjects([
                ...projects,
                { title: '', techStack: '', description: '', bulletPoints: [''] },
              ])
            }
            className="btn-secondary text-xs flex items-center gap-1"
          >
            <Plus className="h-3.5 w-3.5" /> Add Project
          </button>
        </div>

        {projects.map((proj, idx) => (
          <div key={idx} className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-3">
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="block text-[11px] font-semibold text-slate-600 mb-1">Project Title</label>
                <input
                  className="input text-xs"
                  value={proj.title}
                  onChange={(e) => {
                    const copy = [...projects];
                    copy[idx].title = e.target.value;
                    setProjects(copy);
                  }}
                />
              </div>
              <div className="sm:col-span-2">
                <label className="block text-[11px] font-semibold text-slate-600 mb-1">Tech Stack</label>
                <input
                  className="input text-xs"
                  placeholder="Java 17, Spring Boot, PostgreSQL, Docker"
                  value={proj.techStack}
                  onChange={(e) => {
                    const copy = [...projects];
                    copy[idx].techStack = e.target.value;
                    setProjects(copy);
                  }}
                />
              </div>
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-600 mb-1">
                Bullet Points (one per line)
              </label>
              <textarea
                className="input min-h-[60px] text-xs font-mono"
                value={proj.bulletPoints?.join('\n') || ''}
                onChange={(e) => {
                  const copy = [...projects];
                  copy[idx].bulletPoints = e.target.value.split('\n');
                  setProjects(copy);
                }}
              />
            </div>
          </div>
        ))}
      </div>

      {/* SECTION 5: Education & Certifications */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
        {/* Education */}
        <div className="card p-6 space-y-4">
          <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2 border-b border-slate-100 pb-2">
            <GraduationCap className="h-4 w-4 text-brand-600" />
            5. Education
          </h2>
          {educations.map((ed, idx) => (
            <div key={idx} className="p-3 bg-slate-50 rounded-xl space-y-2 text-xs">
              <input
                className="input text-xs font-semibold"
                placeholder="Degree / Branch"
                value={ed.degree}
                onChange={(e) => {
                  const copy = [...educations];
                  copy[idx].degree = e.target.value;
                  setEducations(copy);
                }}
              />
              <input
                className="input text-xs"
                placeholder="Institution / University"
                value={ed.institution}
                onChange={(e) => {
                  const copy = [...educations];
                  copy[idx].institution = e.target.value;
                  setEducations(copy);
                }}
              />
              <div className="grid grid-cols-2 gap-2">
                <input
                  className="input text-xs"
                  placeholder="Years (e.g. 2020 - 2024)"
                  value={ed.startYear}
                  onChange={(e) => {
                    const copy = [...educations];
                    copy[idx].startYear = e.target.value;
                    setEducations(copy);
                  }}
                />
                <input
                  className="input text-xs"
                  placeholder="CGPA (e.g. 8.4 CGPA)"
                  value={ed.scoreOrCgpa}
                  onChange={(e) => {
                    const copy = [...educations];
                    copy[idx].scoreOrCgpa = e.target.value;
                    setEducations(copy);
                  }}
                />
              </div>
            </div>
          ))}
        </div>

        {/* Certifications */}
        <div className="card p-6 space-y-4">
          <h2 className="text-sm font-bold text-slate-900 uppercase tracking-wider flex items-center gap-2 border-b border-slate-100 pb-2">
            <Award className="h-4 w-4 text-emerald-600" />
            6. Certifications
          </h2>
          {certifications.map((cert, idx) => (
            <div key={idx} className="p-3 bg-slate-50 rounded-xl space-y-2 text-xs">
              <input
                className="input text-xs font-semibold"
                placeholder="Certificate Name"
                value={cert.name}
                onChange={(e) => {
                  const copy = [...certifications];
                  copy[idx].name = e.target.value;
                  setCertifications(copy);
                }}
              />
              <input
                className="input text-xs"
                placeholder="Issuing Organization"
                value={cert.issuingOrganization}
                onChange={(e) => {
                  const copy = [...certifications];
                  copy[idx].issuingOrganization = e.target.value;
                  setCertifications(copy);
                }}
              />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
