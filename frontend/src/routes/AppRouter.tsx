import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from 'react-router-dom';

import { useAuth } from '../hooks/useAuth';
import { PrivateRoute } from './PrivateRoute';

import { PublicLayout } from '../layouts/PublicLayout';
import { StudentLayout } from '../layouts/StudentLayout';
import { TrainerLayout } from '../layouts/TrainerLayout';
import { AdminLayout } from '../layouts/AdminLayout';

import { LandingPage } from '../pages/LandingPage';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';

import { StudentDashboard } from '../pages/student/StudentDashboard';
import { CoursesPage } from '../pages/student/CoursesPage';
import { CourseDetailPage } from '../pages/student/CourseDetailPage';
import { TopicViewerPage } from '../pages/student/TopicViewerPage';
import { QuizPage } from '../pages/student/QuizPage';

import { DsaDashboardPage } from '../pages/student/dsa/DsaDashboardPage';
import { DsaProblemListPage } from '../pages/student/dsa/DsaProblemListPage';
import { DsaProblemDetailPage } from '../pages/student/dsa/DsaProblemDetailPage';
import { DsaSubmissionsPage } from '../pages/student/dsa/DsaSubmissionsPage';

import { InterviewDashboardPage } from '../pages/student/interview/InterviewDashboardPage';
import { InterviewTopicListPage } from '../pages/student/interview/InterviewTopicListPage';
import { InterviewCompanyListPage } from '../pages/student/interview/InterviewCompanyListPage';
import { InterviewCompanyDetailPage } from '../pages/student/interview/InterviewCompanyDetailPage';
import { InterviewQuestionPracticePage } from '../pages/student/interview/InterviewQuestionPracticePage';
import { MockInterviewSetupPage } from '../pages/student/interview/MockInterviewSetupPage';
import { LiveMockInterviewPage } from '../pages/student/interview/LiveMockInterviewPage';
import { MockInterviewReportPage } from '../pages/student/interview/MockInterviewReportPage';

import { JobSearchPage } from '../pages/student/JobSearchPage';
import { JobDetailPage } from '../pages/student/JobDetailPage';
import { SavedJobsPage } from '../pages/student/SavedJobsPage';
import { ApplicationTrackerPage } from '../pages/student/ApplicationTrackerPage';
import { ApplicationDetailPage } from '../pages/student/ApplicationDetailPage';

import { PlacementDriveListPage } from '../pages/student/PlacementDriveListPage';
import { PlacementDriveDetailPage } from '../pages/student/PlacementDriveDetailPage';

import { JobPreferencesPage } from '../pages/student/JobPreferencesPage';
import { JobRecommendationsPage } from '../pages/student/JobRecommendationsPage';

import { ResumeAnalyzerPage } from '../pages/student/ResumeAnalyzerPage';
import { ResumeBuilderPage } from '../pages/student/ResumeBuilderPage';
import { ResumeListPage } from '../pages/student/ResumeListPage';
import { ResumePreviewPage } from '../pages/student/ResumePreviewPage';

import CareerDashboardPage from '../pages/student/CareerDashboardPage';
import CareerCopilotPage from '../pages/student/CareerCopilotPage';
import CareerRoadmapPage from '../pages/student/CareerRoadmapPage';
import DailyPlanPage from '../pages/student/DailyPlanPage';

import PlacementPaperListPage from '../pages/student/PlacementPaperListPage';
import PlacementPaperDetailPage from '../pages/student/PlacementPaperDetailPage';
import PlacementPaperAttemptPage from '../pages/student/PlacementPaperAttemptPage';

import BookmarksPage from '../pages/student/BookmarksPage';
import GlobalSearchPage from '../pages/student/GlobalSearchPage';
import NotificationsPage from '../pages/student/NotificationsPage';
import StudentProfilePage from '../pages/student/StudentProfilePage';

import { TrainerDashboard } from '../pages/trainer/TrainerDashboard';
import { AdminDashboard } from '../pages/admin/AdminDashboard';

import { NotFoundPage } from '../pages/NotFoundPage';

import type { RoleName } from '../types';


// ============================================================
// ROLE REDIRECT
// ============================================================

function RoleRedirect() {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  const roleMap: Record<RoleName, string> = {
    STUDENT: '/student/dashboard',
    TRAINER: '/trainer/dashboard',
    ADMIN: '/admin/dashboard',
  };

  const primaryRole = user.roles[0] as RoleName;

  return (
    <Navigate
      to={roleMap[primaryRole] || '/student/dashboard'}
      replace
    />
  );
}


// ============================================================
// APP ROUTER
// ============================================================

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>

        {/* ======================================================
            PUBLIC ROUTES
        ====================================================== */}

        <Route element={<PublicLayout />}>
          <Route
            path="/"
            element={<LandingPage />}
          />

          <Route
            path="/login"
            element={<LoginPage />}
          />

          <Route
            path="/register"
            element={<RegisterPage />}
          />
        </Route>


        {/* ======================================================
            DYNAMIC DASHBOARD
        ====================================================== */}

        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <RoleRedirect />
            </PrivateRoute>
          }
        />


        {/* ======================================================
            GLOBAL PROFILE
        ====================================================== */}

        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <StudentLayout />
            </PrivateRoute>
          }
        >
          <Route
            index
            element={<StudentProfilePage />}
          />
        </Route>


        {/* ======================================================
            STUDENT ROUTES
        ====================================================== */}

        <Route
          element={
            <PrivateRoute allowedRoles={['STUDENT']}>
              <StudentLayout />
            </PrivateRoute>
          }
        >

          <Route path="/student">

            {/* Student Dashboard */}

            <Route
              index
              element={
                <Navigate
                  to="/student/dashboard"
                  replace
                />
              }
            />

            <Route
              path="/student/dashboard"
              element={<StudentDashboard />}
            />

            {/* Profile */}

            <Route
              path="/student/profile"
              element={<StudentProfilePage />}
            />

            {/* Bookmarks */}

            <Route
              path="/student/bookmarks"
              element={<BookmarksPage />}
            />

            {/* Search */}

            <Route
              path="/student/search"
              element={<GlobalSearchPage />}
            />

            {/* Notifications */}

            <Route
              path="/student/notifications"
              element={<NotificationsPage />}
            />


            {/* ==================================================
                CAREER
            ================================================== */}

            <Route
              path="/student/career"
              element={<CareerDashboardPage />}
            />

            <Route
              path="/student/career/copilot"
              element={<CareerCopilotPage />}
            />

            <Route
              path="/student/career/roadmap"
              element={<CareerRoadmapPage />}
            />

            <Route
              path="/student/career/daily-plan"
              element={<DailyPlanPage />}
            />


            {/* ==================================================
                PLACEMENT PAPERS
            ================================================== */}

            <Route
              path="/student/placement-papers"
              element={<PlacementPaperListPage />}
            />

            <Route
              path="/student/placement-papers/:id"
              element={<PlacementPaperDetailPage />}
            />

            <Route
              path="/student/placement-papers/:id/attempt"
              element={<PlacementPaperAttemptPage />}
            />


            {/* ==================================================
                COURSES
            ================================================== */}

            <Route
              path="/student/courses"
              element={<CoursesPage />}
            />

            <Route
              path="/student/courses/:id"
              element={<CourseDetailPage />}
            />

            <Route
              path="/student/topics/:id"
              element={<TopicViewerPage />}
            />

            <Route
              path="/student/quiz/topic/:topicId"
              element={<QuizPage />}
            />

            <Route
              path="/student/quiz/course/:courseId"
              element={<QuizPage />}
            />


            {/* ==================================================
                DSA
            ================================================== */}

            <Route
              path="/student/dsa"
              element={<DsaDashboardPage />}
            />

            <Route
              path="/student/dsa/problems"
              element={<DsaProblemListPage />}
            />

            <Route
              path="/student/dsa/problems/:id"
              element={<DsaProblemDetailPage />}
            />

            <Route
              path="/student/dsa/submissions"
              element={<DsaSubmissionsPage />}
            />


            {/* ==================================================
                INTERVIEW
            ================================================== */}

            <Route
              path="/student/interview"
              element={<InterviewDashboardPage />}
            />

            <Route
              path="/student/interview/topics"
              element={<InterviewTopicListPage />}
            />

            <Route
              path="/student/interview/companies"
              element={<InterviewCompanyListPage />}
            />

            <Route
              path="/student/interview/companies/:id"
              element={<InterviewCompanyDetailPage />}
            />

            <Route
              path="/student/interview/questions"
              element={<InterviewQuestionPracticePage />}
            />

            <Route
              path="/student/interview/mock"
              element={<MockInterviewSetupPage />}
            />

            <Route
              path="/student/interview/mock/:id"
              element={<LiveMockInterviewPage />}
            />

            <Route
              path="/student/interview/result/:id"
              element={<MockInterviewReportPage />}
            />


            {/* ==================================================
                MOCK INTERVIEW ALIASES
            ================================================== */}

            <Route
              path="/student/mock-interview"
              element={<MockInterviewSetupPage />}
            />

            <Route
              path="/student/mock-interview/setup"
              element={<MockInterviewSetupPage />}
            />

            <Route
              path="/student/mock-interview/:id"
              element={<LiveMockInterviewPage />}
            />

            <Route
              path="/student/mock-interview/result/:id"
              element={<MockInterviewReportPage />}
            />


            {/* ==================================================
                JOBS
            ================================================== */}

            <Route
              path="/student/jobs"
              element={<JobSearchPage />}
            />

            <Route
              path="/student/jobs/:id"
              element={<JobDetailPage />}
            />

            <Route
              path="/student/jobs/saved"
              element={<SavedJobsPage />}
            />

            <Route
              path="/student/applications"
              element={<ApplicationTrackerPage />}
            />

            <Route
              path="/student/applications/:id"
              element={<ApplicationDetailPage />}
            />

            <Route
              path="/student/placements"
              element={<PlacementDriveListPage />}
            />

            <Route
              path="/student/placements/:id"
              element={<PlacementDriveDetailPage />}
            />

            <Route
              path="/student/job-preferences"
              element={<JobPreferencesPage />}
            />

            <Route
              path="/student/job-recommendations"
              element={<JobRecommendationsPage />}
            />


            {/* ==================================================
                RESUME
            ================================================== */}

            <Route
              path="/student/resume/analyzer"
              element={<ResumeAnalyzerPage />}
            />

            <Route
              path="/student/resume/builder"
              element={<ResumeBuilderPage />}
            />

            <Route
              path="/student/resume/resumes"
              element={<ResumeListPage />}
            />

            <Route
              path="/student/resume/resumes/:id"
              element={<ResumePreviewPage />}
            />

            <Route
              path="/student/resume/preview/:id"
              element={<ResumePreviewPage />}
            />

          </Route>
        </Route>


        {/* ======================================================
            TRAINER ROUTES
        ====================================================== */}

        <Route
          element={
            <PrivateRoute
              allowedRoles={['TRAINER', 'ADMIN']}
            >
              <TrainerLayout />
            </PrivateRoute>
          }
        >
          <Route
            path="/trainer/dashboard"
            element={<TrainerDashboard />}
          />
        </Route>


        {/* ======================================================
            ADMIN ROUTES
        ====================================================== */}

        <Route
          element={
            <PrivateRoute allowedRoles={['ADMIN']}>
              <AdminLayout />
            </PrivateRoute>
          }
        >
          <Route
            path="/admin/dashboard"
            element={<AdminDashboard />}
          />
        </Route>


        {/* ======================================================
            404
        ====================================================== */}

        <Route
          path="*"
          element={<NotFoundPage />}
        />

      </Routes>
    </BrowserRouter>
  );
}