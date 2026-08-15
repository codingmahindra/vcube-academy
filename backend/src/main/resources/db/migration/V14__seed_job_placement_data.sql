-- V14: Seed Job Portal and Placement Preparation Data

-- 1. Seed Job Categories
INSERT INTO job_categories (id, name, slug, description, icon, is_active)
VALUES
(1, 'Java Backend Development', 'java-backend', 'Core backend engineering, RESTful microservices, and enterprise logic using Java and Spring Boot', 'Server', true),
(2, 'Full Stack Java Development', 'full-stack-java', 'End-to-end full stack development with Java Spring Boot backend and React/Angular frontend', 'Layers', true),
(3, 'Cloud & Microservices Engineering', 'cloud-microservices', 'Distributed systems, Docker containerization, Kubernetes, and Cloud native architecture', 'Cloud', true),
(4, 'Database & SQL Engineering', 'database-sql', 'Relational schema design, query optimization, indexing, and high-throughput data processing', 'Database', true)
ON CONFLICT (id) DO NOTHING;

-- 2. Seed Job Skills
INSERT INTO job_skills (id, name, slug, category)
VALUES
(1, 'Java', 'java', 'LANGUAGE'),
(2, 'Spring Boot', 'spring-boot', 'FRAMEWORK'),
(3, 'Microservices', 'microservices', 'ARCHITECTURE'),
(4, 'Hibernate & JPA', 'hibernate-jpa', 'ORM'),
(5, 'REST APIs', 'rest-apis', 'BACKEND'),
(6, 'SQL & Relational DBs', 'sql-databases', 'DATABASE'),
(7, 'PostgreSQL', 'postgresql', 'DATABASE'),
(8, 'MySQL', 'mysql', 'DATABASE'),
(9, 'Data Structures & Algorithms', 'dsa', 'CORE'),
(10, 'React', 'react', 'FRONTEND'),
(11, 'Docker', 'docker', 'DEVOPS'),
(12, 'Kafka', 'kafka', 'MESSAGING'),
(13, 'Redis', 'redis', 'CACHE'),
(14, 'Git & Version Control', 'git', 'TOOLS'),
(15, 'AWS Cloud', 'aws', 'CLOUD')
ON CONFLICT (id) DO NOTHING;

-- 3. Seed Jobs
-- (Company IDs 1..6 correspond to TCS, Infosys, Amazon, JPMorgan Chase, Accenture, Cognizant from V12)
INSERT INTO jobs (
    id, company_id, category_id, title, slug, description, location,
    employment_type, experience_level, work_mode, salary_min, salary_max, salary_currency, salary_text,
    source, source_url, qualification, responsibilities, selection_process,
    posted_date, application_deadline, is_active
)
VALUES
(
    1, 1, 1,
    'Graduate Java Developer', 'tcs-graduate-java-developer',
    'TCS is seeking passionate Java developers for the Digital Enterprise division. You will build highly scalable enterprise microservices using Java 17+, Spring Boot, and PostgreSQL.',
    'Hyderabad, India', 'FULL_TIME', 'FRESHER', 'HYBRID',
    400000.00, 700000.00, 'INR', '4.0 - 7.0 LPA',
    'COMPANY_CAREER_PAGE', 'https://ibegin.tcs.com/iBegin/jobs',
    'B.Tech / B.E / MCA in Computer Science or related engineering stream with minimum 60% aggregate.',
    '1. Design and develop clean, maintainable Java code adhering to SOLID principles.\n2. Develop REST APIs and integrate with relational databases.\n3. Write unit and integration tests using JUnit and Mockito.\n4. Participate in agile sprint ceremonies.',
    'Round 1: Online Aptitude & Coding Test\nRound 2: Technical Interview (Core Java, OOP, SQL)\nRound 3: Managerial & HR Interview',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true
),
(
    2, 2, 2,
    'Systems Engineer - Java Full Stack', 'infosys-systems-engineer-java',
    'Infosys is hiring Java Full Stack Engineers to build client platforms across banking, healthcare, and retail sectors.',
    'Bangalore, India', 'FULL_TIME', 'EXP_0_TO_1', 'HYBRID',
    450000.00, 800000.00, 'INR', '4.5 - 8.0 LPA',
    'COMPANY_CAREER_PAGE', 'https://career.infosys.com/joblist',
    'B.Tech / B.E / MCA / M.Sc Computer Science with solid programming foundation.',
    '1. Build dynamic frontend interfaces using React / JavaScript.\n2. Build secure backend APIs using Spring Boot and Spring Security.\n3. Work with Hibernate/JPA for database persistence.\n4. Resolve bugs and optimize application performance.',
    'Round 1: InfyTQ / Online Coding Round\nRound 2: Technical Assessment (Java, Spring, Data Structures)\nRound 3: HR Interview',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '25 days', true
),
(
    3, 3, 1,
    'Software Development Engineer I (Java/AWS)', 'amazon-sde-1-java',
    'Amazon is looking for high-caliber SDE-1 engineers to join AWS and Retail Core services. You will design distributed systems that process millions of transactions per second.',
    'Hyderabad, India', 'FULL_TIME', 'EXP_0_TO_1', 'HYBRID',
    1600000.00, 2400000.00, 'INR', '16.0 - 24.0 LPA',
    'LINKEDIN', 'https://amazon.jobs/en/jobs/sde1-hyd',
    'Bachelor''s degree in Computer Science or equivalent with strong DSA problem-solving.',
    '1. Write robust, concurrent, and high-performance Java code.\n2. Architect scalable microservices with Kafka, DynamoDB, and Redis.\n3. Participate in low-level and high-level design discussions.\n4. Monitor production reliability and latency SLAs.',
    'Round 1: Online Coding Assessment (2 LeetCode Medium/Hard DSA problems)\nRound 2: Technical Round 1 (Data Structures, Algorithms & Concurrency)\nRound 3: Technical Round 2 (Low-Level Design & Code Quality)\nRound 4: Bar Raiser & Amazon Leadership Principles',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true
),
(
    4, 4, 1,
    'Java Associate Software Engineer', 'jpmorgan-associate-java',
    'JPMorgan Chase & Co. Corporate & Investment Bank is hiring Java software engineers for high-frequency trading platforms and ledger settlements.',
    'Mumbai, India', 'FULL_TIME', 'FRESHER', 'HYBRID',
    1200000.00, 1800000.00, 'INR', '12.0 - 18.0 LPA',
    'COMPANY_CAREER_PAGE', 'https://careers.jpmorgan.com/global/en/home',
    'B.Tech / B.E / M.Tech in CS/IT/ECE with strong foundation in multi-threading and SQL transactions.',
    '1. Develop low-latency Java banking applications.\n2. Write complex SQL queries, stored procedures, and optimize indexing.\n3. Integrate with Spring Cloud microservices and Spring Security.\n4. Ensure compliance with financial security standards.',
    'Round 1: HackerRank Coding Assessment\nRound 2: Technical Interview (Core Java, Multithreading, SQL ACID properties)\nRound 3: Engineering Architecture & Behavioral Interview',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '20 days', true
),
(
    5, 5, 2,
    'Associate Software Engineer - Java & Cloud', 'accenture-ase-java',
    'Accenture Technology is hiring engineers to deliver enterprise digital transformation solutions utilizing Java, Spring Boot, and Cloud platforms.',
    'Pune, India', 'FULL_TIME', 'FRESHER', 'ONSITE',
    450000.00, 650000.00, 'INR', '4.5 - 6.5 LPA',
    'NAUKRI', 'https://www.naukri.com/accenture-java-jobs',
    'BE / B.Tech all branches, MCA, M.Sc (CS/IT) with no active backlogs.',
    '1. Assist in designing modular application components.\n2. Implement CRUD REST APIs with Spring Boot.\n3. Execute automated test suites and participate in code reviews.',
    'Round 1: Cognitive and Technical Assessment\nRound 2: Coding Assessment (Java fundamentals)\nRound 3: Communication Assessment\nRound 4: Virtual Interview',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '15 days', true
),
(
    6, 6, 1,
    'Programmer Analyst Trainee (Java)', 'cognizant-pat-java',
    'Cognizant is inviting entry-level engineers to join the Java and Web Engineering practice for global healthcare and logistics clients.',
    'Chennai, India', 'FULL_TIME', 'FRESHER', 'HYBRID',
    400000.00, 550000.00, 'INR', '4.0 - 5.5 LPA',
    'FOUNDIT', 'https://www.foundit.in/cognizant-java-careers',
    'Graduates in Engineering or Computer Applications with minimum 60% throughout academics.',
    '1. Build business logic modules using Java 17 and Spring MVC.\n2. Write SQL DDL/DML and troubleshoot query bottlenecks.\n3. Deploy applications onto Docker containers.',
    'Round 1: Online Aptitude & Technical MCQ Test\nRound 2: Technical Interview (Core Java & DBMS)\nRound 3: HR Discussion',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '18 days', true
)
ON CONFLICT (id) DO NOTHING;

-- 4. Seed Job Skill Mappings
-- Job 1 (TCS): Java, Spring Boot, PostgreSQL, REST APIs, Git
INSERT INTO job_skill_mappings (job_id, skill_id, is_required)
VALUES
(1, 1, true), (1, 2, true), (1, 5, true), (1, 7, true), (1, 14, false),
-- Job 2 (Infosys): Java, Spring Boot, React, Hibernate & JPA, SQL
(2, 1, true), (2, 2, true), (2, 4, true), (2, 6, true), (2, 10, true),
-- Job 3 (Amazon): Java, DSA, Microservices, Redis, Kafka, AWS
(3, 1, true), (3, 9, true), (3, 3, true), (3, 12, false), (3, 13, false), (3, 15, false),
-- Job 4 (JPMorgan): Java, SQL, Spring Boot, Microservices, DSA
(4, 1, true), (4, 2, true), (4, 6, true), (4, 3, true), (4, 9, true),
-- Job 5 (Accenture): Java, Spring Boot, REST APIs, SQL
(5, 1, true), (5, 2, true), (5, 5, true), (5, 6, false),
-- Job 6 (Cognizant): Java, SQL, Spring Boot, Docker
(6, 1, true), (6, 2, true), (6, 6, true), (6, 11, false)
ON CONFLICT (job_id, skill_id) DO NOTHING;

-- 5. Seed Placement Drives
INSERT INTO placement_drives (
    id, company_id, title, description, location,
    drive_date, registration_deadline, package_details,
    eligibility_criteria, selection_process, application_link, status
)
VALUES
(
    1, 1,
    'TCS National Qualifier Test (NQT) - Off-Campus Drive 2026',
    'Exclusive placement drive for VCUBE Academy students. Multiple hiring tracks across TCS Ninja (3.6 LPA), Digital (7.2 LPA), and Prime (9.0 LPA).',
    'Online / Pan-India',
    CURRENT_TIMESTAMP + INTERVAL '14 days', CURRENT_TIMESTAMP + INTERVAL '7 days',
    '3.6 LPA - 9.0 LPA',
    'Graduating 2025/2026 batch. BE/B.Tech/MCA with >= 60% aggregate. No active backlogs.',
    '1. TCS NQT Online Test\n2. Technical & Coding Round\n3. HR Interview',
    'https://www.tcs.com/careers/nqt',
    'UPCOMING'
),
(
    2, 4,
    'JPMorgan Chase Code For Good Hackathon & Campus Drive',
    'Join JPMorgan Chase engineers for a 24-hour social good hackathon leading directly to full-time Software Engineer offers.',
    'Hyderabad / Mumbai',
    CURRENT_TIMESTAMP + INTERVAL '21 days', CURRENT_TIMESTAMP + INTERVAL '10 days',
    '14.0 LPA - 18.0 LPA',
    'B.Tech CS/IT with strong algorithmic coding and Java/Full Stack foundation.',
    '1. Pre-Hackathon Coding Challenge\n2. Hackathon Prototype Evaluation\n3. Final Executive Interview',
    'https://careers.jpmorgan.com/code-for-good',
    'UPCOMING'
),
(
    3, 2,
    'Infosys Special Edition - Power Programmer Drive',
    'High-end competitive programming drive for full stack Java developers at Infosys.',
    'Bangalore, India',
    CURRENT_TIMESTAMP + INTERVAL '28 days', CURRENT_TIMESTAMP + INTERVAL '12 days',
    '8.0 LPA - 9.5 LPA',
    'Minimum 65% aggregate with strong proficiency in Data Structures & Java 8+ features.',
    '1. Advanced Coding Assessment\n2. Technical Architecture Round',
    'https://career.infosys.com/powerprogrammer',
    'UPCOMING'
)
ON CONFLICT (id) DO NOTHING;
