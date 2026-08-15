-- V12: Seed Interview Preparation Categories, Topics, Companies, and Comprehensive Questions

-- 1. Insert Categories
INSERT INTO interview_categories (name, slug, description, icon, display_order, is_active) VALUES
('Core Java & OOP', 'core-java', 'Object-Oriented Programming, Collections, Multithreading, JVM & Memory Management', 'Coffee', 1, true),
('Java 8+ & Modern Java', 'java-8-plus', 'Lambdas, Stream API, Optional, Functional Interfaces, Records & Sealed Classes', 'Sparkles', 2, true),
('SQL & Relational Databases', 'sql-databases', 'Queries, Joins, Subqueries, Indexing, Transactions, ACID & Normalization', 'Database', 3, true),
('Spring & Spring Boot', 'spring-boot', 'IoC, DI, Spring MVC, REST APIs, Auto-configuration, Actuator, Profiles', 'Layers', 4, true),
('Hibernate & JPA', 'hibernate-jpa', 'Entity lifecycle, Relationships, Fetch types, JPQL, Caching, N+1 Problem', 'FileCode2', 5, true),
('Microservices & Architecture', 'microservices', 'Service Discovery, API Gateway, OpenFeign, Resilience4j, Distributed Tracing', 'Server', 6, true),
('Behavioral & HR', 'behavioral-hr', 'STAR Method, Leadership, Conflict Resolution, Project Scenarios & HR Q&A', 'Users', 7, true);

-- 2. Insert Topics
INSERT INTO interview_topics (category_id, name, slug, description, display_order, is_active) VALUES
-- Core Java (Cat 1)
(1, 'OOP Fundamentals', 'oop-fundamentals', 'Polymorphism, Inheritance, Encapsulation, Abstraction, Interface vs Abstract Class', 1, true),
(1, 'Java Collections Framework', 'collections-framework', 'List, Set, Map, HashMap internal working, ConcurrentHashMap, TreeSet', 2, true),
(1, 'Multithreading & Concurrency', 'multithreading-concurrency', 'Thread lifecycle, synchronization, volatile, ThreadPoolExecutor, locks', 3, true),
(1, 'JVM Architecture & Memory', 'jvm-memory', 'Heap vs Stack, Metaspace, Garbage Collection algorithms, ClassLoader', 4, true),
(1, 'Exception Handling & String Pool', 'exception-handling-strings', 'Checked vs Unchecked, try-with-resources, String Immutability, StringBuffer/Builder', 5, true),

-- Java 8+ (Cat 2)
(2, 'Stream API & Collectors', 'stream-api', 'Intermediate vs Terminal operations, parallel streams, groupingBy, reduction', 1, true),
(2, 'Lambda Expressions & Functional Interfaces', 'lambda-functional-interfaces', 'Predicate, Function, Consumer, Supplier, Method References, Custom @FunctionalInterface', 2, true),

-- SQL (Cat 3)
(3, 'SQL Joins & Complex Queries', 'sql-joins-queries', 'INNER/LEFT/RIGHT/FULL JOIN, Self Join, Subqueries, Correlated Subqueries, CTEs', 1, true),
(3, 'Indexing & Transactions', 'indexing-transactions', 'B-Tree vs Hash index, clustered index, ACID properties, Isolation levels', 2, true),

-- Spring Boot (Cat 4)
(4, 'IoC Container & Dependency Injection', 'ioc-dependency-injection', 'Bean scopes, Lifecycle callbacks, @Autowired, @Qualifier, Constructor vs Setter Injection', 1, true),
(4, 'Spring Boot Internals & REST', 'spring-boot-rest', '@SpringBootApplication, AutoConfiguration, Embedded Tomcat, ExceptionHandler, Actuator', 2, true),

-- Hibernate/JPA (Cat 5)
(5, 'JPA Relationships & Performance', 'jpa-relationships-performance', 'OneToMany, ManyToMany, Lazy vs Eager loading, N+1 Query Problem, First/Second level Cache', 1, true),

-- Microservices (Cat 6)
(6, 'Microservices Patterns', 'microservices-patterns', 'API Gateway, Service Discovery, Circuit Breaker, Saga pattern, Distributed Tracing', 1, true),

-- Behavioral (Cat 7)
(7, 'STAR Behavioral Questions', 'star-behavioral', 'Situation, Task, Action, Result methodology for teamwork, pressure, conflict resolution', 1, true);

-- 3. Insert Top Companies
INSERT INTO companies (name, slug, logo_url, description, industry, tier, hiring_rounds_info, is_active) VALUES
('Tata Consultancy Services (TCS)', 'tcs', 'https://images.unsplash.com/photo-1542744173-8e7e53415bb0', 'Global IT services leader hiring Java Full Stack Developers for Digital & Prime roles.', 'IT Services', 'TIER_1', 'Round 1: Online Aptitude + Coding, Round 2: Technical Interview (Java, SQL, Spring Boot), Round 3: Managerial & HR', true),
('Infosys', 'infosys', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab', 'Specialist Programmer (SP) and Digital Specialist Engineer (DSE) Java Full Stack hiring.', 'IT Services', 'TIER_1', 'Round 1: Coding Challenge (DSA), Round 2: Technical Interview (Full Stack & System Design), Round 3: HR Round', true),
('Amazon', 'amazon', 'https://images.unsplash.com/photo-1523474255658-4af61b1614ff', 'Tier 1 Product company assessing DSA, Low-Level Design, Microservices, and Leadership Principles.', 'Product / E-Commerce / Cloud', 'TIER_1', 'Round 1: Online Assessment (Coding + Work Simulation), Round 2-4: Technical Rounds (DSA + LLD + System Design), Round 5: Bar Raiser (Behavioral)', true),
('JPMorgan Chase', 'jpmorgan-chase', 'https://images.unsplash.com/photo-1565372195458-9de0b320ef04', 'Global Investment Bank evaluating Core Java, Multithreading, Spring Boot, REST APIs, and High-throughput SQL.', 'Financial Services / Banking', 'TIER_1', 'Round 1: HackerRank Coding Challenge, Round 2: Core Java & Concurrency Deepdive, Round 3: Spring Boot & Microservices, Round 4: Behavioral / Culture Fit', true),
('Accenture', 'accenture', 'https://images.unsplash.com/photo-1497215728101-856f4ea42174', 'Advanced Technology Centers hiring Full Stack Java Engineers for Enterprise Cloud platforms.', 'Consulting / IT Services', 'TIER_1', 'Round 1: Cognitive + Technical Assessment, Round 2: Coding Assessment, Round 3: Technical & HR Interview', true),
('Cognizant', 'cognizant', 'https://images.unsplash.com/photo-1577495508048-b635879837f1', 'GenC Elevate & Next hiring for Cloud-native Java microservices professionals.', 'IT Services', 'TIER_1', 'Round 1: Skill Assessment, Round 2: Hands-on Technical Interview, Round 3: HR Discussion', true);

-- 4. Insert Comprehensive Interview Questions
-- Question 1: HashMap Internal Working (Core Java)
INSERT INTO interview_questions (
    topic_id, question_text, question_type, difficulty, interview_round, question_source, source_reference,
    expected_answer, explanation, interview_points, common_mistakes, follow_up_questions, real_world_example, evaluation_keywords, is_published
) VALUES (
    (SELECT id FROM interview_topics WHERE slug = 'collections-framework'),
    'How does HashMap work internally in Java? Explain hashing, collisions, and Java 8 treeification.',
    'CONCEPTUAL', 'INTERMEDIATE', 'ROUND_3_TECHNICAL', 'REPORTED_PLACEMENT_QUESTION', 'Reported in TCS Digital, Infosys, Amazon & JPMC technical interviews',
    'HashMap is based on hashing and uses an array of Node<K,V> buckets. When put(key, value) is called, it computes hash(key.hashCode()) and index = (n - 1) & hash. If collision occurs (two keys map to same bucket), it appends to a LinkedList. In Java 8, when a bucket exceeds TREEIFY_THRESHOLD (8 nodes) and array capacity >= 64, the linked list transforms into a Red-Black Tree (TreeNode), reducing worst-case search complexity from O(N) to O(log N).',
    'HashMap stores key-value pairs using array of Entry/Node objects. Key steps:\n1. Hashing: hash() function spreads bits to minimize collisions.\n2. Bucket Index: index = (n - 1) & hash where n is array capacity (power of 2).\n3. Collision Handling: Separate Chaining via LinkedList.\n4. Java 8 Optimization: Treeification to Red-Black tree when chain length >= 8 and capacity >= 64.\n5. get(key): calculates index, traverses bucket comparing hash and key.equals(k).',
    '["Array of Node<K,V> buckets", "hash() and index calculation via bitwise AND", "Separate Chaining for collisions", "Java 8 Red-Black Tree transformation when chain length >= 8", "equals() and hashCode() contract importance"]',
    '["Confusing hashCode() return value with the actual bucket index", "Forgetting that capacity must be power of 2 for (n-1)&hash to work", "Not mentioning Java 8 Treeification threshold (8) and min capacity (64)"]',
    '["What happens if two unequal objects return the same hashCode()?", "Why is String a popular choice for HashMap key?", "How does ConcurrentHashMap achieve thread safety in Java 8 compared to Java 7 segments?"]',
    'Caching user sessions in memory keyed by UUID tokens where fast O(1) average lookup is required.',
    '["hashcode", "equals", "bucket", "collision", "separate chaining", "linkedlist", "red-black tree", "treeify", "threshold 8", "o(log n)", "o(1)"]',
    true
);

-- Question 2: Garbage Collection & Memory Leaks (Core Java)
INSERT INTO interview_questions (
    topic_id, question_text, question_type, difficulty, interview_round, question_source, source_reference,
    expected_answer, explanation, interview_points, common_mistakes, follow_up_questions, real_world_example, evaluation_keywords, is_published
) VALUES (
    (SELECT id FROM interview_topics WHERE slug = 'jvm-memory'),
    'What causes Memory Leaks in Java even though the JVM has automatic Garbage Collection? How do you prevent them?',
    'SCENARIO_BASED', 'ADVANCED', 'ROUND_3_TECHNICAL', 'VERIFIED_COMPANY_QUESTION', 'Standard technical question at Amazon, JPMC and Morgan Stanley',
    'In Java, a memory leak occurs when objects that are no longer needed by the application remain reachable through strong references from active GC Roots (e.g., static fields, unclosed resources, uncleared ThreadLocals, un-deregistered event listeners, or improper equals/hashCode in collections). Since GC only collects unreachable objects, these referenced unused objects accumulate, eventually causing java.lang.OutOfMemoryError: Java heap space.',
    'Common causes of Java memory leaks:\n1. Static references: Static collections holding onto objects for JVM lifetime.\n2. Uncleared ThreadLocal: Worker threads in thread pools retain ThreadLocal values indefinitely.\n3. Unclosed I/O & DB Connections: Leaks OS file descriptors and native buffers.\n4. Improper HashMap Keys: Mutating key hashCode leads to orphaned entries.\n5. Inner class references: Non-static inner classes holding implicit references to outer instances.',
    '["GC only reclaims unreachable objects, not unneeded referenced objects", "Static collections holding references indefinitely", "ThreadLocal leaks in thread pools", "Improper equals/hashCode causing orphaned map entries", "Use WeakReference, try-with-resources, and heap dump profiling (VisualVM/Eclipse MAT)"]',
    '["Claiming Java is 100% immune to memory leaks because of Garbage Collection", "Confusing StackOverflowError with OutOfMemoryError"]',
    '["What tools do you use to analyze heap dumps?", "How do WeakReference and SoftReference differ in Java?", "Why is ThreadLocal.remove() mandatory in Tomcat/Spring web request threads?"]',
    'A custom in-memory cache using a static HashMap without TTL eviction that eventually crashes a high-volume payment processing microservice after 3 days of uptime.',
    '["gc roots", "unreachable", "strong reference", "static", "threadlocal", "outofmemoryerror", "leak", "try-with-resources", "heap dump", "weakreference"]',
    true
);

-- Question 3: Java 8 Stream vs Parallel Stream (Java 8+)
INSERT INTO interview_questions (
    topic_id, question_text, question_type, difficulty, interview_round, question_source, source_reference,
    expected_answer, explanation, interview_points, common_mistakes, follow_up_questions, real_world_example, evaluation_keywords, is_published
) VALUES (
    (SELECT id FROM interview_topics WHERE slug = 'stream-api'),
    'What is the difference between map() and flatMap() in Java 8 Stream API? Give code scenarios for each.',
    'CONCEPTUAL', 'INTERMEDIATE', 'ROUND_2_CODING_TECHNICAL', 'REPORTED_PLACEMENT_QUESTION', 'Infosys SP, TCS Digital & Cognizant Elevate technical round',
    'map() is an intermediate operation that performs 1-to-1 transformation: it takes a Function<T, R> and transforms each element into another element, returning Stream<R>. flatMap() performs 1-to-N transformation and flattening: it takes a Function<T, Stream<R>>, transforms each element into a stream, and then flattens all nested streams into a single combined Stream<R>.',
    'Code Scenario:\n- map(): List<User> -> Stream of Names: users.stream().map(User::getName).collect(toList()); (Result: Stream<String>)\n- flatMap(): List<User> where User has List<Order> -> Stream of all Orders across users: users.stream().flatMap(u -> u.getOrders().stream()).collect(toList()); (Result: Stream<Order>, not Stream<List<Order>>).',
    '["map: 1-to-1 transformation returning Stream<R>", "flatMap: 1-to-many transformation and stream flattening", "flatMap eliminates nested Stream<Stream<T>> into Stream<T>", "Common use case: transforming List of Lists or accessing nested collections"]',
    '["Assuming flatMap modifies the underlying collection in-place", "Thinking flatMap is only for String splitting"]',
    '["How does Stream.reduce() differ from Stream.collect()?", "What is the difference between intermediate and terminal operations in Streams?"]',
    'Transforming an e-commerce Customer object with multiple Orders and OrderItems into a flat list of Item IDs for inventory checking.',
    '["1-to-1", "1-to-many", "flattening", "stream of streams", "transformation", "intermediate operation", "stream<r>", "nested"]',
    true
);

-- Question 4: SQL Indexing & Optimization (SQL)
INSERT INTO interview_questions (
    topic_id, question_text, question_type, difficulty, interview_round, question_source, source_reference,
    expected_answer, explanation, interview_points, common_mistakes, follow_up_questions, real_world_example, evaluation_keywords, is_published
) VALUES (
    (SELECT id FROM interview_topics WHERE slug = 'indexing-transactions'),
    'What is an Index in SQL? Explain the difference between Clustered and Non-Clustered Index, and when an Index can hurt performance.',
    'CONCEPTUAL', 'INTERMEDIATE', 'ROUND_3_TECHNICAL', 'VERIFIED_COMPANY_QUESTION', 'JPMorgan, Amazon, TCS Digital database rounds',
    'An index is a database data structure (typically B-Tree) that speeds up data retrieval on a table at the cost of additional storage and write overhead. A Clustered Index determines the physical storage order of rows in the table; hence only ONE clustered index can exist per table (usually Primary Key). A Non-Clustered Index contains sorted index key columns along with pointers (row locators) to the physical data rows, allowing multiple non-clustered indexes per table. Indexes hurt performance during INSERT, UPDATE, and DELETE operations because the B-Tree must be rebalanced on every write.',
    'Key Trade-offs:\n1. Read Performance: Drastically faster WHERE, JOIN, and ORDER BY queries.\n2. Write Performance: Every DML operation (INSERT/UPDATE/DELETE) must update both the table and all associated indexes.\n3. Storage: Indexes consume significant disk space and RAM buffer pool memory.\n4. Cardinality: Indexes on low-cardinality columns (e.g., boolean gender) are ineffective and ignored by query planners.',
    '["B-Tree structure for logarithmic search O(log N)", "Clustered index defines physical data ordering (only 1 per table)", "Non-clustered index stores key values with row pointers (multiple per table)", "Write overhead on INSERT/UPDATE/DELETE due to index maintenance", "Low cardinality columns should not be indexed"]',
    '["Thinking a table can have multiple clustered indexes", "Believing indexes should be created on every single column without evaluating write overhead"]',
    '["What is a Composite Index and what is the Leftmost Prefix Rule?", "How does EXPLAIN / EXPLAIN ANALYZE help diagnose query plans?"]',
    'Indexing user_id and email in an authentication table with 10 million rows to achieve sub-millisecond login query times.',
    '["b-tree", "clustered index", "non-clustered index", "physical order", "row pointer", "primary key", "write overhead", "insert update delete", "cardinality"]',
    true
);

-- Question 5: Spring Boot Autowired vs Constructor Injection (Spring Boot)
INSERT INTO interview_questions (
    topic_id, question_text, question_type, difficulty, interview_round, question_source, source_reference,
    expected_answer, explanation, interview_points, common_mistakes, follow_up_questions, real_world_example, evaluation_keywords, is_published
) VALUES (
    (SELECT id FROM interview_topics WHERE slug = 'ioc-dependency-injection'),
    'Why is Constructor Injection preferred over Field Injection (@Autowired on private fields) in Spring Boot?',
    'CONCEPTUAL', 'INTERMEDIATE', 'ROUND_3_TECHNICAL', 'REPORTED_PLACEMENT_QUESTION', 'Asked frequently in Spring Boot developer interviews at Accenture, Cognizant, and Capgemini',
    'Constructor Injection is preferred because:\n1. Immutability: Dependencies can be declared as final, ensuring they cannot be modified after bean creation.\n2. Testability: The class can be easily unit-tested with plain JUnit without launching a Spring Context or using reflection/Mockito injectMocks.\n3. Null Safety & Fail-Fast: Prevents NullPointerExceptions by guaranteeing all required dependencies are provided at instantiation time.\n4. Detects Circular Dependencies: Circular dependencies fail at startup rather than runtime.\n5. Adheres to SOLID Single Responsibility Principle: A constructor with too many parameters signals code smell.',
    'Field Injection using @Autowired on private fields uses Java Reflection to set dependencies, bypassing encapsulation and making mock testing difficult. Constructor injection allows Spring (and test frameworks) to instantiate immutable, complete objects naturally.',
    '["Enables final fields for immutability", "Facilitates pure JUnit unit testing without Spring context", "Guarantees fail-fast instantiation avoiding NullPointerException", "Highlights violation of Single Responsibility if constructor has too many params", "Recommended by Spring Framework official documentation"]',
    '["Thinking @Autowired on fields provides better performance", "Not knowing that in Spring 4.3+ a single constructor does not even require @Autowired annotation"]',
    '["What is @Qualifier and when is it necessary?", "How does Spring resolve circular dependencies with @Lazy?"]',
    'Writing unit tests for OrderService with Mockito where dependencies (PaymentRepository, EmailClient) are injected cleanly via new OrderService(mockRepo, mockEmail).',
    '["constructor injection", "field injection", "immutability", "final", "testability", "unit test", "nullpointerexception", "fail-fast", "single responsibility"]',
    true
);

-- Question 6: JPA N+1 Problem (Hibernate/JPA)
INSERT INTO interview_questions (
    topic_id, question_text, question_type, difficulty, interview_round, question_source, source_reference,
    expected_answer, explanation, interview_points, common_mistakes, follow_up_questions, real_world_example, evaluation_keywords, is_published
) VALUES (
    (SELECT id FROM interview_topics WHERE slug = 'jpa-relationships-performance'),
    'What is the N+1 SELECT Problem in Hibernate / Spring Data JPA, and how do you resolve it?',
    'SCENARIO_BASED', 'ADVANCED', 'ROUND_3_TECHNICAL', 'VERIFIED_COMPANY_QUESTION', 'Core backend interview question at Amazon, JPMC, and TCS Prime',
    'The N+1 Problem occurs when fetching N parent entities results in 1 initial SQL query for the parents, followed by N separate SQL queries to fetch the related child entities for each parent (especially in Lazy or default Eager collection mappings), causing 1 + N total queries and severe database latency.',
    'Solutions to resolve N+1 Problem:\n1. JOIN FETCH in JPQL: SELECT c FROM Course c JOIN FETCH c.modules;\n2. @EntityGraph: Declaring @EntityGraph(attributePaths = {"modules"}) on Spring Data JPA repository method.\n3. @BatchSize: Using @BatchSize(size = 25) to load child collections in batches using IN clause (reducing N queries to N/batch_size).\n4. DTO Projection: Selecting only required flat fields via custom JPQL constructor expression.',
    '["1 query for parent + N queries for children = N+1 database roundtrips", "Solutions: JOIN FETCH in JPQL", "Solutions: @EntityGraph on repository methods", "Solutions: @BatchSize on entity associations", "Default FetchType for @ManyToOne is EAGER, for @OneToMany is LAZY"]',
    '["Thinking changing FetchType.LAZY to FetchType.EAGER fixes N+1 (it actually can make it worse with findAll())", "Confusing JOIN with JOIN FETCH"]',
    '["What is the difference between JOIN and JOIN FETCH in JPQL?", "How does Hibernate 2nd Level Cache with Redis interact with JPA associations?"]',
    'Loading 100 User records on an admin dashboard that triggers 100 individual queries to fetch each user role list, causing page latency to spike from 20ms to 2.5 seconds.',
    '["n+1 problem", "join fetch", "entitygraph", "batchsize", "lazy loading", "eager loading", "jpql", "roundtrips", "latency"]',
    true
);

-- Question 7: STAR Behavioral Question (HR/Behavioral)
INSERT INTO interview_questions (
    topic_id, question_text, question_type, difficulty, interview_round, question_source, source_reference,
    expected_answer, explanation, interview_points, common_mistakes, follow_up_questions, real_world_example, evaluation_keywords, is_published
) VALUES (
    (SELECT id FROM interview_topics WHERE slug = 'star-behavioral'),
    'Tell me about a time when you faced a critical production bug or tight deadline. How did you handle the situation?',
    'HR_BEHAVIORAL', 'INTERMEDIATE', 'ROUND_4_MANAGERIAL_HR', 'PRACTICE_QUESTION', 'Standard Behavioral Interview for Amazon (Ownership/Deliver Results), TCS, and Infosys HR rounds',
    'Structure answer using the STAR Method:\n- Situation: Describe the context (e.g., "During the final release week of our university e-commerce capstone project, our payment integration service failed under concurrent simulated load").\n- Task: Define your responsibility (e.g., "As the backend lead, I needed to identify the bottleneck, fix the race condition, and deliver the system before the hard deployment deadline").\n- Action: Explain specific technical and team steps (e.g., "I captured thread dumps, isolated synchronized bottlenecks, refactored to ConcurrentHashMap and optimistic locking with @Version, and coordinated daily 15-minute syncs").\n- Result: Quantify the positive outcome (e.g., "Throughput increased by 300%, zero payment transaction drops, and we passed production sign-off 1 day ahead of schedule with 99.9% uptime").',
    'Interviewer evaluates:\n1. Structured communication (STAR framework).\n2. Ownership and technical calmness under pressure.\n3. Problem-solving agility and collaboration.\n4. Measurable outcomes and lessons learned.',
    '["Use STAR: Situation, Task, Action, Result", "Focus on your individual actions (use I, not just we)", "Include quantified measurable results (e.g., % improvement, time saved)", "Demonstrate composure, ownership, and collaborative communication"]',
    '["Rambling without clear structure", "Blaming teammates or past management", "Failing to explain the final concrete result"]',
    '["What would you do differently if you faced that problem today?", "How do you prioritize tasks when multiple urgent deadlines clash?"]',
    'Refactoring a thread-unsafe shared resource during high-concurrency testing before project go-live.',
    '["star method", "situation", "task", "action", "result", "ownership", "deadline", "problem solving", "concurrency", "communication", "outcome"]',
    true
);

-- 5. Map Questions to Companies
INSERT INTO company_interview_questions (company_id, question_id, frequency, last_seen_year, role_title) VALUES
((SELECT id FROM companies WHERE slug = 'tcs'), 1, 'HIGH', 2025, 'Digital / Prime Java Developer'),
((SELECT id FROM companies WHERE slug = 'infosys'), 1, 'HIGH', 2025, 'Specialist Programmer'),
((SELECT id FROM companies WHERE slug = 'amazon'), 1, 'HIGH', 2024, 'Software Development Engineer I'),
((SELECT id FROM companies WHERE slug = 'jpmorgan-chase'), 1, 'HIGH', 2025, 'Software Engineer - Java Full Stack'),
((SELECT id FROM companies WHERE slug = 'amazon'), 2, 'HIGH', 2025, 'SDE I / SDE II'),
((SELECT id FROM companies WHERE slug = 'jpmorgan-chase'), 2, 'HIGH', 2024, 'Java Microservices Engineer'),
((SELECT id FROM companies WHERE slug = 'infosys'), 3, 'HIGH', 2025, 'Digital Specialist Engineer'),
((SELECT id FROM companies WHERE slug = 'tcs'), 3, 'HIGH', 2025, 'Ninja / Digital Developer'),
((SELECT id FROM companies WHERE slug = 'jpmorgan-chase'), 4, 'HIGH', 2025, 'Full Stack Java Engineer'),
((SELECT id FROM companies WHERE slug = 'accenture'), 5, 'HIGH', 2025, 'Associate Software Engineer'),
((SELECT id FROM companies WHERE slug = 'cognizant'), 5, 'HIGH', 2025, 'GenC Elevate Full Stack'),
((SELECT id FROM companies WHERE slug = 'amazon'), 6, 'HIGH', 2025, 'SDE I'),
((SELECT id FROM companies WHERE slug = 'tcs'), 6, 'HIGH', 2025, 'TCS Prime Developer'),
((SELECT id FROM companies WHERE slug = 'amazon'), 7, 'HIGH', 2025, 'SDE I (Leadership Principles)'),
((SELECT id FROM companies WHERE slug = 'tcs'), 7, 'HIGH', 2025, 'TCS Digital HR Round');
