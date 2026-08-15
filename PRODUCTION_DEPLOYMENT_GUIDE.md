# VCUBE Academy — Production Deployment Guide

## 1. Architecture Overview
VCUBE Academy is deployed as a modern micro-monolith consisting of:
- **Frontend SPA**: React 18 + TypeScript + Vite + TailwindCSS served via Nginx with HTML5 pushState routing.
- **Backend API**: Spring Boot 3.4.2 running on JDK 21 with Spring Security 6, stateless JWT authentication, and Spring Data JPA.
- **Relational Database**: PostgreSQL 16 Alpine with Flyway database version control (Migrations V1 through V19).
- **Optional AI Providers**: Gemini, OpenAI, or Claude (built-in deterministic fallback engine is active if no external API key is configured).

---

## 2. Prerequisites
- **Docker Engine**: Version 24.0+ and **Docker Compose** v2.20+.
- **Host Memory**: Minimum 4 GB RAM (8 GB recommended for concurrent builds).
- **Host Ports**:
  - `80` or `5173` (Frontend HTTP)
  - `8080` (Backend API HTTP)
  - `5432` (PostgreSQL Database)

---

## 3. Environment Variables Configuration

Copy `.env.example` to `.env` in the project root and fill in all values:

```bash
cp .env.example .env
```

**Required variables** (the application will NOT start without these):

```env
# ─── PostgreSQL Database ─────────────────────────────────────────────────────
POSTGRES_DB=vcube_academy
POSTGRES_USER=vcube_user
POSTGRES_PASSWORD=<strong_unique_password>
DATABASE_URL=jdbc:postgresql://postgres:5432/vcube_academy
DATABASE_USERNAME=vcube_user
DATABASE_PASSWORD=<strong_unique_password>

# ─── Security & JWT ───────────────────────────────────────────────────────────
# Generate with: openssl rand -base64 64
JWT_SECRET=<your_cryptographically_secure_256bit_key>
JWT_EXPIRATION_MS=86400000

# ─── CORS & Network ──────────────────────────────────────────────────────────
CORS_ALLOWED_ORIGINS=https://academy.vcube.com

# ─── Frontend Build ───────────────────────────────────────────────────────────
VITE_API_BASE_URL=https://api.vcube.com/api

# ─── Optional: External AI Provider ─────────────────────────────────────────
AI_PROVIDER=BUILTIN
AI_API_KEY=
```

> **⚠️ Security**: Never commit `.env` to version control. It is excluded via `.gitignore`.

---

## 4. Single-Command Production Deployment

```bash
# Take a database backup before any deployment
docker exec vcube-postgres pg_dump -U vcube_user vcube_academy > backup_pre_release.sql

# Build images and start all containers
docker compose up -d --build

# Verify all containers are healthy
docker compose ps

# Stream backend startup and Flyway migration logs
docker compose logs -f backend

# Verify health endpoint
curl http://localhost:8080/api/actuator/health
```

Expected response: `{"status":"UP"}`

---

## 5. Manual / Local Development Setup

### Backend (Spring Boot):
```bash
cd backend
mvn clean compile -DskipTests
mvn spring-boot:run
```
Access backend API: `http://localhost:8080/api`
Swagger UI (Dev mode): `http://localhost:8080/api/swagger-ui.html`

### Frontend (Vite + React):
```bash
cd frontend
npm install
npm run dev
```
Access frontend portal: `http://localhost:5173`

---

## 6. Default Admin & Trainer Accounts

On initial Flyway migration startup, the following standard role accounts are provisioned:

| Role | Email | Default Password |
| :--- | :--- | :--- |
| Admin | `admin@vcube.com` | `Admin@123` |
| Trainer (Srikanth) | `srikanth@vcube.com` | `Trainer@123` |
| Trainer (Viswanath) | `viswanath@vcube.com` | `Trainer@123` |
| Student | Self-register at `/register` | — |

> **⚠️ Action required**: Change all default passwords immediately after the first login in production.

---

## 7. SSL / TLS in Production

The Docker Compose stack exposes HTTP on port 8080 (backend) and port 5173/80 (frontend).
**Do not expose these ports directly on the public internet.**

Place an SSL-terminating reverse proxy in front:
- **Nginx** with Let's Encrypt (Certbot)
- **Caddy** (automatic HTTPS)
- **AWS Application Load Balancer** (ACM certificate)
- **Cloudflare Proxy** (flexible SSL)

---

## 8. Rollback Procedure

```bash
# Stop all containers
docker compose down

# Restore the pre-deployment database backup
docker exec -i vcube-postgres psql -U vcube_user vcube_academy < backup_pre_release.sql

# Checkout the previous release tag
git checkout v0.9.0

# Rebuild and restart with the previous version
docker compose up -d --build
```
