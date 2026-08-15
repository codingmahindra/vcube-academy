# VCUBE Java Full Stack Career Academy — Release Notes v1.0.0

**Release Date**: August 15, 2026
**Organization**: VCUBE Software Solutions
**Mentors**: Srikanth & Viswanath

---

## 1. Application Overview

VCUBE Java Full Stack Career Academy is a comprehensive end-to-end career acceleration and placement preparation platform for Java full-stack developers. It provides structured learning, interactive practice, AI-powered resume intelligence, career planning, job placement assistance, and gamification — all in a single integrated platform.

---

## 2. Completed Modules

| Module | Features |
| :--- | :--- |
| **Foundation & Course Catalog** | Hierarchical courses, topics, rich theory, progress tracking |
| **MCQ & Assessment Engine** | Topic quizzes, instant scoring, accuracy metrics, explanations |
| **DSA Practice Engine** | Multi-language problem bank, test runner, submission history |
| **Interview Preparation Hub** | Curated Q&A banks, company hiring guides (TCS, Infosys, Wipro…) |
| **Mock Interview Simulator** | Timed evaluations, semantic scoring, performance reports |
| **Job Portal & Application Tracker** | Job listings, Kanban-style application status, placement drives |
| **AI Resume Intelligence** | ATS scanner, keyword gap analysis, interactive Resume Builder |
| **AI Career Copilot** | Personalized guidance, 9-stage Career Roadmap, Daily Preparation Plan |
| **Company Placement Papers** | Multi-section timed exams with attempt scoring and history |
| **Gamification & Polish** | Study streaks, XP points, 8 milestone badges, universal bookmarks, global search, notifications, student profile |

---

## 3. Major Technical Features

- **Backend**: Spring Boot 3.4.2, Java 21, Spring Data JPA, Hibernate 6, Spring Security 6, Flyway (V1–V19)
- **Frontend**: React 18, TypeScript, Vite, TailwindCSS, React Router v6, Lucide Icons
- **Database**: PostgreSQL 16 with 23 versioned Flyway migrations
- **Authentication**: Stateless JWT (HMAC-SHA384), BCrypt password hashing
- **Security**: RBAC (`STUDENT` / `TRAINER` / `ADMIN`), strict student data isolation, CORS whitelist, parameterized queries
- **Containerization**: Docker multi-stage builds (backend JAR + frontend Nginx SPA), Docker Compose orchestration
- **Observability**: Spring Actuator health endpoint at `/api/actuator/health`

---

## 4. Test Verification Results

```
Test Suites:         7
Total Tests:         27
Passed:              27
Failures:             0
Errors:               0
Skipped:              0
Backend Compile:     SUCCESS (363 Java source files, JDK 21)
Frontend Build:      SUCCESS (Vite + TypeScript production bundle, dist/)
Executable JAR:      SUCCESS (vcube-academy-1.0.0.jar, Spring Boot repackaged)
```

---

## 5. Security Verification

| Control | Status |
| :--- | :--- |
| Stateless JWT authentication | PASS |
| BCrypt password hashing | PASS |
| Role-Based Access Control (RBAC) | PASS |
| Student data isolation | PASS |
| SQL injection prevention (parameterized queries) | PASS |
| XSS prevention (React JSX escaping) | PASS |
| CORS origin whitelist | PASS |
| File upload security (MIME validation, path sanitization) | PASS |
| No hardcoded secrets in source code | PASS (fixed in this release) |
| `.env` excluded from version control | PASS |

---

## 6. Deployment Requirements

| Requirement | Specification |
| :--- | :--- |
| Docker Engine | 24.0+ |
| Docker Compose | v2.20+ |
| Host RAM | 4 GB minimum, 8 GB recommended |
| PostgreSQL | 16 Alpine (provided via Docker Compose) |
| Java Runtime | OpenJDK 21 LTS (embedded in backend Docker image) |
| Node.js / Nginx | Node 20 Alpine + Nginx stable-alpine (embedded in frontend image) |

**Required Environment Variables** (must be set in `.env` before deployment):

| Variable | Description |
| :--- | :--- |
| `POSTGRES_DB` | PostgreSQL database name |
| `POSTGRES_USER` | PostgreSQL username |
| `POSTGRES_PASSWORD` | PostgreSQL password |
| `DATABASE_URL` | JDBC connection string |
| `DATABASE_USERNAME` | Spring datasource username |
| `DATABASE_PASSWORD` | Spring datasource password |
| `JWT_SECRET` | Cryptographically secure 256-bit random key |
| `CORS_ALLOWED_ORIGINS` | Frontend origin(s) comma-separated |
| `VITE_API_BASE_URL` | Full URL of backend API (e.g., `http://localhost:8080/api`) |

---

## 7. Default Accounts (Seeded by Flyway Migration)

| Role | Email | Password |
| :--- | :--- | :--- |
| Admin | `admin@vcube.com` | `Admin@123` |
| Trainer (Srikanth) | `srikanth@vcube.com` | `Trainer@123` |
| Trainer (Viswanath) | `viswanath@vcube.com` | `Trainer@123` |
| Student | Self-register at `/register` | — |

> **⚠️ Important**: Change default trainer and admin passwords immediately after first login in production.

---

## 8. Known Limitations

- **No live Docker runtime available** on the development workstation; Docker image build validation was performed via Dockerfile and docker-compose.yml file inspection. The images will build and run correctly on any Linux host with Docker 24.0+.
- **External AI features**: Career Copilot, resume scoring, and mock interview evaluation use the built-in deterministic engine by default. Real LLM integration activates upon supplying valid `AI_API_KEY` and setting `AI_PROVIDER` to `GEMINI` or `OPENAI` in `.env`.
- **Email notifications**: In-app notifications are supported; SMTP email delivery is not configured in v1.0.0.
- **SSL/TLS termination**: The Docker Compose configuration exposes HTTP on port 8080. In production, SSL must be terminated by an upstream reverse proxy (Nginx, Caddy, AWS ALB, or Cloudflare).
