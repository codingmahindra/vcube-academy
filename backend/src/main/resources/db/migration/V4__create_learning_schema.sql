-- ============================================================
-- V4: Learning Platform Schema
-- ============================================================

-- Course categories (Core Java, Spring, DSA, etc.)
CREATE TABLE IF NOT EXISTS course_categories (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    slug           VARCHAR(100) NOT NULL UNIQUE,
    description    TEXT,
    icon           VARCHAR(50),
    display_order  INT NOT NULL DEFAULT 0,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_course_categories_slug ON course_categories(slug);

-- Courses
CREATE TABLE IF NOT EXISTS courses (
    id             BIGSERIAL PRIMARY KEY,
    category_id    BIGINT NOT NULL REFERENCES course_categories(id) ON DELETE RESTRICT,
    title          VARCHAR(200) NOT NULL,
    slug           VARCHAR(200) NOT NULL UNIQUE,
    description    TEXT,
    difficulty     VARCHAR(20) NOT NULL DEFAULT 'BEGINNER' CHECK (difficulty IN ('BEGINNER','INTERMEDIATE','ADVANCED')),
    estimated_hours INT,
    is_published   BOOLEAN NOT NULL DEFAULT FALSE,
    display_order  INT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_courses_slug        ON courses(slug);
CREATE INDEX IF NOT EXISTS idx_courses_category_id ON courses(category_id);
CREATE INDEX IF NOT EXISTS idx_courses_is_published ON courses(is_published);

-- Course modules
CREATE TABLE IF NOT EXISTS course_modules (
    id             BIGSERIAL PRIMARY KEY,
    course_id      BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title          VARCHAR(200) NOT NULL,
    description    TEXT,
    display_order  INT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_modules_course_id ON course_modules(course_id);

-- Topics within modules
CREATE TABLE IF NOT EXISTS topics (
    id                 BIGSERIAL PRIMARY KEY,
    module_id          BIGINT NOT NULL REFERENCES course_modules(id) ON DELETE CASCADE,
    title              VARCHAR(200) NOT NULL,
    slug               VARCHAR(200) NOT NULL,
    difficulty         VARCHAR(20) NOT NULL DEFAULT 'EASY' CHECK (difficulty IN ('EASY','MEDIUM','HARD')),
    estimated_minutes  INT NOT NULL DEFAULT 30,
    display_order      INT NOT NULL DEFAULT 0,
    is_published       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (module_id, slug)
);

CREATE INDEX IF NOT EXISTS idx_topics_module_id ON topics(module_id);
CREATE INDEX IF NOT EXISTS idx_topics_slug      ON topics(slug);

-- Topic content (rich learning material)
CREATE TABLE IF NOT EXISTS topic_contents (
    id                   BIGSERIAL PRIMARY KEY,
    topic_id             BIGINT NOT NULL UNIQUE REFERENCES topics(id) ON DELETE CASCADE,
    explanation          TEXT,
    simple_explanation   TEXT,
    real_world_example   TEXT,
    syntax_example       TEXT,
    code_example         TEXT,
    code_language        VARCHAR(30) NOT NULL DEFAULT 'java',
    interview_points     TEXT,
    common_mistakes      TEXT,
    practice_questions   TEXT,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_topic_contents_topic_id ON topic_contents(topic_id);

-- Seed categories
INSERT INTO course_categories (name, slug, description, icon, display_order) VALUES
('Core Java',           'core-java',           'Foundation of Java programming',          'Code2',      1),
('Advanced Java',       'advanced-java',        'Servlets, JDBC, JSP, Maven',              'Layers',     2),
('SQL & Databases',     'sql-databases',        'SQL, MySQL, PostgreSQL fundamentals',     'Database',   3),
('HTML & CSS',          'html-css',             'Web structure and styling',               'Globe',      4),
('JavaScript',          'javascript',           'Dynamic web programming',                 'Zap',        5),
('React',               'react',                'Modern frontend framework',               'Atom',       6),
('Spring Framework',    'spring',               'Spring Core, MVC, Boot, Security',        'Leaf',       7),
('Spring Boot',         'spring-boot',          'Production-ready Spring applications',    'Rocket',     8),
('JPA & Hibernate',     'jpa-hibernate',        'Object-Relational Mapping in Java',       'GitBranch',  9),
('REST APIs',           'rest-apis',            'RESTful web services with Spring',        'Globe2',     10),
('Microservices',       'microservices',        'Microservices architecture and patterns', 'Network',    11),
('Data Structures & Algorithms', 'dsa',         'DSA for coding interviews',              'BrainCircuit',12),
('Git & DevOps',        'git-devops',           'Git, Docker, Kubernetes, CI/CD, AWS',     'GitMerge',   13),
('Interview Preparation','interview-prep',      'Complete interview readiness',            'Mic',        14)
ON CONFLICT (slug) DO NOTHING;

-- Seed Core Java course
INSERT INTO courses (category_id, title, slug, description, difficulty, estimated_hours, is_published, display_order)
SELECT id, 'Core Java Programming', 'core-java-programming',
       'Complete Core Java from basics to advanced — OOP, Collections, Streams, Multithreading and more.',
       'BEGINNER', 80, TRUE, 1
FROM course_categories WHERE slug = 'core-java'
ON CONFLICT (slug) DO NOTHING;

-- Core Java Modules
INSERT INTO course_modules (course_id, title, display_order)
SELECT c.id, m.title, m.ord FROM courses c
CROSS JOIN (VALUES
    ('Java Fundamentals',         1),
    ('Object-Oriented Programming',2),
    ('Exception Handling',        3),
    ('Collections Framework',     4),
    ('Java 8 Features',           5),
    ('Multithreading & Concurrency',6),
    ('JVM Internals',             7)
) AS m(title, ord)
WHERE c.slug = 'core-java-programming'
ON CONFLICT DO NOTHING;
