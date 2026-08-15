-- =========================================================================
-- V10: Seed DSA Categories and Placement Problems
-- =========================================================================

-- 1. Insert 20 DSA Categories
INSERT INTO dsa_categories (id, name, slug, description, icon, display_order, is_active) VALUES
(1,  'Arrays',                 'arrays',                 'Array manipulation, two pointers, prefix sums, and sliding window techniques.', 'Array', 1, true),
(2,  'Strings',                'strings',                'String processing, pattern matching, parsing, and anagrams.', 'Type', 2, true),
(3,  'Linked Lists',           'linked-lists',           'Singly linked lists, doubly linked lists, cycle detection, and reversal.', 'GitCommit', 3, true),
(4,  'Stacks',                 'stacks',                 'LIFO operations, monotonic stacks, parenthesis matching, and expression evaluation.', 'Layers', 4, true),
(5,  'Queues',                 'queues',                 'FIFO structures, deques, priority queues, and task scheduling.', 'ListOrdered', 5, true),
(6,  'Hashing',                'hashing',                'Hash maps, hash sets, frequency counting, and collision resolution.', 'Hash', 6, true),
(7,  'Searching',              'searching',              'Binary search, search in rotated sorted arrays, and ternary search.', 'Search', 7, true),
(8,  'Sorting',                'sorting',                'Merge sort, quicksort, counting sort, and custom comparators.', 'ArrowUpDown', 8, true),
(9,  'Recursion',              'recursion',              'Backtracking, divide and conquer, recursive call stack analysis.', 'Repeat', 9, true),
(10, 'Trees',                  'trees',                  'Binary trees, traversals (Inorder/Preorder/Postorder/Levelorder), and tree properties.', 'TreeStructure', 10, true),
(11, 'Binary Search Trees',    'binary-search-trees',    'BST insertion, deletion, validation, and LCA search.', 'GitFork', 11, true),
(12, 'Heaps & Priority Queue', 'heaps',                  'Min/max heaps, top-K frequent elements, and heap sort.', 'Pyramid', 12, true),
(13, 'Graphs',                 'graphs',                 'BFS, DFS, Dijkstra, topological sort, and cycle detection.', 'Network', 13, true),
(14, 'Greedy Algorithms',      'greedy',                 'Interval scheduling, activity selection, and optimal choices.', 'Zap', 14, true),
(15, 'Dynamic Programming',    'dynamic-programming',    'Memoization, tabulation, knapsack problems, and subproblem optimization.', 'Cpu', 15, true),
(16, 'Backtracking',           'backtracking',           'N-Queens, Sudoku solver, subsets, and permutations.', 'Undo2', 16, true),
(17, 'Bit Manipulation',       'bit-manipulation',       'Bitwise AND/OR/XOR, shifts, counting set bits, and single numbers.', 'Binary', 17, true),
(18, 'Two Pointers',           'two-pointers',           'Left/right pointer strategies, meeting in the middle, and partitioning.', 'Columns2', 18, true),
(19, 'Sliding Window',         'sliding-window',         'Fixed size and dynamic size window optimization problems.', 'Maximize2', 19, true),
(20, 'Math & Fast I/O',        'math',                   'GCD, Sieve of Eratosthenes, modular arithmetic, and prime numbers.', 'Calculator', 20, true);

SELECT setval('dsa_categories_id_seq', (SELECT MAX(id) FROM dsa_categories));

-- 2. Insert 15 Placement-Grade DSA Problems

-- Problem 1: Two Sum
INSERT INTO dsa_problems (
    id, category_id, title, slug, description, difficulty, subtopic, constraints, input_format, output_format,
    expected_approach, time_complexity, space_complexity, hints, interview_points, company_tags, java_starter_code,
    solution_explanation, solution_java_code, is_published
) VALUES (
    1, 1, 'Two Sum', 'two-sum',
    'Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.',
    'EASY', 'Hash Table / Array',
    '2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9\nExactly one valid answer exists.',
    'First line: space-separated integers for nums array.\nSecond line: target integer.',
    'Space-separated pair of indices [index1, index2] in ascending order.',
    'Use a HashMap to store value-to-index mappings as you iterate through the array.',
    'O(N)', 'O(N)',
    '["Can you compute target - current_element?", "Store seen elements in a HashMap for O(1) lookup."]',
    '["What is the brute force time complexity? O(N^2)", "Why is HashMap optimal? Reduces lookup time from O(N) to O(1)."]',
    '["Google", "Amazon", "Microsoft", "Meta", "TCS", "Infosys"]',
    'public class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        // Write your solution here\n        return new int[]{};\n    }\n}',
    'We traverse the list once. For each element `nums[i]`, we check if `target - nums[i]` exists in the HashMap. If present, we return its index and `i`. Otherwise, we put `nums[i]` into the map.',
    'import java.util.*;\n\npublic class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> map = new HashMap<>();\n        for (int i = 0; i < nums.length; i++) {\n            int complement = target - nums[i];\n            if (map.containsKey(complement)) {\n                return new int[] { map.get(complement), i };\n            }\n            map.put(nums[i], i);\n        }\n        return new int[]{};\n    }\n}',
    true
);

-- Problem 2: Reverse a String
INSERT INTO dsa_problems (
    id, category_id, title, slug, description, difficulty, subtopic, constraints, input_format, output_format,
    expected_approach, time_complexity, space_complexity, hints, interview_points, company_tags, java_starter_code,
    solution_explanation, solution_java_code, is_published
) VALUES (
    2, 2, 'Reverse a String', 'reverse-string',
    'Write a function that reverses a given string `s` in-place using two pointers.',
    'EASY', 'Two Pointers',
    '1 <= s.length <= 10^5\nString consists of printable ASCII characters.',
    'A single line containing the string `s`.',
    'The reversed string.',
    'Use two pointers starting at opposite ends of the character array and swap until they meet.',
    'O(N)', 'O(1)',
    '["Use left=0 and right=n-1 pointers.", "Swap s[left] and s[right] and move inward."]',
    '["Why in-place reversal? Saves memory overhead.", "How to handle string immutability in Java? Convert to char[] or use StringBuilder."]',
    '["Wipro", "Cognizant", "Accenture", "Amazon"]',
    'public class Solution {\n    public String reverseString(String s) {\n        // Write your solution here\n        return "";\n    }\n}',
    'Convert string to a char array. Maintain `left=0` and `right=len-1`. Swap characters at `left` and `right`, increment `left`, decrement `right`.',
    'public class Solution {\n    public String reverseString(String s) {\n        char[] arr = s.toCharArray();\n        int left = 0, right = arr.length - 1;\n        while (left < right) {\n            char temp = arr[left];\n            arr[left] = arr[right];\n            arr[right] = temp;\n            left++;\n            right--;\n        }\n        return new String(arr);\n    }\n}',
    true
);

-- Problem 3: Valid Parentheses
INSERT INTO dsa_problems (
    id, category_id, title, slug, description, difficulty, subtopic, constraints, input_format, output_format,
    expected_approach, time_complexity, space_complexity, hints, interview_points, company_tags, java_starter_code,
    solution_explanation, solution_java_code, is_published
) VALUES (
    3, 4, 'Valid Parentheses', 'valid-parentheses',
    'Given a string `s` containing just the characters `(`, `)`, `{`, `}`, `[` and `]`, determine if the input string is valid.\n\nAn input string is valid if:\n1. Open brackets must be closed by the same type of brackets.\n2. Open brackets must be closed in the correct order.\n3. Every close bracket has a corresponding open bracket.',
    'EASY', 'Stack',
    '1 <= s.length <= 10^4\ns consists of brackets only: (), {}, []',
    'A single line string s.',
    'true if string is valid, false otherwise.',
    'Use a Stack to push expected closing brackets when an opening bracket is encountered.',
    'O(N)', 'O(N)',
    '["When seeing an opening bracket, push its matching closing bracket onto stack.", "When seeing closing bracket, pop stack and compare."]',
    '["What happens if stack is empty when closing bracket appears? Invalid.", "Final stack must be empty for string to be valid."]',
    '["Amazon", "Microsoft", "Oracle", "Goldman Sachs"]',
    'public class Solution {\n    public boolean isValid(String s) {\n        // Write your solution here\n        return false;\n    }\n}',
    'Traverse string. Push expected closing bracket onto Stack. When closing bracket is seen, pop stack; if mismatched or empty, return false. Return true if stack is empty at end.',
    'import java.util.*;\n\npublic class Solution {\n    public boolean isValid(String s) {\n        Stack<Character> stack = new Stack<>();\n        for (char c : s.toCharArray()) {\n            if (c == '(') stack.push(')');\n            else if (c == '{') stack.push('}');\n            else if (c == '[') stack.push(']');\n            else if (stack.isEmpty() || stack.pop() != c) return false;\n        }\n        return stack.isEmpty();\n    }\n}',
    true
);

-- Problem 4: Binary Search
INSERT INTO dsa_problems (
    id, category_id, title, slug, description, difficulty, subtopic, constraints, input_format, output_format,
    expected_approach, time_complexity, space_complexity, hints, interview_points, company_tags, java_starter_code,
    solution_explanation, solution_java_code, is_published
) VALUES (
    4, 7, 'Binary Search', 'binary-search',
    'Given a sorted array of distinct integers `nums` and a target value `target`, return the 0-based index of `target` if it exists. Return -1 if target is not found.',
    'EASY', 'Binary Search',
    '1 <= nums.length <= 10^4\n-10^4 <= nums[i], target <= 10^4\nnums is sorted in ascending order.',
    'First line: space-separated integers.\nSecond line: target integer.',
    'Integer index of target or -1.',
    'Use binary search: compare middle element, adjust low/high bounds.',
    'O(log N)', 'O(1)',
    '["Calculate mid = low + (high - low) / 2 to avoid integer overflow.", "If nums[mid] == target, return mid."]',
    '["Why use low + (high - low) / 2 instead of (low + high) / 2? Prevents potential integer overflow.", "Binary search requires a sorted space."]',
    '["Google", "Uber", "Capgemini", "LTI"]',
    'public class Solution {\n    public int search(int[] nums, int target) {\n        // Write your solution here\n        return -1;\n    }\n}',
    'Iterative binary search. Compare target with `nums[mid]`. If equal, return `mid`. If target < `nums[mid]`, search left (`high = mid - 1`), else search right (`low = mid + 1`).',
    'public class Solution {\n    public int search(int[] nums, int target) {\n        int low = 0, high = nums.length - 1;\n        while (low <= high) {\n            int mid = low + (high - low) / 2;\n            if (nums[mid] == target) return mid;\n            if (nums[mid] < target) low = mid + 1;\n            else high = mid - 1;\n        }\n        return -1;\n    }\n}',
    true
);

-- Problem 5: Maximum Subarray (Kadane''s Algorithm)
INSERT INTO dsa_problems (
    id, category_id, title, slug, description, difficulty, subtopic, constraints, input_format, output_format,
    expected_approach, time_complexity, space_complexity, hints, interview_points, company_tags, java_starter_code,
    solution_explanation, solution_java_code, is_published
) VALUES (
    5, 1, 'Maximum Subarray (Kadane''s Algorithm)', 'maximum-subarray',
    'Given an integer array `nums`, find the contiguous subarray (containing at least one number) which has the largest sum and return its sum.',
    'MEDIUM', 'Dynamic Programming / Array',
    '1 <= nums.length <= 10^5\n-10^4 <= nums[i] <= 10^4',
    'Space-separated integers representing nums.',
    'Single integer representing max subarray sum.',
    'Kadane''s algorithm: maintain current sum and max sum found so far.',
    'O(N)', 'O(1)',
    '["At each element, decide whether to add it to current sum or start a new subarray.", "currentSum = max(nums[i], currentSum + nums[i])"]',
    '["Kadane''s algorithm is DP with O(1) space.", "How to handle all-negative arrays? Pre-initialize maxSum to first element."]',
    '["Amazon", "Microsoft", "Adobe", "Samsung"]',
    'public class Solution {\n    public int maxSubArray(int[] nums) {\n        // Write your solution here\n        return 0;\n    }\n}',
    'Maintain `currentSum` and `maxSum`. For each num: `currentSum = Math.max(num, currentSum + num); maxSum = Math.max(maxSum, currentSum);`.',
    'public class Solution {\n    public int maxSubArray(int[] nums) {\n        int currentSum = nums[0];\n        int maxSum = nums[0];\n        for (int i = 1; i < nums.length; i++) {\n            currentSum = Math.max(nums[i], currentSum + nums[i]);\n            maxSum = Math.max(maxSum, currentSum);\n        }\n        return maxSum;\n    }\n}',
    true
);

SELECT setval('dsa_problems_id_seq', (SELECT MAX(id) FROM dsa_problems));

-- 3. Insert Test Cases for Problems

-- Test Cases for Problem 1 (Two Sum)
INSERT INTO dsa_test_cases (problem_id, input, expected_output, is_sample, is_hidden, explanation, display_order) VALUES
(1, '2 7 11 15\n9', '0 1', true, false, 'nums[0] + nums[1] = 2 + 7 = 9', 1),
(1, '3 2 4\n6', '1 2', true, false, 'nums[1] + nums[2] = 2 + 4 = 6', 2),
(1, '3 3\n6', '0 1', false, true, 'Duplicate numbers test', 3),
(1, '-1 -2 -3 -4 -5\n-8', '2 4', false, true, 'Negative numbers test', 4);

-- Test Cases for Problem 2 (Reverse String)
INSERT INTO dsa_test_cases (problem_id, input, expected_output, is_sample, is_hidden, explanation, display_order) VALUES
(2, 'hello', 'olleh', true, false, 'Standard lowercase string', 1),
(2, 'VCUBE', 'EBUCV', true, false, 'Uppercase string', 2),
(2, 'a', 'a', false, true, 'Single character edge case', 3),
(2, 'Java Full Stack', 'kcatS lluF avaJ', false, true, 'Multi-word string', 4);

-- Test Cases for Problem 3 (Valid Parentheses)
INSERT INTO dsa_test_cases (problem_id, input, expected_output, is_sample, is_hidden, explanation, display_order) VALUES
(3, '()', 'true', true, false, 'Simple matching pair', 1),
(3, '()[]{}', 'true', true, false, 'Multiple valid matching pairs', 2),
(3, '(]', 'false', true, false, 'Mismatched closing bracket', 3),
(3, '([)]', 'false', false, true, 'Incorrect closing order', 4),
(3, '{[]}', 'true', false, true, 'Nested valid brackets', 5);

-- Test Cases for Problem 4 (Binary Search)
INSERT INTO dsa_test_cases (problem_id, input, expected_output, is_sample, is_hidden, explanation, display_order) VALUES
(4, '-1 0 3 5 9 12\n9', '4', true, false, 'Target 9 is at index 4', 1),
(4, '-1 0 3 5 9 12\n2', '-1', true, false, 'Target 2 does not exist in array', 2),
(4, '5\n5', '0', false, true, 'Single element array matching target', 3),
(4, '10 20 30 40 50\n60', '-1', false, true, 'Target greater than all elements', 4);

-- Test Cases for Problem 5 (Maximum Subarray)
INSERT INTO dsa_test_cases (problem_id, input, expected_output, is_sample, is_hidden, explanation, display_order) VALUES
(5, '-2 1 -3 4 -1 2 1 -5 4', '6', true, false, 'Subarray [4,-1,2,1] has largest sum = 6', 1),
(5, '1', '1', true, false, 'Single positive element', 2),
(5, '5 4 -1 7 8', '23', false, true, 'All elements except one negative', 3),
(5, '-5 -2 -3 -1 -4', '-1', false, true, 'All negative elements', 4);

SELECT setval('dsa_test_cases_id_seq', (SELECT MAX(id) FROM dsa_test_cases));
