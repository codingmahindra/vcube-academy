-- =========================================================================
-- V9: DSA Practice Engine Schema
-- =========================================================================

-- 1. DSA Categories
CREATE TABLE dsa_categories (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL UNIQUE,
    slug          VARCHAR(100) NOT NULL UNIQUE,
    description   TEXT,
    icon          VARCHAR(50),
    display_order INT NOT NULL DEFAULT 0,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dsa_categories_slug
    ON dsa_categories(slug);


-- 2. DSA Problems
CREATE TABLE dsa_problems (
    id                   BIGSERIAL PRIMARY KEY,
    category_id          BIGINT NOT NULL
                         REFERENCES dsa_categories(id)
                         ON DELETE RESTRICT,
    title                VARCHAR(250) NOT NULL,
    slug                 VARCHAR(250) NOT NULL UNIQUE,
    description          TEXT NOT NULL,
    difficulty           VARCHAR(20) NOT NULL DEFAULT 'EASY',
    subtopic             VARCHAR(100),
    constraints          TEXT,
    input_format         TEXT,
    output_format        TEXT,
    expected_approach    TEXT,
    time_complexity      VARCHAR(50),
    space_complexity     VARCHAR(50),
    hints                TEXT,
    interview_points     TEXT,
    company_tags         TEXT,
    java_starter_code    TEXT NOT NULL,
    solution_explanation TEXT,
    solution_java_code   TEXT,
    is_published         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dsa_problems_category
    ON dsa_problems(category_id);

CREATE INDEX idx_dsa_problems_difficulty
    ON dsa_problems(difficulty);

CREATE INDEX idx_dsa_problems_slug
    ON dsa_problems(slug);


-- 3. DSA Test Cases
CREATE TABLE dsa_test_cases (
    id              BIGSERIAL PRIMARY KEY,
    problem_id      BIGINT NOT NULL
                    REFERENCES dsa_problems(id)
                    ON DELETE CASCADE,
    input           TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    is_sample       BOOLEAN NOT NULL DEFAULT FALSE,
    is_hidden       BOOLEAN NOT NULL DEFAULT TRUE,
    explanation     TEXT,
    display_order   INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_dsa_test_cases_problem
    ON dsa_test_cases(problem_id);


-- 4. DSA Submissions
CREATE TABLE dsa_submissions (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL
                      REFERENCES users(id)
                      ON DELETE CASCADE,
    problem_id        BIGINT NOT NULL
                      REFERENCES dsa_problems(id)
                      ON DELETE CASCADE,
    language          VARCHAR(20) NOT NULL DEFAULT 'JAVA',
    source_code       TEXT NOT NULL,
    status            VARCHAR(40) NOT NULL,
    execution_time_ms BIGINT,
    memory_used_kb    BIGINT,
    passed_test_cases INT NOT NULL DEFAULT 0,
    total_test_cases  INT NOT NULL DEFAULT 0,
    error_output      TEXT,
    submitted_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dsa_submissions_user
    ON dsa_submissions(user_id);

CREATE INDEX idx_dsa_submissions_problem
    ON dsa_submissions(problem_id);

CREATE INDEX idx_dsa_submissions_user_problem
    ON dsa_submissions(user_id, problem_id);


-- 5. DSA Student Progress
CREATE TABLE dsa_student_progress (
    id                     BIGSERIAL PRIMARY KEY,
    user_id                BIGINT NOT NULL
                           REFERENCES users(id)
                           ON DELETE CASCADE,
    problem_id             BIGINT NOT NULL
                           REFERENCES dsa_problems(id)
                           ON DELETE CASCADE,
    is_solved              BOOLEAN NOT NULL DEFAULT FALSE,
    attempt_count          INT NOT NULL DEFAULT 0,
    best_execution_time_ms BIGINT,
    solved_at              TIMESTAMP WITH TIME ZONE,
    last_attempt_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_dsa_problem
        UNIQUE(user_id, problem_id)
);

CREATE INDEX idx_dsa_progress_user
    ON dsa_student_progress(user_id);