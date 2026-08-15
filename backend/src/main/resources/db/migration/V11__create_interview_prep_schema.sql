-- V11: Create Interview Preparation Schema

-- 1. Interview Categories
CREATE TABLE interview_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(50),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Interview Topics
CREATE TABLE interview_topics (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES interview_categories(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_interview_topic_slug UNIQUE (category_id, slug)
);

-- 3. Companies Catalog
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(255),
    description TEXT,
    industry VARCHAR(100),
    tier VARCHAR(50) DEFAULT 'TIER_1',
    hiring_rounds_info TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Interview Questions
CREATE TABLE interview_questions (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL REFERENCES interview_topics(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL DEFAULT 'CONCEPTUAL',
    difficulty VARCHAR(30) NOT NULL DEFAULT 'INTERMEDIATE',
    interview_round VARCHAR(50) NOT NULL DEFAULT 'ROUND_3_TECHNICAL',
    question_source VARCHAR(50) NOT NULL DEFAULT 'PRACTICE_QUESTION',
    source_reference VARCHAR(255),
    expected_answer TEXT NOT NULL,
    explanation TEXT NOT NULL,
    interview_points TEXT,
    common_mistakes TEXT,
    follow_up_questions TEXT,
    real_world_example TEXT,
    evaluation_keywords TEXT,
    is_published BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Company Interview Question Mapping
CREATE TABLE company_interview_questions (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES interview_questions(id) ON DELETE CASCADE,
    frequency VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    last_seen_year INT,
    role_title VARCHAR(100) DEFAULT 'Java Full Stack Developer',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_company_question UNIQUE (company_id, question_id)
);

-- 6. Mock Interviews
CREATE TABLE mock_interviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    role_title VARCHAR(100) NOT NULL DEFAULT 'Java Full Stack Developer',
    target_company_id BIGINT REFERENCES companies(id) ON DELETE SET NULL,
    interview_type VARCHAR(50) NOT NULL DEFAULT 'TECHNICAL',
    difficulty VARCHAR(30) NOT NULL DEFAULT 'INTERMEDIATE',
    total_questions INT NOT NULL DEFAULT 5,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',
    overall_score DOUBLE PRECISION,
    technical_score DOUBLE PRECISION,
    java_score DOUBLE PRECISION,
    sql_score DOUBLE PRECISION,
    spring_score DOUBLE PRECISION,
    dsa_score DOUBLE PRECISION,
    hr_score DOUBLE PRECISION,
    communication_score DOUBLE PRECISION,
    interview_readiness_percentage INT,
    recommendation_status VARCHAR(50),
    feedback_summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

-- 7. Mock Interview Questions
CREATE TABLE mock_interview_questions (
    id BIGSERIAL PRIMARY KEY,
    mock_interview_id BIGINT NOT NULL REFERENCES mock_interviews(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES interview_questions(id) ON DELETE CASCADE,
    question_order INT NOT NULL,
    user_answer TEXT,
    time_taken_seconds INT DEFAULT 0,
    score DOUBLE PRECISION,
    technical_accuracy_score DOUBLE PRECISION,
    completeness_score DOUBLE PRECISION,
    clarity_score DOUBLE PRECISION,
    feedback TEXT,
    missing_points TEXT,
    improved_answer TEXT,
    evaluated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_mock_question_order UNIQUE (mock_interview_id, question_order)
);

-- 8. Single Question Practice Evaluations
CREATE TABLE interview_evaluations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES interview_questions(id) ON DELETE CASCADE,
    user_answer TEXT NOT NULL,
    score DOUBLE PRECISION NOT NULL,
    feedback TEXT NOT NULL,
    strengths TEXT,
    weaknesses TEXT,
    missing_points TEXT,
    improved_answer TEXT,
    evaluated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9. Student Interview Progress
CREATE TABLE interview_student_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES interview_questions(id) ON DELETE CASCADE,
    is_completed BOOLEAN NOT NULL DEFAULT false,
    last_score DOUBLE PRECISION DEFAULT 0.0,
    practice_count INT NOT NULL DEFAULT 1,
    last_practiced_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_student_interview_question UNIQUE (user_id, question_id)
);

-- Indices for rapid lookup
CREATE INDEX idx_int_topic_cat ON interview_topics(category_id);
CREATE INDEX idx_int_q_topic ON interview_questions(topic_id);
CREATE INDEX idx_int_q_type ON interview_questions(question_type);
CREATE INDEX idx_int_q_diff ON interview_questions(difficulty);
CREATE INDEX idx_comp_q_comp ON company_interview_questions(company_id);
CREATE INDEX idx_comp_q_q ON company_interview_questions(question_id);
CREATE INDEX idx_mock_user ON mock_interviews(user_id);
CREATE INDEX idx_mock_q_mock ON mock_interview_questions(mock_interview_id);
CREATE INDEX idx_int_prog_user ON interview_student_progress(user_id);
