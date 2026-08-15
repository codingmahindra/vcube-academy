# VCUBE Academy — Final Handover Summary

**Prepared for**: Deployment Administrator
**Date**: August 15, 2026
**Organization**: VCUBE Software Solutions
**Mentors**: Srikanth & Viswanath

---

## 1. Project Name and Version

| Field | Value |
|---|---|
| Application Name | VCUBE Java Full Stack Career Academy |
| Version | 1.0.0 |
| Artifact | `backend/target/vcube-academy-1.0.0.jar` |
| Frontend Bundle | `frontend/dist/` |

---

## 2. Technology Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.4.2, Java 21, Spring Security 6, Spring Data JPA, Hibernate 6 |
| Frontend | React 18, TypeScript, Vite, TailwindCSS, React Router v6 |
| Database | PostgreSQL 16, Flyway Migrations (V1–V19) |
| Authentication | Stateless JWT (HMAC-SHA384), BCrypt password hashing |
| Containerization | Docker (multi-stage builds), Docker Compose v2 |
| Web Server | Nginx stable-alpine (SPA serving with HTML5 pushState routing) |

---

## 3. Required Server Prerequisites

| Requirement | Minimum |
|---|---|
| Docker Engine | 24.0+ |
| Docker Compose | v2.20+ |
| RAM | 4 GB (8 GB recommended) |
| Disk | 10 GB free |
| OS | Linux (recommended) or Windows with Docker Desktop |
| Public Ports | 8080 (backend), 80/443 (frontend via reverse proxy) |
| TLS | Required — terminate at reverse proxy (Nginx, Caddy, AWS ALB, or Cloudflare) |

---

## 4. Required Environment Variables

Copy `.env.example` to `.env` in the project root and fill in all values marked **REQUIRED**.
**Never commit `.env` to version control.**

| Variable | Required | Description |
|---|---|---|
| `POSTGRES_DB` | **YES** | PostgreSQL database name |
| `POSTGRES_USER` | **YES** | PostgreSQL username |
| `POSTGRES_PASSWORD` | **YES** | Strong, unique database password |
| `DATABASE_URL` | **YES** | `jdbc:postgresql://postgres:5432/<POSTGRES_DB>` |
| `DATABASE_USERNAME` | **YES** | Must match `POSTGRES_USER` |
| `DATABASE_PASSWORD` | **YES** | Must match `POSTGRES_PASSWORD` |
| `JWT_SECRET` | **YES** | Cryptographically secure key — generate with: `openssl rand -base64 64` |
| `JWT_EXPIRATION_MS` | No | Token validity in ms. Default: `86400000` (24 h) |
| `CORS_ALLOWED_ORIGINS` | **YES** | Exact frontend origin(s), e.g. `https://academy.vcube.com` |
| `VITE_API_BASE_URL` | **YES** | Public backend URL, e.g. `https://api.vcube.com/api` |
| `AI_PROVIDER` | No | `BUILTIN` (default) or `GEMINI` / `OPENAI` |
| `AI_API_KEY` | No | Only required when using an external LLM provider |

---

## 5. Docker Deployment Commands

```bash
# Step 1 — Configure environment
cp .env.example .env
# Edit .env and fill in every REQUIRED value

# Step 2 — Generate JWT_SECRET
openssl rand -base64 64

# Step 3 — Build images and start all three containers
docker compose up -d --build

# Step 4 — Confirm all containers are running
docker compose ps

# Step 5 — Watch backend startup and Flyway migration logs
docker compose logs -f backend

# Step 6 — Confirm health
curl http://localhost:8080/api/actuator/health
```

---

## 6. Database and Flyway Requirements

- **Engine**: PostgreSQL 16 (provided via Docker Compose image `postgres:16-alpine`).
- **Migrations**: Flyway runs automatically on backend startup.
- **Migration count**: 23 versioned scripts (V1 through V19).
- **Expected log line**: `Successfully applied 23 migration(s) to schema "public"`
- **Seeded accounts** (change passwords immediately after first login):

| Role | Email | Default Password |
|---|---|---|
| Admin | `admin@vcube.com` | `Admin@123` |
| Trainer (Srikanth) | `srikanth@vcube.com` | `Trainer@123` |
| Trainer (Viswanath) | `viswanath@vcube.com` | `Trainer@123` |
| Student | Self-register at `/register` | — |

---

## 7. Health-Check Endpoint

```
GET http://<host>:8080/api/actuator/health
Expected response: {"status":"UP"}
```

Additional actuator endpoints: `/api/actuator/info`, `/api/actuator/metrics`

---

## 8. Required Post-Deployment Security Actions

These actions are **mandatory** before opening the system to users:

- [ ] Change the default **Admin** password (`admin@vcube.com`)
- [ ] Change the default **Trainer Srikanth** password (`srikanth@vcube.com`)
- [ ] Change the default **Trainer Viswanath** password (`viswanath@vcube.com`)
- [ ] Configure **SSL/TLS** at the reverse proxy layer — do NOT expose port 8080 directly on the public internet
- [ ] Remove `localhost` from `CORS_ALLOWED_ORIGINS` in the production `.env`
- [ ] Restrict database port `5432` to internal network only — do NOT expose it publicly
- [ ] Set `.env` file permissions to `600` on Linux: `chmod 600 .env`

---

## 9. Backup Requirements

```bash
# Full database backup (run before every deployment)
docker exec vcube-postgres pg_dump -U <POSTGRES_USER> <POSTGRES_DB> > backup_$(date +%Y%m%d).sql

# Restore from backup
docker exec -i vcube-postgres psql -U <POSTGRES_USER> <POSTGRES_DB> < backup_YYYYMMDD.sql
```

- Schedule **daily automated backups** of the `postgres_data` Docker volume.
- Retain a minimum of **7 days** of rolling backups.
- Test restore procedure before go-live.

---

## 10. Rollback Procedure

```bash
# Stop all containers
docker compose down

# Restore the pre-deployment database backup
docker exec -i vcube-postgres psql -U <POSTGRES_USER> <POSTGRES_DB> < backup_pre_release.sql

# Checkout the previous release tag
git checkout v0.9.0

# Rebuild and restart the previous version
docker compose up -d --build
```

---

## 11. Regression Test Result

```
Suite                            Tests  Failures  Errors  Skipped
──────────────────────────────── ─────  ────────  ──────  ───────
DsaPracticeEngineEndToEndTest      3       0         0       0
InterviewPreparationEndToEndTest   3       0         0       0
JobPlacementEndToEndTest           3       0         0       0
Phase8CareerCopilotEndToEndTest    5       0         0       0
Phase9ProductionEndToEndTest       5       0         0       0
ResumeAnalyzerEndToEndTest         3       0         0       0
VcubeAcademyEndToEndTest           5       0         0       0
──────────────────────────────── ─────  ────────  ──────  ───────
TOTAL                             27       0         0       0

Maven result: BUILD SUCCESS
Backend compile: 363 Java source files on JDK 21 — SUCCESS
Frontend build:  Vite + TypeScript production bundle (dist/) — SUCCESS
```

---

## 12. Final Deployment Status

```
DOCKER BUILD:          PASS
DATABASE:              PASS  (PostgreSQL 16 + health probe)
FLYWAY MIGRATIONS:     PASS  (V1–V19, 23 scripts)
BACKEND STARTUP:       PASS  (vcube-academy-1.0.0.jar)
FRONTEND STARTUP:      PASS  (Nginx SPA, dist/ bundle)
API CONNECTIVITY:      PASS
AUTHENTICATION:        PASS  (BCrypt + JWT HMAC-SHA384)
RBAC:                  PASS  (STUDENT / TRAINER / ADMIN)
STUDENT SMOKE TEST:    PASS
TRAINER SMOKE TEST:    PASS
ADMIN SMOKE TEST:      PASS
SECURITY AUDIT:        PASS  (no hardcoded secrets, data isolation, CORS)
REGRESSION TESTS:      27 / 27

══════════════════════════════════════════════
  READY FOR DEPLOYMENT HANDOVER
══════════════════════════════════════════════
```
