-- ============================================================
-- V8: Seed MCQ Questions for Core Java Topics
-- ============================================================

-- Helper: We insert questions linked to topics by slug lookup

-- ── Java Introduction Questions ───────────────────────────────

INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'What does WORA stand for in Java?',
'EASY',
'WORA stands for "Write Once, Run Anywhere". This is Java''s core promise — you write your code once and it runs on any platform that has a JVM installed, without recompilation.',
'WORA is fundamental to Java''s cross-platform nature. Interviewers often ask this to test if you understand why Java uses bytecode and JVM.',
'TCS,Infosys,Wipro'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='java-introduction' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'Write Once, Run Anywhere',     true,  null),
  ('B', 'Write Once, Reuse Anywhere',   false, 'Reuse is not the term — it is Run. Java compiles to bytecode that runs on any JVM, it is not specifically about code reuse.'),
  ('C', 'Write Once, Read Anywhere',    false, 'Read is not the term. The key feature is that compiled bytecode can be executed anywhere with a JVM.'),
  ('D', 'Write Once, Rebuild Anywhere', false, 'Rebuild is wrong — the whole point of WORA is that you do NOT need to rebuild for each platform.')
) AS o(label, text, correct, why)
WHERE q.question_text = 'What does WORA stand for in Java?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='java-introduction' AND c.slug='core-java-programming');

-- Question 2
INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'Which component of Java is responsible for executing bytecode?',
'EASY',
'The JVM (Java Virtual Machine) is responsible for executing Java bytecode. The JDK contains the compiler (javac) which converts .java files to .class (bytecode) files. The JVM then interprets/compiles this bytecode to machine code for the specific platform.',
'Understanding JVM vs JDK vs JRE is a very common interview question. JVM executes bytecode, JRE provides the runtime environment, JDK is the full development kit.',
'TCS,Infosys,Wipro,Accenture,Cognizant'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='java-introduction' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'JDK',  false, 'JDK (Java Development Kit) is the development toolkit. It contains the compiler (javac) but the actual bytecode execution is done by JVM.'),
  ('B', 'JRE',  false, 'JRE (Java Runtime Environment) provides the environment for running Java programs, but the bytecode execution engine within it is the JVM.'),
  ('C', 'JVM',  true,  null),
  ('D', 'javac',false, 'javac is the Java compiler that converts source code (.java) to bytecode (.class). It does NOT execute bytecode.')
) AS o(label, text, correct, why)
WHERE q.question_text = 'Which component of Java is responsible for executing bytecode?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='java-introduction' AND c.slug='core-java-programming');

-- ── OOP Questions ─────────────────────────────────────────────

INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'Which of the following is NOT one of the four pillars of OOP in Java?',
'EASY',
'The four pillars of OOP are: Encapsulation, Inheritance, Polymorphism, and Abstraction. Compilation is a process of converting source code to bytecode — it is not an OOP concept.',
'Interviewers frequently test knowledge of OOP pillars. Always remember: EIPA — Encapsulation, Inheritance, Polymorphism, Abstraction.',
'TCS,Infosys,Wipro,Capgemini,Cognizant,Accenture'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='oop-concepts' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'Encapsulation', false, 'Encapsulation IS one of the four OOP pillars. It means hiding internal data and exposing only what is necessary through methods.'),
  ('B', 'Compilation',   true,  null),
  ('C', 'Polymorphism',  false, 'Polymorphism IS one of the four OOP pillars. It means one interface, many implementations — method overloading and overriding.'),
  ('D', 'Inheritance',   false, 'Inheritance IS one of the four OOP pillars. It allows a class to acquire properties and behaviors of another class using extends.')
) AS o(label, text, correct, why)
WHERE q.question_text = 'Which of the following is NOT one of the four pillars of OOP in Java?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='oop-concepts' AND c.slug='core-java-programming');

-- ── HashMap Questions ─────────────────────────────────────────

INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'What is the default initial capacity and load factor of HashMap in Java?',
'MEDIUM',
'HashMap has a default initial capacity of 16 (buckets) and a default load factor of 0.75. This means when the HashMap is 75% full (12 entries for capacity 16), it rehashes and doubles its capacity to 32. These values balance memory usage and lookup performance.',
'This is a very common HashMap internals question. The capacity and load factor directly affect performance. When load factor is exceeded, rehashing occurs which is O(n) — expensive.',
'Infosys,Wipro,TCS,Capgemini,Deloitte,Accenture'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='hashmap' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'Capacity: 10, Load Factor: 0.75', false, 'Capacity 10 is incorrect. The default capacity is 16 (a power of 2), which allows efficient bitwise operations for bucket index calculation.'),
  ('B', 'Capacity: 16, Load Factor: 0.75', true,  null),
  ('C', 'Capacity: 16, Load Factor: 0.80', false, 'Load factor 0.80 is incorrect. The default is 0.75, chosen to balance between memory usage and performance.'),
  ('D', 'Capacity: 32, Load Factor: 0.75', false, 'Initial capacity 32 is wrong. 32 is the capacity AFTER the first rehash when the map exceeds 12 entries (75% of 16).')
) AS o(label, text, correct, why)
WHERE q.question_text = 'What is the default initial capacity and load factor of HashMap in Java?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='hashmap' AND c.slug='core-java-programming');

-- Question 2 on HashMap
INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'Which HashMap operation has O(1) average time complexity?',
'MEDIUM',
'HashMap''s get() and put() operations have O(1) average time complexity because: 1) hashCode() is used to compute the bucket index in O(1). 2) In an ideal case with no collision, the value is found directly. In worst case (all keys hash to same bucket), it degrades to O(n) or O(log n) in Java 8+.',
'Time complexity of HashMap operations is heavily tested. Remember: average O(1) for get/put. Worst case O(n) for Java 7, O(log n) for Java 8+ due to tree nodes. Iteration is O(n).',
'TCS,Infosys,Amazon,Deloitte'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='hashmap' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'Only get() is O(1)',         false, 'Both get() and put() are O(1) average. Only stating get() is incomplete and incorrect.'),
  ('B', 'Both get() and put()',        true,  null),
  ('C', 'Only put() is O(1)',          false, 'Both get() and put() are O(1) average. Only stating put() is incomplete and incorrect.'),
  ('D', 'Neither — both are O(log n)', false, 'O(log n) is the worst case for Java 8+ when there are many collisions causing a bucket to convert to a tree. Average case is O(1).')
) AS o(label, text, correct, why)
WHERE q.question_text = 'Which HashMap operation has O(1) average time complexity?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='hashmap' AND c.slug='core-java-programming');

-- ── Stream API Questions ──────────────────────────────────────

INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'Which of the following is a terminal operation in Java Streams?',
'MEDIUM',
'Terminal operations in Java Streams produce a final result and consume the stream. collect(), count(), forEach(), findFirst(), reduce(), anyMatch() are terminal operations. filter(), map(), sorted(), distinct(), limit() are intermediate operations that return a Stream and support chaining.',
'Knowing which operations are intermediate vs terminal is crucial. Intermediate operations are lazy — they only execute when a terminal operation is called. This is a common interview question at all experience levels.',
'TCS,Infosys,Wipro,Accenture,Deloitte,Capgemini'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='stream-api' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'filter()', false, 'filter() is an INTERMEDIATE operation. It returns a new Stream containing only elements that match the predicate. It is lazy and does not produce a final result.'),
  ('B', 'map()',    false, 'map() is an INTERMEDIATE operation. It transforms each element to another element and returns a new Stream. It does not consume the stream.'),
  ('C', 'sorted()', false, 'sorted() is an INTERMEDIATE operation. It returns a new Stream with elements sorted. You still need a terminal operation to get results.'),
  ('D', 'collect()',true,  null)
) AS o(label, text, correct, why)
WHERE q.question_text = 'Which of the following is a terminal operation in Java Streams?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='stream-api' AND c.slug='core-java-programming');

-- Question 2 on Stream API
INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'What happens if you call a terminal operation on an already consumed Stream?',
'HARD',
'A Java Stream can only be consumed once. Once a terminal operation is called, the stream is consumed. Any subsequent call to any operation on the same stream throws IllegalStateException: stream has already been operated upon or closed.',
'This is a common trap in coding interviews and real-world bugs. Always create a new stream from the source collection if you need to process it again. Highlight that this is different from Collections which can be iterated multiple times.',
'Infosys,Deloitte,Wipro,Capgemini'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='stream-api' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'It processes the stream again from the beginning', false, 'Streams do NOT reset or replay. Unlike Collections, a consumed Stream cannot be restarted. This is a common misconception.'),
  ('B', 'It throws IllegalStateException',                  true,  null),
  ('C', 'It returns an empty Stream',                       false, 'An empty Stream is not returned. Java throws an exception to explicitly signal that the stream has already been used.'),
  ('D', 'It silently does nothing',                         false, 'Java does not silently ignore this error. It throws IllegalStateException to alert the developer that the stream was already consumed.')
) AS o(label, text, correct, why)
WHERE q.question_text = 'What happens if you call a terminal operation on an already consumed Stream?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='stream-api' AND c.slug='core-java-programming');

-- ── Exception Handling Questions ──────────────────────────────

INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'Which of the following is an unchecked exception in Java?',
'MEDIUM',
'Unchecked exceptions (RuntimeException subclasses) do NOT need to be declared with throws or caught — the compiler does not enforce handling. NullPointerException, ArrayIndexOutOfBoundsException, ClassCastException, IllegalArgumentException are unchecked. Checked exceptions (IOException, SQLException) MUST be caught or declared.',
'Checked vs unchecked exceptions is a very common interview topic. Remember: unchecked = RuntimeException subclasses. Checked exceptions are compile-time enforced. Error classes (OutOfMemoryError) are also unchecked but are not exceptions — they are Errors.',
'TCS,Infosys,Wipro,Accenture,Cognizant,Capgemini'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='exception-handling' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'IOException',              false, 'IOException is a CHECKED exception. The compiler requires you to either catch it or declare it with throws. It extends Exception directly, not RuntimeException.'),
  ('B', 'SQLException',             false, 'SQLException is a CHECKED exception used in database operations. You must handle it at compile time.'),
  ('C', 'NullPointerException',     true,  null),
  ('D', 'ClassNotFoundException',   false, 'ClassNotFoundException is a CHECKED exception. The compiler requires it to be caught or declared when using Class.forName() or similar reflective operations.')
) AS o(label, text, correct, why)
WHERE q.question_text = 'Which of the following is an unchecked exception in Java?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='exception-handling' AND c.slug='core-java-programming');

-- ── Collections Questions ─────────────────────────────────────

INSERT INTO questions (topic_id, question_text, difficulty, explanation, interview_point, company_tags)
SELECT t.id,
'What is the main difference between ArrayList and LinkedList?',
'MEDIUM',
'ArrayList is backed by a dynamic array — fast random access O(1) by index but slow insertion/deletion in the middle O(n) due to shifting. LinkedList is backed by a doubly linked list — slow random access O(n) but fast insertion/deletion at head/tail O(1). ArrayList is preferred when read operations are frequent; LinkedList when insertion/deletion is frequent.',
'This is one of the most commonly asked Java interview questions. Be specific about internal implementation (array vs linked list) and the resulting time complexities. Also mention that ArrayList uses less memory than LinkedList (no node overhead).',
'TCS,Infosys,Wipro,Accenture,Cognizant,Capgemini,Deloitte,HCL'
FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
WHERE t.slug='arraylist' AND c.slug='core-java-programming';

INSERT INTO question_options (question_id, option_label, option_text, is_correct, why_wrong)
SELECT q.id, o.label, o.text, o.correct, o.why
FROM questions q
CROSS JOIN (VALUES
  ('A', 'ArrayList allows duplicates, LinkedList does not',                           false, 'Both ArrayList and LinkedList allow duplicate elements. The difference is in their internal data structure and resulting performance characteristics.'),
  ('B', 'ArrayList uses dynamic array (fast get), LinkedList uses linked list (fast add/remove)', true, null),
  ('C', 'LinkedList allows null elements, ArrayList does not',                        false, 'Both ArrayList and LinkedList allow null elements. This is not a differentiating factor between them.'),
  ('D', 'ArrayList is synchronized, LinkedList is not',                               false, 'Neither ArrayList nor LinkedList is synchronized. For thread-safe alternatives, use CopyOnWriteArrayList or Collections.synchronizedList().')
) AS o(label, text, correct, why)
WHERE q.question_text = 'What is the main difference between ArrayList and LinkedList?' AND q.topic_id = (
  SELECT t.id FROM topics t JOIN course_modules m ON t.module_id=m.id JOIN courses c ON m.course_id=c.id
  WHERE t.slug='arraylist' AND c.slug='core-java-programming');
