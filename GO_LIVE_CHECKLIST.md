# VCUBE Academy — Go-Live Checklist

**Version**: 1.0.0 | **Date**: August 15, 2026

Use this checklist sequentially. Check each item before proceeding to the next section.

---

## SECTION 1 — Pre-Deployment Checks

- [ ] Source code is on the correct release branch/tag (`v1.0.0`)
- [ ] All 27 backend integration tests pass: `mvn clean test`
- [ ] Backend JAR builds successfully: `mvn clean package -DskipTests`
- [ ] Frontend production bundle builds successfully: `npm run build`
- [ ] No secrets, passwords, or API keys exist in tracked source files
- [ ] `.env` is listed in `.gitignore` and is NOT committed to the repository
- [ ] `docker-compose.yml` uses `${VAR}` references only — no inline credentials
- [ ] `application.yml` uses `${JWT_SECRET}` with no hardcoded fallback

---

## SECTION 2 — Environment Configuration

- [ ] `.env` file created on the production host from `.env.example` template
- [ ] `POSTGRES_DB` set to the production database name
- [ ] `POSTGRES_USER` set to the production database user
- [ ] `POSTGRES_PASSWORD` set to a strong, unique password (minimum 20 characters)
- [ ] `DATABASE_URL` set to `jdbc:postgresql://postgres:5432/<POSTGRES_DB>`
- [ ] `DATABASE_USERNAME` matches `POSTGRES_USER`
- [ ] `DATABASE_PASSWORD` matches `POSTGRES_PASSWORD`
- [ ] `JWT_SECRET` generated using: `openssl rand -base64 64`
- [ ] `JWT_EXPIRATION_MS` configured (default `86400000` = 24 hours)
- [ ] `CORS_ALLOWED_ORIGINS` set to the exact frontend domain (e.g., `https://academy.vcube.com`)
- [ ] `VITE_API_BASE_URL` set to the public backend API URL (e.g., `https://api.vcube.com/api`)
- [ ] `AI_PROVIDER` and `AI_API_KEY` configured if external LLM integration is required
- [ ] `.env` file permissions set to `600` (owner read/write only) on Linux hosts

---

## SECTION 3 — Database Setup

- [ ] PostgreSQL 16 host or managed instance is accessible
- [ ] Database connection verified from the deployment host
- [ ] If using managed PostgreSQL (AWS RDS / Cloud SQL): update `DATABASE_URL` host accordingly
- [ ] `DATABASE_URL` in `.env` points to the correct host (not `localhost` inside Docker unless using host networking)
- [ ] Confirm the PostgreSQL user has `CREATE TABLE`, `CREATE INDEX`, and `ALTER TABLE` privileges for Flyway to run

---

## SECTION 4 — Migration Verification

- [ ] Start the backend container: `docker compose up backend`
- [ ] Check backend logs for Flyway output: `docker compose logs -f backend`
- [ ] Confirm log line: `Successfully applied 23 migration(s) to schema "public"`
- [ ] Confirm log line: `Started VcubeAcademyApplication in X seconds`
- [ ] No `FlywayException` or `MigrationException` errors in logs
- [ ] Verify migration history in database:
  ```sql
  SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
  ```
- [ ] All 23 rows show `success = true`

---

## SECTION 5 — Backend Deployment

**Commands (run from project root):**

```bash
# Step 1: Pull latest code
git pull origin main

# Step 2: Build and start all services
docker compose up -d --build

# Step 3: Check all containers are healthy
docker compose ps

# Step 4: Stream backend startup logs
docker compose logs -f backend
```

- [ ] `vcube-postgres` container status: `healthy`
- [ ] `vcube-backend` container status: `running`
- [ ] Health endpoint returns `{"status":"UP"}`:
  ```bash
  curl http://localhost:8080/api/actuator/health
  ```
- [ ] No `ERROR` or `FATAL` lines in backend startup logs
- [ ] No Flyway migration failures in logs
- [ ] No `BeanCreationException` or `ApplicationContext` failures in logs

---

## SECTION 6 — Frontend Deployment

- [ ] `vcube-frontend` container status: `running`
- [ ] Frontend is accessible in browser: `http://localhost:5173` (or configured domain)
- [ ] Login page renders correctly with VCUBE branding
- [ ] No 404 errors on page refresh (SPA routing via Nginx `try_files` confirmed)
- [ ] Browser DevTools → Network tab shows API calls going to the correct `VITE_API_BASE_URL`
- [ ] No Content Security Policy (CSP) or CORS errors in browser console

---

## SECTION 7 — Smoke Testing

Run these tests against the live deployment:

**Authentication**
- [ ] `POST /api/auth/login` with `admin@vcube.com` / `Admin@123` → returns JWT token
- [ ] `POST /api/auth/login` with `srikanth@vcube.com` / `Trainer@123` → returns JWT token
- [ ] `POST /api/auth/register` with new student email → registers successfully

**Student Role**
- [ ] `GET /api/courses` → returns course list
- [ ] `GET /api/student/dashboard` with Student JWT → returns dashboard data
- [ ] `GET /api/dsa/problems` → returns DSA problem list
- [ ] `GET /api/interview/companies` → returns company list
- [ ] `GET /api/jobs` → returns job listings
- [ ] `GET /api/student/bookmarks` with Student JWT → returns bookmark list
- [ ] `GET /api/student/notifications` with Student JWT → returns notification list
- [ ] `GET /api/student/gamification/summary` with Student JWT → returns streaks/XP/badges
- [ ] `GET /api/search?q=java` → returns categorized search results

**RBAC Isolation**
- [ ] `GET /api/student/profile` with Trainer JWT → returns `403 Forbidden`
- [ ] `GET /api/admin/dashboard` with Student JWT → returns `403 Forbidden`
- [ ] `GET /api/student/bookmarks` without JWT → returns `401 Unauthorized`

**Trainer Role**
- [ ] `GET /api/trainer/dashboard` with Trainer JWT → returns dashboard data

**Admin Role**
- [ ] `GET /api/admin/dashboard` with Admin JWT → returns dashboard data

---

## SECTION 8 — Rollback Procedure

If a critical issue is found after go-live:

```bash
# Stop all running containers
docker compose down

# Restore previous Docker images (if tagged)
docker compose up -d --no-build

# OR: Roll back to the previous Git tag and rebuild
git checkout v0.9.0
docker compose up -d --build
```

**Database rollback**:
- Flyway does not auto-rollback. If schema changes caused the issue, restore from the pre-deployment database snapshot/backup taken in Section 3.
- Flyway undo scripts (if provided) can be applied manually.

> **⚠️ Always take a full PostgreSQL backup before deploying**: 
> `docker exec vcube-postgres pg_dump -U vcube_user vcube_academy > backup_pre_release.sql`

---

## SECTION 9 — Post-Deployment Monitoring

- [ ] Schedule daily backup of the `postgres_data` Docker volume or managed DB
- [ ] Monitor `/api/actuator/health` endpoint — set up an uptime check (UptimeRobot, Pingdom, etc.)
- [ ] Review backend logs for unexpected `500 Internal Server Error` responses:
  ```bash
  docker compose logs backend | grep "ERROR\|500"
  ```
- [ ] Change default trainer and admin passwords immediately:
  - `admin@vcube.com` / `Admin@123` → new secure password
  - `srikanth@vcube.com` / `Trainer@123` → new secure password
  - `viswanath@vcube.com` / `Trainer@123` → new secure password
- [ ] Configure SSL/TLS via a reverse proxy (Nginx, Caddy, or cloud load balancer) — do not expose HTTP port 8080 directly on public-facing deployments
- [ ] Set up log rotation for Docker container logs
- [ ] Confirm CORS is locked to production domain only (remove `localhost` origins from `CORS_ALLOWED_ORIGINS`)
