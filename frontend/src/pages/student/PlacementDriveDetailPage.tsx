import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  Building,
  Calendar,
  Clock,
  MapPin,
  DollarSign,
  ArrowLeft,
  ExternalLink,
  CheckCircle2,
  AlertCircle,
  FileText,
  MessageSquare,
  BookOpen,
} from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type { PlacementDriveDto } from '../../api/jobs';

export const PlacementDriveDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [drive, setDrive] = useState<PlacementDriveDto | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (id) {
      loadDriveDetail(Number(id));
    }
  }, [id]);

  const loadDriveDetail = async (driveId: number) => {
    try {
      setLoading(true);
      const res = await jobsApi.getPlacementDriveDetail(driveId);
      setDrive(res);
    } catch (err) {
      console.error('Failed to load placement drive:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="card p-12 border-gray-800 animate-pulse space-y-4">
        <div className="h-8 bg-gray-800 rounded w-1/3"></div>
        <div className="h-48 bg-gray-800 rounded"></div>
      </div>
    );
  }

  if (!drive) {
    return (
      <div className="card p-12 text-center border-gray-800 space-y-3">
        <AlertCircle className="w-12 h-12 text-rose-500 mx-auto" />
        <h2 className="text-xl font-bold text-white">Placement Drive Not Found</h2>
        <Link to="/student/placements" className="btn btn-secondary text-sm inline-flex items-center gap-2">
          <ArrowLeft className="w-4 h-4" /> Back to Drives
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-fade-in">
      <div className="flex items-center justify-between">
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-2 text-sm text-gray-400 hover:text-white transition-colors"
        >
          <ArrowLeft className="w-4 h-4" /> Back to Drives
        </button>

        {drive.applicationLink && (
          <a
            href={drive.applicationLink}
            target="_blank"
            rel="noopener noreferrer"
            className="btn btn-primary inline-flex items-center gap-2 text-sm shadow-lg shadow-purple-500/20"
          >
            <ExternalLink className="w-4 h-4" />
            Official Registration Link
          </a>
        )}
      </div>

      {/* Header Card */}
      <div className="card p-8 border-gray-800 bg-gray-900/60 space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="flex items-start gap-4">
            <div className="w-16 h-16 rounded-2xl bg-gray-800 border border-gray-700 flex items-center justify-center font-bold text-purple-400 text-2xl">
              {drive.companyName.charAt(0)}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-2xl font-bold text-white">{drive.title}</h1>
                <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-purple-500/15 text-purple-400 border border-purple-500/30">
                  {drive.status}
                </span>
              </div>
              <h2 className="text-base text-gray-300 font-medium mt-1">{drive.companyName}</h2>
              <div className="flex flex-wrap items-center gap-4 text-xs text-gray-400 mt-2">
                <span className="inline-flex items-center gap-1.5">
                  <MapPin className="w-3.5 h-3.5 text-gray-500" />
                  {drive.location}
                </span>
                <span className="inline-flex items-center gap-1.5">
                  <Calendar className="w-3.5 h-3.5 text-gray-500" />
                  Drive Date: {new Date(drive.driveDate).toLocaleDateString()}
                </span>
                <span className="inline-flex items-center gap-1.5">
                  <Clock className="w-3.5 h-3.5 text-amber-500" />
                  Deadline: {new Date(drive.registrationDeadline).toLocaleDateString()}
                </span>
                {drive.packageDetails && (
                  <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-semibold">
                    <DollarSign className="w-3.5 h-3.5" />
                    {drive.packageDetails}
                  </span>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Details Sections */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          <div className="card p-6 border-gray-800 space-y-3">
            <h3 className="text-base font-bold text-white">Event Overview & Details</h3>
            <p className="text-sm text-gray-300 whitespace-pre-line leading-relaxed">
              {drive.description}
            </p>
          </div>

          {drive.eligibilityCriteria && (
            <div className="card p-6 border-gray-800 space-y-3">
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                Eligibility Criteria
              </h3>
              <p className="text-sm text-gray-300 whitespace-pre-line leading-relaxed bg-gray-900/60 p-4 rounded-xl border border-gray-800">
                {drive.eligibilityCriteria}
              </p>
            </div>
          )}

          {drive.selectionProcess && (
            <div className="card p-6 border-gray-800 space-y-3">
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <FileText className="w-4 h-4 text-blue-400" />
                Hiring & Selection Process
              </h3>
              <p className="text-sm text-gray-300 whitespace-pre-line leading-relaxed">
                {drive.selectionProcess}
              </p>
            </div>
          )}
        </div>

        {/* Preparation CTA */}
        <div className="space-y-6">
          <div className="card p-6 border-purple-500/30 bg-purple-950/20 space-y-4">
            <h3 className="text-sm font-bold text-white uppercase tracking-wider">
              Accelerate Your Placement Prep
            </h3>
            <p className="text-xs text-gray-300 leading-relaxed">
              Review verified technical interview questions asked at {drive.companyName} and practice live mock interview simulations.
            </p>

            <div className="space-y-2 pt-2">
              <Link
                to="/student/interview/companies"
                className="btn btn-secondary w-full text-xs flex items-center justify-center gap-2 text-purple-300 border-purple-500/30 hover:bg-purple-500/10"
              >
                <BookOpen className="w-3.5 h-3.5" />
                {drive.companyName} Company Prep
              </Link>
              <Link
                to="/student/interview/mock/setup"
                className="btn btn-primary w-full text-xs flex items-center justify-center gap-2"
              >
                <MessageSquare className="w-3.5 h-3.5" />
                Start Mock Interview
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default PlacementDriveDetailPage;
