-- =============================================================================
-- V16: Seed Resume Seed Data & Templates Reference
-- =============================================================================

-- Seed a sample resume profile for standard demonstration if demo student exists
DO $$
DECLARE
    v_user_id BIGINT;
    v_profile_id BIGINT;
    v_version_id BIGINT;
    v_job_id BIGINT;
BEGIN
    SELECT id INTO v_user_id FROM users WHERE email = 'student@vcube.com' LIMIT 1;

    IF v_user_id IS NOT NULL THEN
        -- Create default student resume profile
        INSERT INTO resume_profiles (user_id, full_name, email, phone, location, linkedin_url, github_url, professional_summary)
        VALUES (
            v_user_id,
            'VCUBE Student Candidate',
            'student@vcube.com',
            '+91 98765 43210',
            'Hyderabad, India',
            'https://linkedin.com/in/vcube-student',
            'https://github.com/vcube-student',
            'Passionate Java Full Stack Developer skilled in Java 17, Spring Boot, Microservices, PostgreSQL, and React. Strong problem solver with hands-on enterprise project experience.'
        ) RETURNING id INTO v_profile_id;

        SELECT id INTO v_job_id FROM jobs WHERE slug = 'tcs-graduate-java-developer' LIMIT 1;

        -- Create a base version
        INSERT INTO resume_versions (profile_id, job_id, version_title, target_role, target_company, template, latest_ats_score, is_primary)
        VALUES (
            v_profile_id,
            v_job_id,
            'Java Developer — TCS General',
            'Graduate Java Developer',
            'Tata Consultancy Services',
            'JAVA_FULLSTACK',
            78,
            TRUE
        ) RETURNING id INTO v_version_id;

        -- Seed Education
        INSERT INTO resume_educations (version_id, institution, degree, field_of_study, start_year, end_year, score_or_cgpa, display_order)
        VALUES (
            v_version_id,
            'JNTU Hyderabad',
            'Bachelor of Technology (B.Tech)',
            'Computer Science & Engineering',
            '2020',
            '2024',
            '8.4 CGPA',
            1
        );

        -- Seed Experience / Internship
        INSERT INTO resume_experiences (version_id, company_name, role_title, location, start_date, end_date, is_current, description, bullet_points, display_order)
        VALUES (
            v_version_id,
            'VCUBE Software Solutions',
            'Java Full Stack Intern',
            'Hyderabad, India',
            'Jan 2024',
            'Jun 2024',
            FALSE,
            'Developed robust backend REST services with Spring Boot and PostgreSQL.',
            '["Architected 15+ RESTful endpoints using Spring Boot, JPA, and PostgreSQL with sub-100ms response times","Implemented JWT stateless authentication and role-based security","Engineered unit and integration test suites using JUnit 5 and Mockito achieving 85%+ coverage"]',
            1
        );

        -- Seed Project
        INSERT INTO resume_projects (version_id, title, tech_stack, live_url, github_url, description, bullet_points, display_order)
        VALUES (
            v_version_id,
            'E-Commerce Microservices Engine',
            'Java 17, Spring Boot, Spring Cloud, PostgreSQL, Docker',
            'https://demo.vcube-ecommerce.com',
            'https://github.com/vcube-student/ecommerce-microservices',
            'Scalable distributed e-commerce architecture with service discovery, API gateway, and transactional outbox pattern.',
            '["Decoupled order and inventory services using Spring Cloud Config and Eureka Registry","Optimized database queries and added indexing, reducing checkout latency by 40%","Containerized multi-service ecosystem using Docker Compose for seamless local and CI deployment"]',
            1
        );

        -- Seed Certification
        INSERT INTO resume_certifications (version_id, name, issuing_organization, issue_date, credential_url, display_order)
        VALUES (
            v_version_id,
            'Oracle Certified Professional: Java SE 17 Developer',
            'Oracle Corporation',
            '2024',
            'https://catalog-education.oracle.com/ords/certview/sharebadge?id=12345',
            1
        );
    END IF;
END $$;
