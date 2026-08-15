-- =============================================================================
-- V17: Phase 8 — AI Career Copilot, Placement Papers, Daily Plan & Roadmap
-- =============================================================================

CREATE TABLE IF NOT EXISTS career_copilot_conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL DEFAULT 'Career Guidance Session',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_career_copilot_conversations_user ON career_copilot_conversations(user_id);

CREATE TABLE IF NOT EXISTS career_copilot_messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES career_copilot_conversations(id) ON DELETE CASCADE,
    sender VARCHAR(20) NOT NULL, -- USER, COPILOT
    message_text TEXT NOT NULL,
    recommended_actions TEXT, -- JSON array of actions / links
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS placement_papers (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT REFERENCES companies(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    paper_year VARCHAR(20) NOT NULL DEFAULT '2024',
    target_role VARCHAR(150) NOT NULL DEFAULT 'Software Engineer',
    round_name VARCHAR(100) NOT NULL DEFAULT 'Online Assessment (Cognitive + Technical)',
    duration_minutes INT NOT NULL DEFAULT 60,
    total_marks INT NOT NULL DEFAULT 100,
    passing_marks INT NOT NULL DEFAULT 60,
    difficulty VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    paper_source VARCHAR(50) NOT NULL DEFAULT 'VERIFIED',
    instructions TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_placement_papers_company ON placement_papers(company_id);

CREATE TABLE IF NOT EXISTS placement_paper_questions (
    id BIGSERIAL PRIMARY KEY,
    paper_id BIGINT NOT NULL REFERENCES placement_papers(id) ON DELETE CASCADE,
    section_name VARCHAR(100) NOT NULL, -- APTITUDE, REASONING, VERBAL, TECHNICAL_MCQ, SQL, JAVA, CODING
    question_text TEXT NOT NULL,
    option_a TEXT NOT NULL,
    option_b TEXT NOT NULL,
    option_c TEXT NOT NULL,
    option_d TEXT NOT NULL,
    correct_option VARCHAR(10) NOT NULL,
    explanation TEXT,
    marks INT NOT NULL DEFAULT 1,
    negative_marks NUMERIC(3, 2) DEFAULT 0.00,
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS placement_paper_attempts (
    id BIGSERIAL PRIMARY KEY,
    paper_id BIGINT NOT NULL REFERENCES placement_papers(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP WITH TIME ZONE,
    total_questions INT NOT NULL DEFAULT 0,
    correct_answers INT NOT NULL DEFAULT 0,
    wrong_answers INT NOT NULL DEFAULT 0,
    unanswered INT NOT NULL DEFAULT 0,
    score_obtained NUMERIC(6, 2) DEFAULT 0.00,
    percentage NUMERIC(5, 2) DEFAULT 0.00,
    is_passed BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS', -- IN_PROGRESS, COMPLETED, TIMED_OUT
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_placement_paper_attempts_user ON placement_paper_attempts(user_id);

CREATE TABLE IF NOT EXISTS placement_paper_answers (
    id BIGSERIAL PRIMARY KEY,
    attempt_id BIGINT NOT NULL REFERENCES placement_paper_attempts(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES placement_paper_questions(id) ON DELETE CASCADE,
    selected_option VARCHAR(10),
    is_correct BOOLEAN DEFAULT FALSE,
    time_taken_seconds INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS daily_preparation_plans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_date DATE NOT NULL DEFAULT CURRENT_DATE,
    total_tasks INT NOT NULL DEFAULT 0,
    completed_tasks INT NOT NULL DEFAULT 0,
    completion_percentage INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_daily_plan_user_date ON daily_preparation_plans(user_id, plan_date);

CREATE TABLE IF NOT EXISTS daily_plan_items (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL REFERENCES daily_preparation_plans(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL, -- JAVA_TOPIC, MCQ_PRACTICE, DSA_PROBLEM, SQL_PRACTICE, INTERVIEW_QA, MOCK_INTERVIEW, RESUME_POLISH, JOB_APPLY
    title VARCHAR(255) NOT NULL,
    target_count INT NOT NULL DEFAULT 1,
    completed_count INT NOT NULL DEFAULT 0,
    action_link VARCHAR(255),
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS career_weak_areas (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_or_topic_name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    weakness_score INT NOT NULL DEFAULT 70, -- 0-100 (higher = weaker)
    source_module VARCHAR(50) NOT NULL, -- MCQ, DSA, INTERVIEW, RESUME, PLACEMENT_PAPER
    recommendation_text TEXT,
    action_link VARCHAR(255),
    detected_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_career_weak_areas_user ON career_weak_areas(user_id);
