# VCUBE Academy — Production Environment Specification

## 1. Environment Topology
- **Production Host**: Linux / Windows Container Host or Cloud VM (AWS EC2 / GCP Compute / Azure VM).
- **Runtime Stack**:
  - OpenJDK 21 LTS (`eclipse-temurin:21-jre-alpine`)
  - Node.js 20 LTS / Nginx Stable (`nginx:stable-alpine`)
  - PostgreSQL 16 Alpine (`postgres:16-alpine`)
- **Port Allocations**:
  - `80` (HTTP Ingress / SSL termination via reverse proxy)
  - `8080` (Spring Boot API Gateway)
  - `5432` (PostgreSQL Database Engine)

---

## 2. Environment Variables & Secret Management

| Variable Name | Required | Example / Default | Description |
| :--- | :--- | :--- | :--- |
| `DATABASE_URL` | **YES** | `jdbc:postgresql://postgres:5432/vcube_academy` | PostgreSQL JDBC Connection String |
| `DATABASE_USERNAME` | **YES** | `vcube_user` | PostgreSQL Database User |
| `DATABASE_PASSWORD` | **YES** | `[SECURE_DB_PASSWORD]` | PostgreSQL Database Password |
| `JWT_SECRET` | **YES** | `[256-BIT_HEX_OR_BASE64_KEY]` | HMAC-SHA secret for signing access tokens |
| `JWT_EXPIRATION_MS`| NO | `86400000` (24h) | Token validity duration in milliseconds |
| `CORS_ALLOWED_ORIGINS`| **YES** | `https://academy.vcube.com,http://localhost:5173` | Allowed frontend origins whitelist |
| `AI_PROVIDER` | NO | `GEMINI` / `OPENAI` / `CLAUDE` | External LLM engine (Built-in engine used if omitted) |
| `AI_API_KEY` | NO | `[SECRET_API_KEY]` | API token for external generative AI provider |
| `VITE_API_BASE_URL` | **YES** | `http://localhost:8080/api` | Backend API URL used during frontend Vite build |

---

## 3. Database Migration Integrity (Flyway)

Flyway automates schema state across all 23 versioned migrations:
- `V1`: Core users, roles, permissions, course catalog schema
- `V2`–`V8`: Topics, rich learning content, quizzes, MCQ options
- `V9`–`V12`: LeetCode-style DSA problem repository & submission evaluations
- `V13`–`V15`: Interview preparation, company hiring hubs, mock interview rounds
- `V16`–`V18`: Job postings, placement drives, candidate applications
- `V19`–`V20`: AI Resume Intelligence, ATS scanning, keyword scoring
- `V21`–`V22`: Career Copilot, 9-stage roadmap, company placement papers
- `V19`: Gamification streaks, badges, universal bookmarks, student notifications, enhanced profiles

---

## 4. Operational Monitoring & Health Checks
- **Health Endpoint**: `GET /api/actuator/health` -> `{"status":"UP"}`
- **Database Liveness**: Checked via PostgreSQL `pg_isready` probe in `docker-compose.yml`.
- **Stateless Session Management**: Backing stores require zero session replication, allowing seamless horizontal scaling of backend replicas.
