-- ============================================================
-- V6: Student Progress Schema
-- ============================================================

-- Track which topics a student has completed
CREATE TABLE IF NOT EXISTS topic_completions (
    id             BIGSERIAL PRIMARY KEY,
    student_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    topic_id       BIGINT NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    completed_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, topic_id)
);

CREATE INDEX IF NOT EXISTS idx_topic_completions_student_id ON topic_completions(student_id);
CREATE INDEX IF NOT EXISTS idx_topic_completions_topic_id   ON topic_completions(topic_id);

-- Aggregated student progress per course
CREATE TABLE IF NOT EXISTS student_progress (
    id                       BIGSERIAL PRIMARY KEY,
    student_id               BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id                BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    topics_completed         INT NOT NULL DEFAULT 0,
    total_topics             INT NOT NULL DEFAULT 0,
    quiz_attempts            INT NOT NULL DEFAULT 0,
    total_correct            INT NOT NULL DEFAULT 0,
    total_attempted_questions INT NOT NULL DEFAULT 0,
    last_activity_at         TIMESTAMP WITH TIME ZONE,
    updated_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, course_id)
);

CREATE INDEX IF NOT EXISTS idx_student_progress_student_id ON student_progress(student_id);
CREATE INDEX IF NOT EXISTS idx_student_progress_course_id  ON student_progress(course_id);

-- Weak topics computed from quiz results
CREATE TABLE IF NOT EXISTS weak_topics (
    id                   BIGSERIAL PRIMARY KEY,
    student_id           BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    topic_id             BIGINT NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    total_questions      INT NOT NULL DEFAULT 0,
    correct_count        INT NOT NULL DEFAULT 0,
    accuracy_pct         NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    last_attempted_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, topic_id)
);

CREATE INDEX IF NOT EXISTS idx_weak_topics_student_id   ON weak_topics(student_id);
CREATE INDEX IF NOT EXISTS idx_weak_topics_accuracy_pct ON weak_topics(accuracy_pct);
