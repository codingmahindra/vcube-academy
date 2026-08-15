-- ============================================================
-- V5: MCQ / Quiz Engine Schema
-- ============================================================

-- Questions
CREATE TABLE IF NOT EXISTS questions (
    id                   BIGSERIAL PRIMARY KEY,
    topic_id             BIGINT REFERENCES topics(id) ON DELETE SET NULL,
    course_id            BIGINT REFERENCES courses(id) ON DELETE SET NULL,
    question_text        TEXT NOT NULL,
    difficulty           VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (difficulty IN ('EASY','MEDIUM','HARD')),
    explanation          TEXT,
    interview_point      TEXT,
    company_tags         VARCHAR(500),
    is_active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_by           BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_questions_topic_id    ON questions(topic_id);
CREATE INDEX IF NOT EXISTS idx_questions_course_id   ON questions(course_id);
CREATE INDEX IF NOT EXISTS idx_questions_difficulty  ON questions(difficulty);
CREATE INDEX IF NOT EXISTS idx_questions_is_active   ON questions(is_active);

-- Question options (4 per question)
CREATE TABLE IF NOT EXISTS question_options (
    id              BIGSERIAL PRIMARY KEY,
    question_id     BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    option_label    VARCHAR(5) NOT NULL,        -- 'A','B','C','D'
    option_text     TEXT NOT NULL,
    is_correct      BOOLEAN NOT NULL DEFAULT FALSE,
    why_wrong       TEXT,                       -- shown when student picks this wrong option
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_question_options_question_id ON question_options(question_id);

-- Quiz definitions (reusable)
CREATE TABLE IF NOT EXISTS quizzes (
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(200) NOT NULL,
    quiz_type      VARCHAR(50) NOT NULL DEFAULT 'TOPIC_QUIZ',
    topic_id       BIGINT REFERENCES topics(id) ON DELETE SET NULL,
    course_id      BIGINT REFERENCES courses(id) ON DELETE SET NULL,
    difficulty     VARCHAR(20) CHECK (difficulty IN ('EASY','MEDIUM','HARD')),
    question_count INT NOT NULL DEFAULT 10,
    time_limit_min INT,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_quizzes_quiz_type  ON quizzes(quiz_type);
CREATE INDEX IF NOT EXISTS idx_quizzes_topic_id   ON quizzes(topic_id);
CREATE INDEX IF NOT EXISTS idx_quizzes_course_id  ON quizzes(course_id);

-- Quiz attempts (one per student quiz session)
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id             BIGSERIAL PRIMARY KEY,
    student_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    quiz_type      VARCHAR(50) NOT NULL,
    topic_id       BIGINT REFERENCES topics(id) ON DELETE SET NULL,
    course_id      BIGINT REFERENCES courses(id) ON DELETE SET NULL,
    difficulty     VARCHAR(20),
    status         VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS','COMPLETED','ABANDONED')),
    total_questions INT NOT NULL DEFAULT 0,
    current_index  INT NOT NULL DEFAULT 0,
    started_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    completed_at   TIMESTAMP WITH TIME ZONE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_quiz_attempts_student_id ON quiz_attempts(student_id);
CREATE INDEX IF NOT EXISTS idx_quiz_attempts_status     ON quiz_attempts(status);

-- Questions assigned to an attempt (ordered)
CREATE TABLE IF NOT EXISTS attempt_questions (
    id             BIGSERIAL PRIMARY KEY,
    attempt_id     BIGINT NOT NULL REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    question_id    BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    display_order  INT NOT NULL,
    UNIQUE (attempt_id, display_order)
);

CREATE INDEX IF NOT EXISTS idx_attempt_questions_attempt_id ON attempt_questions(attempt_id);

-- Individual answers within an attempt
CREATE TABLE IF NOT EXISTS quiz_answers (
    id                   BIGSERIAL PRIMARY KEY,
    attempt_id           BIGINT NOT NULL REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    question_id          BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    selected_option_id   BIGINT REFERENCES question_options(id) ON DELETE SET NULL,
    is_correct           BOOLEAN NOT NULL DEFAULT FALSE,
    answered_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (attempt_id, question_id)
);

CREATE INDEX IF NOT EXISTS idx_quiz_answers_attempt_id  ON quiz_answers(attempt_id);
CREATE INDEX IF NOT EXISTS idx_quiz_answers_question_id ON quiz_answers(question_id);

-- Quiz results (computed after completion)
CREATE TABLE IF NOT EXISTS quiz_results (
    id                   BIGSERIAL PRIMARY KEY,
    attempt_id           BIGINT NOT NULL UNIQUE REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    student_id           BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_questions      INT NOT NULL DEFAULT 0,
    attempted_count      INT NOT NULL DEFAULT 0,
    correct_count        INT NOT NULL DEFAULT 0,
    wrong_count          INT NOT NULL DEFAULT 0,
    score_percentage     NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    time_taken_seconds   INT NOT NULL DEFAULT 0,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_quiz_results_student_id  ON quiz_results(student_id);
CREATE INDEX IF NOT EXISTS idx_quiz_results_attempt_id  ON quiz_results(attempt_id);
