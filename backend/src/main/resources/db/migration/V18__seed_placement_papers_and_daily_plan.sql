-- =============================================================================
-- V18: Seed Placement Papers & Questions Reference
-- =============================================================================

-- 1. Seed Placement Papers (Company 1 = TCS, Company 2 = Infosys)
INSERT INTO placement_papers (
    id, company_id, title, slug, paper_year, target_role, round_name,
    duration_minutes, total_marks, passing_marks, difficulty, paper_source,
    instructions, is_active
)
VALUES
(
    1,
    1,
    'TCS NQT National Placement Paper 2024 (Cognitive + Technical)',
    'tcs-nqt-2024-placement-paper',
    '2024',
    'Graduate Software Engineer / Ninja & Digital',
    'Round 1: Online Assessment',
    60,
    100,
    60,
    'MEDIUM',
    'VERIFIED',
    'Comprehensive assessment covering Quantitative Aptitude, Logical Reasoning, Core Java, SQL queries, and Data Structures.',
    true
),
(
    2,
    2,
    'Infosys Specialist Programmer (DSE/SP) Technical Paper 2024',
    'infosys-dse-sp-2024-paper',
    '2024',
    'Digital Specialist Engineer (DSE)',
    'Technical Online Coding & MCQ Assessment',
    60,
    100,
    65,
    'HARD',
    'REPORTED',
    'Advanced technical paper focusing on Algorithms, Java Memory Model, Spring Boot Microservices, and SQL Optimization.',
    true
)
ON CONFLICT (id) DO NOTHING;

-- 2. Seed Placement Paper Questions for Paper 1 (TCS NQT)
INSERT INTO placement_paper_questions (
    id, paper_id, section_name, question_text,
    option_a, option_b, option_c, option_d,
    correct_option, explanation, marks, display_order
)
VALUES
(
    1,
    1,
    'JAVA',
    'What will be the output when executing: List<Integer> list = List.of(1, 2, 3); list.add(4); in Java 17?',
    'List containing [1, 2, 3, 4]',
    'UnsupportedOperationException at runtime',
    'Compilation error',
    'NullPointerException',
    'B',
    'List.of() produces an immutable list implementation. Mutating operations like add() or remove() throw UnsupportedOperationException.',
    2,
    1
),
(
    2,
    1,
    'SQL',
    'Which SQL clause is used to filter aggregated group records after a GROUP BY statement?',
    'WHERE',
    'ORDER BY',
    'HAVING',
    'FILTER',
    'C',
    'The HAVING clause filters aggregated results, whereas WHERE filters individual rows prior to grouping.',
    2,
    2
),
(
    3,
    1,
    'DSA',
    'What is the worst-case time complexity of inserting n elements into a Binary Search Tree (BST)?',
    'O(log n)',
    'O(n)',
    'O(n log n)',
    'O(n^2)',
    'D',
    'When elements are inserted in sorted order, the BST becomes a skewed degenerate linked list, leading to O(n^2) total time for n insertions.',
    2,
    3
),
(
    4,
    1,
    'APTITUDE',
    'A train running at 54 km/hr crosses a platform 150 meters long in 20 seconds. What is the length of the train?',
    '120 m',
    '150 m',
    '180 m',
    '200 m',
    'B',
    'Speed = 54 * (5/18) = 15 m/s. Total distance = 15 * 20 = 300 m. Train length = 300 - 150 = 150 m.',
    2,
    4
),
(
    5,
    1,
    'REASONING',
    'In a certain code, JAVA is written as KCXC. How is SPRING written in that code?',
    'TQULPJI',
    'TQVLQJI',
    'TQULQJI',
    'TQUMQJI',
    'C',
    'Each letter is shifted by +1, +2, +3, +4, +5, +6, +7 respectively: S(+1)=T, P(+2)=R, R(+3)=U, I(+4)=M... TQULQJI.',
    2,
    5
)
ON CONFLICT (id) DO NOTHING;

-- 3. Seed Placement Paper Questions for Paper 2 (Infosys DSE)
INSERT INTO placement_paper_questions (
    id, paper_id, section_name, question_text,
    option_a, option_b, option_c, option_d,
    correct_option, explanation, marks, display_order
)
VALUES
(
    6,
    2,
    'JAVA',
    'In Spring Boot, which annotation is used to declare a transactional boundary with automatic rollback on runtime exceptions?',
    '@Transaction',
    '@Transactional',
    '@EnableTransactionManagement',
    '@RollbackOn',
    'B',
    '@Transactional at class or method level marks the boundary and automatically rolls back on unchecked exceptions.',
    2,
    1
),
(
    7,
    2,
    'SQL',
    'Which index type is default for B-Tree indexed primary keys in PostgreSQL and handles equality and range queries efficiently?',
    'Hash Index',
    'B-Tree Index',
    'GIN Index',
    'BRIN Index',
    'B',
    'B-Tree is the default PostgreSQL index type suitable for comparison operators (<, <=, =, >=, >) and BETWEEN.',
    2,
    2
)
ON CONFLICT (id) DO NOTHING;
