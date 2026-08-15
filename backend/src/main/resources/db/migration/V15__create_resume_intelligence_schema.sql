-- =============================================================================
-- V15: Resume Intelligence System & ATS Analyzer Schema
-- =============================================================================

CREATE TABLE IF NOT EXISTS resume_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(50),
    location VARCHAR(150),
    linkedin_url VARCHAR(255),
    github_url VARCHAR(255),
    portfolio_url VARCHAR(255),
    professional_summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_resume_profiles_user_id ON resume_profiles(user_id);

CREATE TABLE IF NOT EXISTS resume_versions (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL REFERENCES resume_profiles(id) ON DELETE CASCADE,
    job_id BIGINT REFERENCES jobs(id) ON DELETE SET NULL,
    version_title VARCHAR(150) NOT NULL,
    target_role VARCHAR(150),
    target_company VARCHAR(150),
    template VARCHAR(50) NOT NULL DEFAULT 'ATS_CLASSIC',
    raw_resume_text TEXT,
    parsed_json TEXT,
    latest_ats_score INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_resume_versions_profile ON resume_versions(profile_id);
CREATE INDEX IF NOT EXISTS idx_resume_versions_job ON resume_versions(job_id);

CREATE TABLE IF NOT EXISTS resume_experiences (
    id BIGSERIAL PRIMARY KEY,
    version_id BIGINT NOT NULL REFERENCES resume_versions(id) ON DELETE CASCADE,
    company_name VARCHAR(150) NOT NULL,
    role_title VARCHAR(150) NOT NULL,
    location VARCHAR(100),
    start_date VARCHAR(50),
    end_date VARCHAR(50),
    is_current BOOLEAN DEFAULT FALSE,
    description TEXT,
    bullet_points TEXT,
    display_order INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS resume_educations (
    id BIGSERIAL PRIMARY KEY,
    version_id BIGINT NOT NULL REFERENCES resume_versions(id) ON DELETE CASCADE,
    institution VARCHAR(200) NOT NULL,
    degree VARCHAR(150) NOT NULL,
    field_of_study VARCHAR(150),
    start_year VARCHAR(20),
    end_year VARCHAR(20),
    score_or_cgpa VARCHAR(50),
    display_order INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS resume_projects (
    id BIGSERIAL PRIMARY KEY,
    version_id BIGINT NOT NULL REFERENCES resume_versions(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    tech_stack VARCHAR(255),
    live_url VARCHAR(255),
    github_url VARCHAR(255),
    description TEXT,
    bullet_points TEXT,
    display_order INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS resume_certifications (
    id BIGSERIAL PRIMARY KEY,
    version_id BIGINT NOT NULL REFERENCES resume_versions(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    issuing_organization VARCHAR(150),
    issue_date VARCHAR(50),
    credential_url VARCHAR(255),
    display_order INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS resume_analysis (
    id BIGSERIAL PRIMARY KEY,
    version_id BIGINT NOT NULL REFERENCES resume_versions(id) ON DELETE CASCADE,
    job_id BIGINT REFERENCES jobs(id) ON DELETE SET NULL,
    target_job_title VARCHAR(150),
    target_company_name VARCHAR(150),
    job_description_text TEXT,
    overall_ats_score INT NOT NULL,
    keyword_match_score INT NOT NULL,
    skills_match_score INT NOT NULL,
    experience_match_score INT NOT NULL,
    project_match_score INT NOT NULL,
    education_match_score INT NOT NULL,
    structure_score INT NOT NULL,
    ai_provider VARCHAR(50) DEFAULT 'RULE_BASED',
    summary_feedback TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_resume_analysis_version ON resume_analysis(version_id);

CREATE TABLE IF NOT EXISTS resume_keywords (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT NOT NULL REFERENCES resume_analysis(id) ON DELETE CASCADE,
    keyword_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    match_status VARCHAR(50) NOT NULL,
    importance VARCHAR(20) DEFAULT 'HIGH',
    occurrence_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS resume_missing_skills (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT NOT NULL REFERENCES resume_analysis(id) ON DELETE CASCADE,
    skill_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    importance VARCHAR(20) NOT NULL,
    why_it_matters TEXT,
    course_id BIGINT REFERENCES courses(id) ON DELETE SET NULL,
    dsa_problem_id BIGINT REFERENCES dsa_problems(id) ON DELETE SET NULL,
    interview_question_id BIGINT REFERENCES interview_questions(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS resume_recommendations (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT NOT NULL REFERENCES resume_analysis(id) ON DELETE CASCADE,
    section_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    actionable_fix TEXT
);

CREATE TABLE IF NOT EXISTS resume_analysis_history (
    id BIGSERIAL PRIMARY KEY,
    version_id BIGINT NOT NULL REFERENCES resume_versions(id) ON DELETE CASCADE,
    score_before INT,
    score_after INT NOT NULL,
    change_summary TEXT,
    analyzed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
