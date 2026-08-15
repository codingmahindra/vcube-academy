import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Building,
  Calendar,
  Clock,
  MapPin,
  DollarSign,
  ChevronRight,
  ExternalLink,
  Users,
  CheckCircle2,
  AlertCircle,
} from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import type { PlacementDriveDto } from '../../api/jobs';

export const PlacementDriveListPage: React.FC = () => {
  const [drives, setDrives] = useState<PlacementDriveDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    loadDrives();
  }, []);

  const loadDrives = async () => {
    try {
      setLoading(true);
      const res = await jobsApi.getPlacementDrives();
      setDrives(res);
    } catch (err) {
      console.error('Failed to load placement drives:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header Banner */}
      <div className="card bg-gradient-to-r from-purple-900/40 via-indigo-900/30 to-blue-900/20 border-purple-500/20 p-8 rounded-2xl relative overflow-hidden">
        <div className="relative z-10 max-w-3xl space-y-3">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-purple-500/10 border border-purple-500/30 text-purple-400 text-xs font-semibold uppercase tracking-wider">
            <Building className="w-3.5 h-3.5" />
            VCUBE Campus & Pool Placement Drives
          </div>
          <h1 className="text-3xl font-bold text-white tracking-tight">
            Exclusive Hiring Drives & Recruitment Events
          </h1>
          <p className="text-gray-400 text-sm leading-relaxed">
            Register directly for upcoming national campus drives and exclusive recruitment programs arranged with our corporate hiring partners.
          </p>
        </div>
      </div>

      {/* Drives Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {[1, 2, 3, 4].map((n) => (
            <div key={n} className="card p-6 border-gray-800 animate-pulse h-48 bg-gray-900/60"></div>
          ))}
        </div>
      ) : drives.length === 0 ? (
        <div className="card p-12 text-center border-gray-800 space-y-3">
          <Building className="w-12 h-12 text-gray-600 mx-auto" />
          <h3 className="text-lg font-medium text-white">No active placement drives currently scheduled</h3>
          <p className="text-sm text-gray-400 max-w-sm mx-auto">
            New corporate hiring events and pool drives will appear here once announced by our placement cell.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {drives.map((drive) => (
            <div
              key={drive.id}
              className="card p-6 border-gray-800 hover:border-purple-500/40 bg-gray-900/60 flex flex-col justify-between space-y-5 transition-all group"
            >
              <div className="space-y-3">
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-xl bg-gray-800 border border-gray-700 flex items-center justify-center font-bold text-purple-400 text-xl">
                      {drive.companyName.charAt(0)}
                    </div>
                    <div>
                      <h4 className="text-sm font-semibold text-white group-hover:text-purple-400 transition-colors">
                        {drive.companyName}
                      </h4>
                      <span className="text-xs text-gray-400">{drive.location}</span>
                    </div>
                  </div>
                  <span className="text-xs px-2.5 py-0.5 rounded-full font-semibold uppercase tracking-wider bg-purple-500/10 text-purple-400 border border-purple-500/20">
                    {drive.status}
                  </span>
                </div>

                <h3 className="text-lg font-bold text-white group-hover:text-purple-300 transition-colors">
                  {drive.title}
                </h3>
                <p className="text-xs text-gray-300 line-clamp-2 leading-relaxed">
                  {drive.description}
                </p>

                <div className="grid grid-cols-2 gap-2 pt-2 text-xs">
                  <div className="p-2.5 rounded-lg bg-gray-800/60 border border-gray-700/60">
                    <span className="text-gray-400 block text-[11px]">Drive Date:</span>
                    <span className="text-white font-semibold flex items-center gap-1 mt-0.5">
                      <Calendar className="w-3.5 h-3.5 text-purple-400" />
                      {new Date(drive.driveDate).toLocaleDateString()}
                    </span>
                  </div>
                  <div className="p-2.5 rounded-lg bg-gray-800/60 border border-gray-700/60">
                    <span className="text-gray-400 block text-[11px]">Reg. Deadline:</span>
                    <span className="text-amber-400 font-semibold flex items-center gap-1 mt-0.5">
                      <Clock className="w-3.5 h-3.5" />
                      {new Date(drive.registrationDeadline).toLocaleDateString()}
                    </span>
                  </div>
                </div>

                {drive.packageDetails && (
                  <div className="text-xs text-emerald-400 font-semibold flex items-center gap-1.5 pt-1">
                    <DollarSign className="w-3.5 h-3.5" />
                    Salary Package: {drive.packageDetails}
                  </div>
                )}
              </div>

              <div className="pt-4 border-t border-gray-800 flex items-center justify-between">
                <Link
                  to={`/student/placements/${drive.id}`}
                  className="btn btn-secondary text-xs px-4 py-2 inline-flex items-center gap-1.5 text-gray-300 hover:text-white"
                >
                  View Eligibility & Rounds
                  <ChevronRight className="w-3.5 h-3.5" />
                </Link>

                {drive.applicationLink && (
                  <a
                    href={drive.applicationLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="btn btn-primary text-xs px-4 py-2 inline-flex items-center gap-1.5 shadow-lg shadow-purple-500/20"
                  >
                    Register Online
                    <ExternalLink className="w-3.5 h-3.5" />
                  </a>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
export default PlacementDriveListPage;
