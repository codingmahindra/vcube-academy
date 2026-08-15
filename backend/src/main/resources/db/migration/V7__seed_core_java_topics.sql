-- ============================================================
-- V7: Seed Core Java Course Topics with Content
-- ============================================================

-- ── Module 1: Java Fundamentals ─────────────────────────────

INSERT INTO topics (module_id, title, slug, difficulty, estimated_minutes, display_order)
SELECT m.id, t.title, t.slug, t.diff, t.mins, t.ord
FROM course_modules m
CROSS JOIN (VALUES
  ('Java Introduction',       'java-introduction',       'EASY', 20, 1),
  ('JVM / JRE / JDK',         'jvm-jre-jdk',             'EASY', 25, 2),
  ('Variables',               'variables',               'EASY', 20, 3),
  ('Data Types',              'data-types',              'EASY', 25, 4),
  ('Operators',               'operators',               'EASY', 25, 5),
  ('Control Statements',      'control-statements',      'EASY', 30, 6),
  ('Arrays',                  'arrays',                  'EASY', 35, 7),
  ('Strings',                 'strings',                 'MEDIUM',35,8),
  ('Methods',                 'methods',                 'EASY', 30, 9)
) AS t(title, slug, diff, mins, ord)
WHERE m.title = 'Java Fundamentals'
  AND m.course_id = (SELECT id FROM courses WHERE slug = 'core-java-programming')
ON CONFLICT (module_id, slug) DO NOTHING;

-- ── Module 2: OOP ────────────────────────────────────────────

INSERT INTO topics (module_id, title, slug, difficulty, estimated_minutes, display_order)
SELECT m.id, t.title, t.slug, t.diff, t.mins, t.ord
FROM course_modules m
CROSS JOIN (VALUES
  ('OOP Concepts',            'oop-concepts',            'EASY', 30, 1),
  ('Classes and Objects',     'classes-and-objects',     'EASY', 35, 2),
  ('Constructors',            'constructors',            'EASY', 25, 3),
  ('this keyword',            'this-keyword',            'EASY', 20, 4),
  ('static keyword',          'static-keyword',          'EASY', 25, 5),
  ('final keyword',           'final-keyword',           'EASY', 20, 6),
  ('Inheritance',             'inheritance',             'MEDIUM',35,7),
  ('Polymorphism',            'polymorphism',            'MEDIUM',35,8),
  ('Method Overloading',      'method-overloading',      'EASY', 25, 9),
  ('Method Overriding',       'method-overriding',       'MEDIUM',30,10),
  ('Abstraction',             'abstraction',             'MEDIUM',35,11),
  ('Interfaces',              'interfaces',              'MEDIUM',40,12),
  ('Encapsulation',           'encapsulation',           'EASY', 25, 13),
  ('Packages',                'packages',                'EASY', 20, 14),
  ('Access Modifiers',        'access-modifiers',        'EASY', 20, 15)
) AS t(title, slug, diff, mins, ord)
WHERE m.title = 'Object-Oriented Programming'
  AND m.course_id = (SELECT id FROM courses WHERE slug = 'core-java-programming')
ON CONFLICT (module_id, slug) DO NOTHING;

-- ── Module 3: Exception Handling ─────────────────────────────

INSERT INTO topics (module_id, title, slug, difficulty, estimated_minutes, display_order)
SELECT m.id, t.title, t.slug, t.diff, t.mins, t.ord
FROM course_modules m
CROSS JOIN (VALUES
  ('Exception Handling',      'exception-handling',      'MEDIUM',40,1),
  ('Custom Exceptions',       'custom-exceptions',       'MEDIUM',30,2)
) AS t(title, slug, diff, mins, ord)
WHERE m.title = 'Exception Handling'
  AND m.course_id = (SELECT id FROM courses WHERE slug = 'core-java-programming')
ON CONFLICT (module_id, slug) DO NOTHING;

-- ── Module 4: Collections Framework ──────────────────────────

INSERT INTO topics (module_id, title, slug, difficulty, estimated_minutes, display_order)
SELECT m.id, t.title, t.slug, t.diff, t.mins, t.ord
FROM course_modules m
CROSS JOIN (VALUES
  ('Collections Overview',    'collections-overview',    'MEDIUM',30,1),
  ('List Interface',          'list-interface',          'MEDIUM',25,2),
  ('ArrayList',               'arraylist',               'MEDIUM',30,3),
  ('LinkedList',              'linkedlist',              'MEDIUM',30,4),
  ('Set Interface',           'set-interface',           'MEDIUM',25,5),
  ('HashSet',                 'hashset',                 'MEDIUM',25,6),
  ('LinkedHashSet',           'linkedhashset',           'EASY', 20,7),
  ('TreeSet',                 'treeset',                 'MEDIUM',25,8),
  ('Map Interface',           'map-interface',           'MEDIUM',30,9),
  ('HashMap',                 'hashmap',                 'HARD', 40,10),
  ('LinkedHashMap',           'linkedhashmap',           'MEDIUM',25,11),
  ('TreeMap',                 'treemap',                 'MEDIUM',25,12),
  ('Queue & Deque',           'queue-deque',             'MEDIUM',30,13),
  ('PriorityQueue',           'priorityqueue',           'MEDIUM',30,14),
  ('Iterator',                'iterator',                'EASY', 20,15),
  ('Comparable & Comparator', 'comparable-comparator',   'HARD', 35,16),
  ('Generics',                'generics',                'HARD', 40,17),
  ('Wrapper Classes',         'wrapper-classes',         'EASY', 20,18),
  ('StringBuilder & StringBuffer','stringbuilder-stringbuffer','MEDIUM',25,19)
) AS t(title, slug, diff, mins, ord)
WHERE m.title = 'Collections Framework'
  AND m.course_id = (SELECT id FROM courses WHERE slug = 'core-java-programming')
ON CONFLICT (module_id, slug) DO NOTHING;

-- ── Module 5: Java 8 Features ────────────────────────────────

INSERT INTO topics (module_id, title, slug, difficulty, estimated_minutes, display_order)
SELECT m.id, t.title, t.slug, t.diff, t.mins, t.ord
FROM course_modules m
CROSS JOIN (VALUES
  ('Lambda Expressions',      'lambda-expressions',      'MEDIUM',35,1),
  ('Functional Interfaces',   'functional-interfaces',   'MEDIUM',30,2),
  ('Stream API',              'stream-api',              'HARD', 50,3),
  ('Optional',                'optional',                'MEDIUM',25,4),
  ('Date and Time API',       'date-time-api',           'MEDIUM',30,5)
) AS t(title, slug, diff, mins, ord)
WHERE m.title = 'Java 8 Features'
  AND m.course_id = (SELECT id FROM courses WHERE slug = 'core-java-programming')
ON CONFLICT (module_id, slug) DO NOTHING;

-- ── Module 6: Multithreading ──────────────────────────────────

INSERT INTO topics (module_id, title, slug, difficulty, estimated_minutes, display_order)
SELECT m.id, t.title, t.slug, t.diff, t.mins, t.ord
FROM course_modules m
CROSS JOIN (VALUES
  ('Multithreading Basics',   'multithreading-basics',   'HARD', 45,1),
  ('Thread Class',            'thread-class',            'HARD', 35,2),
  ('Runnable Interface',      'runnable-interface',      'MEDIUM',30,3),
  ('Synchronization',         'synchronization',         'HARD', 45,4),
  ('ExecutorService',         'executorservice',         'HARD', 40,5),
  ('Concurrency Utilities',   'concurrency-utilities',   'HARD', 40,6)
) AS t(title, slug, diff, mins, ord)
WHERE m.title = 'Multithreading & Concurrency'
  AND m.course_id = (SELECT id FROM courses WHERE slug = 'core-java-programming')
ON CONFLICT (module_id, slug) DO NOTHING;

-- ── Module 7: JVM Internals ───────────────────────────────────

INSERT INTO topics (module_id, title, slug, difficulty, estimated_minutes, display_order)
SELECT m.id, t.title, t.slug, t.diff, t.mins, t.ord
FROM course_modules m
CROSS JOIN (VALUES
  ('JVM Memory Model',        'jvm-memory-model',        'HARD', 40,1),
  ('Garbage Collection',      'garbage-collection',      'HARD', 35,2),
  ('Inner & Nested Classes',  'inner-nested-classes',    'MEDIUM',30,3),
  ('Enum',                    'enum',                    'EASY', 25,4),
  ('Annotations',             'annotations',             'MEDIUM',30,5),
  ('Serialization',           'serialization',           'MEDIUM',30,6),
  ('Reflection',              'reflection',              'HARD', 35,7),
  ('JDBC',                    'jdbc',                    'MEDIUM',45,8)
) AS t(title, slug, diff, mins, ord)
WHERE m.title = 'JVM Internals'
  AND m.course_id = (SELECT id FROM courses WHERE slug = 'core-java-programming')
ON CONFLICT (module_id, slug) DO NOTHING;

-- ── Topic Content: Java Introduction ─────────────────────────

INSERT INTO topic_contents (topic_id, explanation, simple_explanation, real_world_example, syntax_example, code_example, interview_points, common_mistakes, practice_questions)
SELECT t.id,
'Java is a high-level, class-based, object-oriented programming language designed to have as few implementation dependencies as possible. Created by James Gosling at Sun Microsystems in 1995, Java follows the "Write Once, Run Anywhere" (WORA) principle. Java programs are compiled into bytecode that runs on the Java Virtual Machine (JVM), making them platform-independent.',
'Think of Java like English — a universal language that anyone can understand regardless of where they are. You write your Java code once, and it runs on any device that has Java installed.',
'Banks use Java for their core systems because it runs the same way on Windows servers in New York and Linux servers in Tokyo. Android apps are written in Java. Netflix, Amazon, and LinkedIn use Java for their backends.',
'public class ClassName {
    public static void main(String[] args) {
        // your code here
    }
}',
'public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, VCUBE Academy!");
        System.out.println("Welcome to Java Programming!");
    }
}
// Output:
// Hello, VCUBE Academy!
// Welcome to Java Programming!',
'• Java is platform-independent due to JVM bytecode
• Java is strongly typed — all variables must be declared
• Java is object-oriented — everything is a class
• Java is compiled then interpreted
• "Write Once, Run Anywhere" (WORA) is Java''s key feature',
'• Forgetting semicolons at end of statements
• Confusing Java with JavaScript (they are completely different)
• Case sensitivity: main() ≠ Main() ≠ MAIN()
• Forgetting the main method signature exactly',
'1. What is Java and why is it popular?
2. Explain "Write Once, Run Anywhere"
3. What is the difference between JDK, JRE, and JVM?
4. Who created Java and when?
5. Name 5 real-world applications of Java'
FROM topics t
JOIN course_modules m ON t.module_id = m.id
JOIN courses c ON m.course_id = c.id
WHERE t.slug = 'java-introduction' AND c.slug = 'core-java-programming'
ON CONFLICT (topic_id) DO NOTHING;

-- ── Topic Content: JVM/JRE/JDK ───────────────────────────────

INSERT INTO topic_contents (topic_id, explanation, simple_explanation, real_world_example, syntax_example, code_example, interview_points, common_mistakes, practice_questions)
SELECT t.id,
'JDK (Java Development Kit) is the full development package containing the compiler (javac), JRE, and developer tools. JRE (Java Runtime Environment) is what users need to run Java programs — it contains the JVM and standard libraries. JVM (Java Virtual Machine) is the engine that executes Java bytecode. The compilation flow is: Source Code (.java) → Compiler (javac) → Bytecode (.class) → JVM → Machine Code → Execution.',
'JDK is the full toolkit for developers (like a complete workshop). JRE is just what you need to run programs (like having just the tools to use something). JVM is the actual engine that runs your code (like the engine in a car).',
'When a developer writes Java code at a company: 1) They install JDK to develop and compile. 2) The servers where the app runs only need JRE. 3) The JVM on the server executes the bytecode regardless of the server OS.',
'javac HelloWorld.java    // compile: creates HelloWorld.class
java HelloWorld          // run: JVM executes the bytecode',
'// Check your Java version
// In terminal: java -version
// In terminal: javac -version

public class JvmDemo {
    public static void main(String[] args) {
        // Runtime info from JVM
        Runtime rt = Runtime.getRuntime();
        System.out.println("Available processors: " + rt.availableProcessors());
        System.out.println("Free memory: " + rt.freeMemory() / 1024 / 1024 + " MB");
        System.out.println("Max memory: " + rt.maxMemory() / 1024 / 1024 + " MB");
    }
}',
'• JDK = JRE + Compiler + Developer Tools
• JRE = JVM + Standard Libraries
• JVM = Bytecode Executor (platform-specific)
• JVM has: Class Loader, Runtime Memory, Execution Engine
• JVM is platform-specific but bytecode is platform-independent',
'• Confusing JDK with JRE — JDK includes JRE
• Thinking JVM is platform-independent — JVM itself is platform-specific
• Not knowing that .class files contain bytecode not machine code',
'1. What is the difference between JDK, JRE, and JVM?
2. Explain the Java compilation and execution process
3. Is the JVM platform-independent? Explain.
4. What components does JVM contain?
5. Why do we need JRE on a server?'
FROM topics t
JOIN course_modules m ON t.module_id = m.id
JOIN courses c ON m.course_id = c.id
WHERE t.slug = 'jvm-jre-jdk' AND c.slug = 'core-java-programming'
ON CONFLICT (topic_id) DO NOTHING;

-- ── Topic Content: Variables ──────────────────────────────────

INSERT INTO topic_contents (topic_id, explanation, simple_explanation, real_world_example, syntax_example, code_example, interview_points, common_mistakes, practice_questions)
SELECT t.id,
'A variable in Java is a named memory location that stores data. Variables must be declared with a type before use. Java has three kinds of variables: Local variables (declared inside methods, no default value), Instance variables (declared inside a class but outside methods, have default values), and Static/Class variables (shared across all instances, declared with static keyword).',
'A variable is like a labeled box. The label is the variable name, and the box stores the value. The type of box determines what kind of data it can hold.',
'In a banking app: customerName (String), accountBalance (double), isActive (boolean), accountNumber (long) are all variables storing different customer information.',
'dataType variableName;              // declaration
dataType variableName = value;      // declaration + initialization
int age = 25;
String name = "SriKanth";
double salary = 75000.50;
boolean isActive = true;',
'public class VariablesDemo {
    // Instance variable (belongs to object)
    String name = "VCUBE Student";

    // Static variable (shared by all)
    static int studentCount = 0;

    public static void main(String[] args) {
        // Local variable (only inside this method)
        int age = 22;
        double gpa = 8.5;
        boolean isEnrolled = true;
        char grade = ''A'';

        System.out.println("Name: " + new VariablesDemo().name);
        System.out.println("Age: " + age);
        System.out.println("GPA: " + gpa);
        System.out.println("Enrolled: " + isEnrolled);
        System.out.println("Grade: " + grade);
    }
}',
'• Local variables have no default value — must initialize before use
• Instance variables have default values (int=0, boolean=false, String=null)
• Variable names are case-sensitive
• Java is strongly typed — type must match
• Naming convention: camelCase for variables',
'• Using a local variable before initializing it
• Using reserved keywords as variable names
• Confusing = (assignment) with == (comparison)
• Declaring variables inside a loop unnecessarily',
'1. What are the types of variables in Java?
2. What are the default values of instance variables?
3. What is the difference between local and instance variables?
4. Can you declare multiple variables in one line?
5. What are naming conventions for Java variables?'
FROM topics t
JOIN course_modules m ON t.module_id = m.id
JOIN courses c ON m.course_id = c.id
WHERE t.slug = 'variables' AND c.slug = 'core-java-programming'
ON CONFLICT (topic_id) DO NOTHING;

-- ── Topic Content: OOP Concepts ──────────────────────────────

INSERT INTO topic_contents (topic_id, explanation, simple_explanation, real_world_example, syntax_example, code_example, interview_points, common_mistakes, practice_questions)
SELECT t.id,
'Object-Oriented Programming (OOP) is a programming paradigm based on the concept of "objects" which contain data (fields/attributes) and behavior (methods). Java is a pure OOP language. The four pillars of OOP are: Encapsulation (hiding internal details), Inheritance (reusing code from parent classes), Polymorphism (one interface, many implementations), and Abstraction (hiding complexity, showing only essentials).',
'OOP is like building with LEGO blocks. Each block (object) has its own shape and purpose. You can combine blocks to build complex structures. The blocks have properties (color, size) and behaviors (how they connect).',
'A Car has: Properties (color, brand, speed) and Behaviors (start(), stop(), accelerate()). A BankAccount has: Properties (balance, accountNumber) and Behaviors (deposit(), withdraw(), getBalance()). These real-world entities map directly to Java classes.',
'class ClassName {
    // fields (properties)
    dataType fieldName;

    // methods (behaviors)
    returnType methodName(parameters) {
        // behavior
    }
}',
'// Real-world example: Student class
public class Student {
    // Encapsulation: private fields
    private String name;
    private int age;
    private double gpa;

    // Constructor
    public Student(String name, int age, double gpa) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }

    // Behavior (methods)
    public void study() {
        System.out.println(name + " is studying...");
    }

    public void displayInfo() {
        System.out.println("Student: " + name + ", Age: " + age + ", GPA: " + gpa);
    }

    // Getters (controlled access)
    public String getName() { return name; }
    public double getGpa() { return gpa; }
}

public class Main {
    public static void main(String[] args) {
        Student s1 = new Student("Ravi", 22, 8.5);
        Student s2 = new Student("Priya", 21, 9.1);
        s1.study();
        s2.displayInfo();
    }
}',
'• Four pillars: Encapsulation, Inheritance, Polymorphism, Abstraction
• Java is object-oriented — everything (except primitives) is an object
• Objects are instances of classes
• Class is a blueprint; Object is the actual entity
• OOP promotes code reusability, maintainability, and modularity',
'• Confusing class and object
• Forgetting that Java passes objects by reference
• Not using encapsulation (making fields public)
• Confusing method overloading (polymorphism at compile time) with overriding (at runtime)',
'1. What are the four pillars of OOP?
2. What is the difference between a class and an object?
3. How does Java implement OOP?
4. What is the difference between encapsulation and abstraction?
5. Give a real-world example for each OOP pillar'
FROM topics t
JOIN course_modules m ON t.module_id = m.id
JOIN courses c ON m.course_id = c.id
WHERE t.slug = 'oop-concepts' AND c.slug = 'core-java-programming'
ON CONFLICT (topic_id) DO NOTHING;

-- ── Topic Content: HashMap ────────────────────────────────────

INSERT INTO topic_contents (topic_id, explanation, simple_explanation, real_world_example, syntax_example, code_example, interview_points, common_mistakes, practice_questions)
SELECT t.id,
'HashMap is a key-value data structure that stores entries using hashing. It implements the Map interface and allows one null key and multiple null values. Internal working: uses an array of buckets, each bucket is a linked list (or tree in Java 8+). hashCode() determines the bucket, equals() resolves collisions. Average O(1) for get/put. NOT synchronized (use ConcurrentHashMap for thread safety). Load factor default 0.75, initial capacity 16.',
'HashMap is like a dictionary. You look up a word (key) and immediately find its meaning (value). You don''t scan every page — you go directly to the right section. That is O(1) lookup.',
'In an e-commerce app: Map<String, Product> productCache stores productId→Product for fast O(1) lookups. Word frequency counter: Map<String, Integer> wordCount. Student grade lookup: Map<String, Double> grades.',
'Map<KeyType, ValueType> map = new HashMap<>();
map.put(key, value);
map.get(key);
map.containsKey(key);
map.remove(key);
map.size();
map.keySet();
map.values();
map.entrySet();',
'import java.util.*;

public class HashMapDemo {
    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();

        // Insert
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        scores.put("Alice", 98); // updates existing key

        // Access
        System.out.println("Alice score: " + scores.get("Alice")); // 98

        // Check existence
        System.out.println("Has Bob: " + scores.containsKey("Bob"));

        // Iterate
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // getOrDefault
        int dave = scores.getOrDefault("Dave", 0);
        System.out.println("Dave: " + dave); // 0

        // Word frequency counter
        String[] words = {"java", "is", "great", "java", "is", "fun"};
        Map<String, Integer> freq = new HashMap<>();
        for (String w : words) {
            freq.put(w, freq.getOrDefault(w, 0) + 1);
        }
        System.out.println("Frequencies: " + freq);
    }
}',
'• HashMap uses array + linked list (Java 7) or array + tree (Java 8+)
• Default capacity: 16, load factor: 0.75 → rehash at 12 entries
• hashCode() → bucket index; equals() → resolve collisions
• NOT thread-safe → use ConcurrentHashMap in multithreaded code
• Iteration order is NOT guaranteed (use LinkedHashMap for insertion order)
• TreeMap maintains sorted key order
• Null key allowed (only one); multiple null values allowed',
'• Using mutable objects as keys (hashCode changes, key is lost)
• Expecting consistent iteration order from HashMap
• Not overriding hashCode() when overriding equals()
• Using HashMap in multithreaded code without synchronization',
'1. How does HashMap work internally?
2. What is the difference between HashMap, LinkedHashMap, and TreeMap?
3. What happens when two keys have the same hashCode?
4. What is the time complexity of HashMap operations?
5. How does Java 8 improve HashMap (treeification)?
6. What is the default capacity and load factor?
7. Why should you override both hashCode() and equals()?'
FROM topics t
JOIN course_modules m ON t.module_id = m.id
JOIN courses c ON m.course_id = c.id
WHERE t.slug = 'hashmap' AND c.slug = 'core-java-programming'
ON CONFLICT (topic_id) DO NOTHING;

-- ── Topic Content: Stream API ─────────────────────────────────

INSERT INTO topic_contents (topic_id, explanation, simple_explanation, real_world_example, syntax_example, code_example, interview_points, common_mistakes, practice_questions)
SELECT t.id,
'Java 8 Stream API allows functional-style operations on collections. Streams are NOT data structures — they are a pipeline of operations on data. Streams support lazy evaluation (intermediate operations only execute when a terminal operation is called). Two types of operations: Intermediate (filter, map, sorted, distinct, limit, skip — return a Stream) and Terminal (collect, forEach, count, reduce, findFirst — consume the stream).',
'Think of a Stream like an assembly line in a factory. Raw materials (data) go in one end, pass through different stations (filter, transform, sort), and finished products (results) come out the other end. Each station does one job.',
'E-commerce: filter active products, map to prices, find average price. HR system: filter employees by department, map to salary, calculate total payroll. Log analysis: filter ERROR logs, extract timestamps, count per hour.',
'collection.stream()
    .filter(item -> condition)
    .map(item -> transform)
    .sorted()
    .collect(Collectors.toList());',
'import java.util.*;
import java.util.stream.*;

public class StreamDemo {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        // Filter even numbers and square them
        List<Integer> result = numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .collect(Collectors.toList());
        System.out.println("Even squares: " + result); // [4,16,36,64,100]

        // Sum using reduce
        int sum = numbers.stream()
            .reduce(0, Integer::sum);
        System.out.println("Sum: " + sum); // 55

        // String operations
        List<String> names = Arrays.asList("Alice","Bob","Charlie","David","Eve");
        String result2 = names.stream()
            .filter(n -> n.length() > 3)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.joining(", "));
        System.out.println("Long names: " + result2);

        // Statistics
        OptionalDouble avg = numbers.stream()
            .mapToInt(Integer::intValue)
            .average();
        System.out.println("Average: " + avg.getAsDouble());

        // Group by length
        Map<Integer, List<String>> byLength = names.stream()
            .collect(Collectors.groupingBy(String::length));
        System.out.println("By length: " + byLength);
    }
}',
'• Streams are lazy — intermediate ops don''t execute until terminal op
• A Stream can only be consumed once — reuse requires new stream
• Streams don''t modify the source collection
• parallelStream() for parallel processing
• Common terminal ops: collect, count, forEach, findFirst, reduce, anyMatch
• Collectors.toList(), groupingBy(), joining(), counting() are heavily used in interviews',
'• Trying to reuse a consumed stream (throws IllegalStateException)
• Forgetting to call a terminal operation (nothing executes!)
• Using forEach for transformation (use map instead)
• Not understanding lazy evaluation',
'1. What is the difference between Stream and Collection?
2. What are intermediate and terminal operations?
3. Explain lazy evaluation in Streams
4. Difference between map() and flatMap()?
5. How do you group elements using Stream API?
6. What is the difference between findFirst() and findAny()?
7. Write code to find the second highest number using Streams'
FROM topics t
JOIN course_modules m ON t.module_id = m.id
JOIN courses c ON m.course_id = c.id
WHERE t.slug = 'stream-api' AND c.slug = 'core-java-programming'
ON CONFLICT (topic_id) DO NOTHING;
