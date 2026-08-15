import React, { useState, useEffect } from 'react';
import {
  Sliders,
  CheckCircle2,
  Sparkles,
  MapPin,
  Briefcase,
  Layers,
  DollarSign,
  Plus,
  X,
  Save,
} from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type {
  StudentJobPreferenceDto,
  ExperienceLevel,
  WorkMode,
  EmploymentType,
} from '../../api/jobs';

export const JobPreferencesPage: React.FC = () => {
  const [preferences, setPreferences] = useState<StudentJobPreferenceDto>({
    preferredRoles: ['Java Developer', 'Full Stack Developer', 'Backend Engineer'],
    preferredLocations: ['Hyderabad', 'Bangalore', 'Remote'],
    preferredTechnologies: ['Java', 'Spring Boot', 'SQL', 'Microservices', 'React'],
    experienceLevel: 'FRESHER',
    workMode: 'HYBRID',
    employmentType: 'FULL_TIME',
    expectedSalaryMin: 400000,
  });

  const [newRole, setNewRole] = useState('');
  const [newLoc, setNewLoc] = useState('');
  const [newTech, setNewTech] = useState('');
  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [savedSuccess, setSavedSuccess] = useState<boolean>(false);

  useEffect(() => {
    loadPreferences();
  }, []);

  const loadPreferences = async () => {
    try {
      setLoading(true);
      const res = await jobsApi.getJobPreferences();
      if (res) {
        setPreferences(res);
      }
    } catch (err) {
      console.error('Failed to load career preferences:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setSaving(true);
      const res = await jobsApi.saveJobPreferences(preferences);
      setPreferences(res);
      setSavedSuccess(true);
      setTimeout(() => setSavedSuccess(false), 3000);
    } catch (err) {
      console.error('Failed to save preferences:', err);
    } finally {
      setSaving(false);
    }
  };

  const addRole = () => {
    if (newRole.trim() && !preferences.preferredRoles.includes(newRole.trim())) {
      setPreferences({
        ...preferences,
        preferredRoles: [...preferences.preferredRoles, newRole.trim()],
      });
      setNewRole('');
    }
  };

  const removeRole = (role: string) => {
    setPreferences({
      ...preferences,
      preferredRoles: preferences.preferredRoles.filter((r) => r !== role),
    });
  };

  const addLoc = () => {
    if (newLoc.trim() && !preferences.preferredLocations.includes(newLoc.trim())) {
      setPreferences({
        ...preferences,
        preferredLocations: [...preferences.preferredLocations, newLoc.trim()],
      });
      setNewLoc('');
    }
  };

  const removeLoc = (loc: string) => {
    setPreferences({
      ...preferences,
      preferredLocations: preferences.preferredLocations.filter((l) => l !== loc),
    });
  };

  const addTech = () => {
    if (newTech.trim() && !preferences.preferredTechnologies.includes(newTech.trim())) {
      setPreferences({
        ...preferences,
        preferredTechnologies: [...preferences.preferredTechnologies, newTech.trim()],
      });
      setNewTech('');
    }
  };

  const removeTech = (tech: string) => {
    setPreferences({
      ...preferences,
      preferredTechnologies: preferences.preferredTechnologies.filter((t) => t !== tech),
    });
  };

  if (loading) {
    return (
      <div className="card p-12 border-gray-800 animate-pulse space-y-4">
        <div className="h-8 bg-gray-800 rounded w-1/3"></div>
        <div className="h-64 bg-gray-800 rounded"></div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl space-y-8 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold text-white flex items-center gap-2">
          <Sliders className="w-6 h-6 text-blue-400" />
          Career & Job Preferences
        </h1>
        <p className="text-sm text-gray-400 mt-1">
          Customize your target roles, locations, and technology stack. These preferences power your personalized job matching score.
        </p>
      </div>

      {savedSuccess && (
        <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-sm flex items-center gap-2">
          <CheckCircle2 className="w-5 h-5" /> Preferences saved successfully! Job match scores have been refreshed.
        </div>
      )}

      <form onSubmit={handleSave} className="card p-8 border-gray-800 bg-gray-900/60 space-y-8">
        {/* Preferred Roles */}
        <div className="space-y-3">
          <label className="text-sm font-bold text-white flex items-center gap-2">
            <Briefcase className="w-4 h-4 text-blue-400" />
            Target Job Roles
          </label>
          <div className="flex flex-wrap gap-2">
            {preferences.preferredRoles.map((role) => (
              <span
                key={role}
                className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-500/15 text-blue-300 border border-blue-500/30 text-xs font-medium"
              >
                {role}
                <button
                  type="button"
                  onClick={() => removeRole(role)}
                  className="hover:text-white"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </span>
            ))}
          </div>
          <div className="flex gap-2 max-w-md">
            <input
              type="text"
              placeholder="e.g. Java Backend Engineer"
              value={newRole}
              onChange={(e) => setNewRole(e.target.value)}
              className="input w-full bg-gray-800 border-gray-700 text-xs text-white"
            />
            <button
              type="button"
              onClick={addRole}
              className="btn btn-secondary text-xs px-3"
            >
              <Plus className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Preferred Locations */}
        <div className="space-y-3 pt-6 border-t border-gray-800">
          <label className="text-sm font-bold text-white flex items-center gap-2">
            <MapPin className="w-4 h-4 text-emerald-400" />
            Preferred Cities / Locations
          </label>
          <div className="flex flex-wrap gap-2">
            {preferences.preferredLocations.map((loc) => (
              <span
                key={loc}
                className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/15 text-emerald-300 border border-emerald-500/30 text-xs font-medium"
              >
                {loc}
                <button
                  type="button"
                  onClick={() => removeLoc(loc)}
                  className="hover:text-white"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </span>
            ))}
          </div>
          <div className="flex gap-2 max-w-md">
            <input
              type="text"
              placeholder="e.g. Hyderabad, Bangalore, Pune"
              value={newLoc}
              onChange={(e) => setNewLoc(e.target.value)}
              className="input w-full bg-gray-800 border-gray-700 text-xs text-white"
            />
            <button
              type="button"
              onClick={addLoc}
              className="btn btn-secondary text-xs px-3"
            >
              <Plus className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Technical Skills & Stack */}
        <div className="space-y-3 pt-6 border-t border-gray-800">
          <label className="text-sm font-bold text-white flex items-center gap-2">
            <Layers className="w-4 h-4 text-purple-400" />
            Core Technologies & Skills
          </label>
          <div className="flex flex-wrap gap-2">
            {preferences.preferredTechnologies.map((tech) => (
              <span
                key={tech}
                className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-purple-500/15 text-purple-300 border border-purple-500/30 text-xs font-medium"
              >
                {tech}
                <button
                  type="button"
                  onClick={() => removeTech(tech)}
                  className="hover:text-white"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </span>
            ))}
          </div>
          <div className="flex gap-2 max-w-md">
            <input
              type="text"
              placeholder="e.g. Spring Boot, Docker, PostgreSQL"
              value={newTech}
              onChange={(e) => setNewTech(e.target.value)}
              className="input w-full bg-gray-800 border-gray-700 text-xs text-white"
            />
            <button
              type="button"
              onClick={addTech}
              className="btn btn-secondary text-xs px-3"
            >
              <Plus className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Work Mode & Experience */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-6 border-t border-gray-800">
          <div>
            <label className="text-xs font-semibold text-gray-300 block mb-1.5">Work Mode</label>
            <select
              value={preferences.workMode || 'HYBRID'}
              onChange={(e) =>
                setPreferences({ ...preferences, workMode: e.target.value as WorkMode })
              }
              className="input w-full bg-gray-800 border-gray-700 text-sm text-white"
            >
              <option value="ONSITE">Onsite</option>
              <option value="HYBRID">Hybrid</option>
              <option value="REMOTE">Remote</option>
            </select>
          </div>

          <div>
            <label className="text-xs font-semibold text-gray-300 block mb-1.5">Experience Level</label>
            <select
              value={preferences.experienceLevel || 'FRESHER'}
              onChange={(e) =>
                setPreferences({ ...preferences, experienceLevel: e.target.value as ExperienceLevel })
              }
              className="input w-full bg-gray-800 border-gray-700 text-sm text-white"
            >
              <option value="FRESHER">Fresher (0 yrs)</option>
              <option value="ENTRY_LEVEL">0 - 2 Years</option>
              <option value="MID_LEVEL">2 - 5 Years</option>
              <option value="SENIOR">5+ Years</option>
            </select>
          </div>

          <div>
            <label className="text-xs font-semibold text-gray-300 block mb-1.5">Expected Salary (₹ Min LPA)</label>
            <input
              type="number"
              placeholder="e.g. 500000"
              value={preferences.expectedSalaryMin || ''}
              onChange={(e) =>
                setPreferences({
                  ...preferences,
                  expectedSalaryMin: e.target.value ? Number(e.target.value) : undefined,
                })
              }
              className="input w-full bg-gray-800 border-gray-700 text-sm text-white"
            />
          </div>
        </div>

        <div className="pt-4 border-t border-gray-800 flex justify-end">
          <button
            type="submit"
            disabled={saving}
            className="btn btn-primary px-8 py-2.5 flex items-center gap-2 shadow-lg shadow-blue-500/20"
          >
            <Save className="w-4 h-4" />
            {saving ? 'Saving...' : 'Save Career Preferences'}
          </button>
        </div>
      </form>
    </div>
  );
};
export default JobPreferencesPage;
