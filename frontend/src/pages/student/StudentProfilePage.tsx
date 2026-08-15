import React, { useState, useEffect } from 'react';
import { profileApi, type StudentProfile, type StudentProfileUpdatePayload } from '../../api/profile';
import {
  User, Mail, Phone, GraduationCap, Briefcase,
  Globe, Code2, ShieldCheck, CheckCircle2, AlertCircle, Save, ExternalLink
} from 'lucide-react';

export default function StudentProfilePage() {
  const [profile, setProfile] = useState<StudentProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Form State
  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');
  const [college, setCollege] = useState('');
  const [degree, setDegree] = useState('');
  const [graduationYear, setGraduationYear] = useState('');
  const [cgpa, setCgpa] = useState<number | undefined>(undefined);
  const [bio, setBio] = useState('');
  const [linkedinUrl, setLinkedinUrl] = useState('');
  const [githubUrl, setGithubUrl] = useState('');
  const [portfolioUrl, setPortfolioUrl] = useState('');
  const [technicalSkills, setTechnicalSkills] = useState('');
  const [targetRoles, setTargetRoles] = useState('');
  const [preferredLocations, setPreferredLocations] = useState('');
  const [includeInResume, setIncludeInResume] = useState(true);
  const [includeInAtsAnalysis, setIncludeInAtsAnalysis] = useState(true);
  const [includeInCopilot, setIncludeInCopilot] = useState(true);

  useEffect(() => {
    async function loadProfile() {
      try {
        setLoading(true);
        const data = await profileApi.getProfile();
        setProfile(data);
        setFullName(data.fullName || '');
        setPhone(data.phone || '');
        setCollege(data.college || '');
        setDegree(data.degree || '');
        setGraduationYear(data.graduationYear || '');
        setCgpa(data.cgpa);
        setBio(data.bio || '');
        setLinkedinUrl(data.linkedinUrl || '');
        setGithubUrl(data.githubUrl || '');
        setPortfolioUrl(data.portfolioUrl || '');
        setTechnicalSkills(data.technicalSkills?.join(', ') || '');
        setTargetRoles(data.targetRoles?.join(', ') || '');
        setPreferredLocations(data.preferredLocations?.join(', ') || '');
        setIncludeInResume(data.includeInResume);
        setIncludeInAtsAnalysis(data.includeInAtsAnalysis);
        setIncludeInCopilot(data.includeInCopilot);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load profile');
      } finally {
        setLoading(false);
      }
    }
    loadProfile();
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setSaving(true);
      setError(null);
      setSavedSuccess(false);

      const payload: StudentProfileUpdatePayload = {
        fullName,
        phone,
        college,
        degree,
        graduationYear,
        cgpa,
        bio,
        linkedinUrl,
        githubUrl,
        portfolioUrl,
        technicalSkills: technicalSkills.split(',').map((s) => s.trim()).filter(Boolean),
        targetRoles: targetRoles.split(',').map((s) => s.trim()).filter(Boolean),
        preferredLocations: preferredLocations.split(',').map((s) => s.trim()).filter(Boolean),
        includeInResume,
        includeInAtsAnalysis,
        includeInCopilot,
      };

      const updated = await profileApi.updateProfile(payload);
      setProfile(updated);
      setSavedSuccess(true);
      setTimeout(() => setSavedSuccess(false), 4000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-28">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-4xl mx-auto pb-16">
      {/* Header */}
      <div className="rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-8 text-white shadow-xl">
        <div className="flex items-center gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-indigo-600 text-white font-extrabold text-xl shadow-lg border border-indigo-400/30">
            {fullName ? fullName.charAt(0).toUpperCase() : 'S'}
          </div>
          <div>
            <h1 className="text-2xl font-extrabold">{profile?.fullName || 'Student Profile'}</h1>
            <p className="text-xs text-slate-300 mt-0.5">{profile?.email} • VCUBE Academy</p>
          </div>
        </div>
      </div>

      {savedSuccess && (
        <div className="rounded-2xl border border-emerald-200 bg-emerald-50 p-4 text-xs font-bold text-emerald-800 flex items-center gap-2">
          <CheckCircle2 className="h-4 w-4" /> Profile updated and synchronized with ATS Resume Builder and Career Copilot!
        </div>
      )}

      {error && (
        <div className="rounded-2xl border border-red-200 bg-red-50 p-4 text-xs text-red-700 flex items-center gap-2">
          <AlertCircle className="h-4 w-4" /> {error}
        </div>
      )}

      <form onSubmit={handleSave} className="space-y-6">
        {/* Personal & Contact Details */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs space-y-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
            <User className="h-4 w-4 text-indigo-600" /> Personal & Contact Information
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
            <div>
              <label className="block font-bold text-slate-700 mb-1">Full Name *</label>
              <input
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Phone Number</label>
              <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="+91 9876543210"
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1 text-xs">Professional Bio</label>
            <textarea
              rows={3}
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              placeholder="Summary of skills and career goals..."
              className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-xs text-slate-900 focus:border-indigo-500 focus:outline-none leading-relaxed"
            />
          </div>
        </div>

        {/* Education Details */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs space-y-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
            <GraduationCap className="h-4 w-4 text-emerald-600" /> Education & Academic Background
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
            <div>
              <label className="block font-bold text-slate-700 mb-1">College / University</label>
              <input
                type="text"
                value={college}
                onChange={(e) => setCollege(e.target.value)}
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Degree & Branch</label>
              <input
                type="text"
                value={degree}
                onChange={(e) => setDegree(e.target.value)}
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Graduation Year</label>
              <input
                type="text"
                value={graduationYear}
                onChange={(e) => setGraduationYear(e.target.value)}
                placeholder="2025"
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">CGPA / Percentage</label>
              <input
                type="number"
                step="0.01"
                value={cgpa || ''}
                onChange={(e) => setCgpa(e.target.value ? parseFloat(e.target.value) : undefined)}
                placeholder="8.5"
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>
          </div>
        </div>

        {/* Skills & Career Preferences */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs space-y-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
            <Briefcase className="h-4 w-4 text-purple-600" /> Technical Skills & Target Career Preferences
          </h2>

          <div className="space-y-4 text-xs">
            <div>
              <label className="block font-bold text-slate-700 mb-1">Technical Skills (Comma-separated)</label>
              <input
                type="text"
                value={technicalSkills}
                onChange={(e) => setTechnicalSkills(e.target.value)}
                placeholder="Java, Spring Boot, PostgreSQL, React, Docker, Git"
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none font-mono"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Target Roles (Comma-separated)</label>
                <input
                  type="text"
                  value={targetRoles}
                  onChange={(e) => setTargetRoles(e.target.value)}
                  placeholder="Java Developer, Full Stack Engineer"
                  className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Preferred Locations (Comma-separated)</label>
                <input
                  type="text"
                  value={preferredLocations}
                  onChange={(e) => setPreferredLocations(e.target.value)}
                  placeholder="Hyderabad, Bengaluru, Remote"
                  className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
                />
              </div>
            </div>
          </div>
        </div>

        {/* Portfolio & Social Links */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs space-y-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
            <Globe className="h-4 w-4 text-amber-600" /> Online Profiles & Portfolio
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
            <div>
              <label className="block font-bold text-slate-700 mb-1 flex items-center gap-1">
                <ExternalLink className="h-3.5 w-3.5 text-blue-600" /> LinkedIn
              </label>
              <input
                type="url"
                value={linkedinUrl}
                onChange={(e) => setLinkedinUrl(e.target.value)}
                placeholder="https://linkedin.com/in/..."
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1 flex items-center gap-1">
                <Code2 className="h-3.5 w-3.5 text-slate-800" /> GitHub
              </label>
              <input
                type="url"
                value={githubUrl}
                onChange={(e) => setGithubUrl(e.target.value)}
                placeholder="https://github.com/..."
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1 flex items-center gap-1">
                <Globe className="h-3.5 w-3.5 text-indigo-600" /> Portfolio Website
              </label>
              <input
                type="url"
                value={portfolioUrl}
                onChange={(e) => setPortfolioUrl(e.target.value)}
                placeholder="https://yourname.dev"
                className="w-full rounded-xl border border-slate-200 px-3.5 py-2.5 text-slate-900 focus:border-indigo-500 focus:outline-none"
              />
            </div>
          </div>
        </div>

        {/* AI & Resume Visibility Controls */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-xs space-y-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
            <ShieldCheck className="h-4 w-4 text-emerald-600" /> Privacy & AI Data Controls
          </h2>

          <div className="space-y-3 text-xs">
            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                checked={includeInResume}
                onChange={(e) => setIncludeInResume(e.target.checked)}
                className="h-4 w-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500"
              />
              <span className="font-semibold text-slate-800">
                Use verified academic & skill details in ATS Resume Builder exports
              </span>
            </label>

            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                checked={includeInAtsAnalysis}
                onChange={(e) => setIncludeInAtsAnalysis(e.target.checked)}
                className="h-4 w-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500"
              />
              <span className="font-semibold text-slate-800">
                Allow ATS Scanner to benchmark my profile against target Job Descriptions
              </span>
            </label>

            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                checked={includeInCopilot}
                onChange={(e) => setIncludeInCopilot(e.target.checked)}
                className="h-4 w-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500"
              />
              <span className="font-semibold text-slate-800">
                Allow AI Career Copilot to reference my course progress for personalized daily plans
              </span>
            </label>
          </div>
        </div>

        {/* Submit */}
        <div className="flex justify-end pt-4">
          <button
            type="submit"
            disabled={saving}
            className="inline-flex items-center gap-2 rounded-2xl bg-indigo-600 px-7 py-3 text-sm font-bold text-white shadow-md hover:bg-indigo-500 disabled:opacity-50 transition-colors"
          >
            <Save className="h-4 w-4" /> {saving ? 'Saving Profile...' : 'Save Profile Changes'}
          </button>
        </div>
      </form>
    </div>
  );
}
