-- =========================================================================
-- V10: Seed DSA Problems
-- =========================================================================

-- =========================================================================
-- 1. DSA Categories
-- =========================================================================

INSERT INTO dsa_categories
    (name, slug, description, icon, display_order, is_active)
VALUES
    ('Arrays', 'arrays', 'Practice problems based on arrays and array manipulation.', 'array', 1, TRUE),
    ('Strings', 'strings', 'Practice problems based on strings and string manipulation.', 'string', 2, TRUE),
    ('Linked List', 'linked-list', 'Practice problems based on singly and doubly linked lists.', 'link', 3, TRUE),
    ('Stack', 'stack', 'Practice problems based on stack data structures.', 'stack', 4, TRUE),
    ('Queue', 'queue', 'Practice problems based on queue data structures.', 'queue', 5, TRUE),
    ('HashMap', 'hashmap', 'Practice problems using HashMap and hashing techniques.', 'hashmap', 6, TRUE),
    ('Sorting', 'sorting', 'Practice problems based on sorting algorithms.', 'sort', 7, TRUE),
    ('Searching', 'searching', 'Practice problems based on searching algorithms.', 'search', 8, TRUE),
    ('Recursion', 'recursion', 'Practice problems based on recursion.', 'recursion', 9, TRUE),
    ('Trees', 'trees', 'Practice problems based on tree data structures.', 'tree', 10, TRUE),
    ('Graphs', 'graphs', 'Practice problems based on graph algorithms.', 'graph', 11, TRUE),
    ('Dynamic Programming', 'dynamic-programming', 'Practice problems based on dynamic programming.', 'dp', 12, TRUE)
ON CONFLICT (slug) DO NOTHING;


-- =========================================================================
-- 2. ARRAY PROBLEMS
-- =========================================================================

INSERT INTO dsa_problems
(
    category_id,
    title,
    slug,
    description,
    difficulty,
    subtopic,
    constraints,
    input_format,
    output_format,
    expected_approach,
    time_complexity,
    space_complexity,
    hints,
    interview_points,
    company_tags,
    java_starter_code,
    solution_explanation,
    solution_java_code,
    is_published
)
SELECT
    c.id,
    'Find Maximum Element',
    'find-maximum-element',
    'Given an integer array, find and return the maximum element in the array.',
    'EASY',
    'Array Traversal',
    'The array contains at least one element.',
    'An integer array.',
    'Return the maximum element.',
    'Traverse the array once while maintaining the maximum value.',
    'O(n)',
    'O(1)',
    'Initialize max with the first element and compare every remaining element.',
    'Understand array traversal and maintaining a running maximum.',
    'TCS, Infosys, Wipro, Accenture',
    'public class Solution {
    public static int findMaximum(int[] arr) {
        // Write your solution here
        return 0;
    }
}',
    'Start with the first element as the maximum. Traverse the remaining elements. If the current element is greater than max, update max. Finally return max.',
    'public class Solution {
    public static int findMaximum(int[] arr) {
        int max = arr[0];

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        return max;
    }
}',
    TRUE
FROM dsa_categories c
WHERE c.slug = 'arrays'
ON CONFLICT (slug) DO NOTHING;


INSERT INTO dsa_problems
(
    category_id,
    title,
    slug,
    description,
    difficulty,
    subtopic,
    constraints,
    input_format,
    output_format,
    expected_approach,
    time_complexity,
    space_complexity,
    hints,
    interview_points,
    company_tags,
    java_starter_code,
    solution_explanation,
    solution_java_code,
    is_published
)
SELECT
    c.id,
    'Find Second Largest Element',
    'find-second-largest-element',
    'Given an integer array, find the second largest distinct element.',
    'EASY',
    'Array Traversal',
    'The array contains at least two distinct elements.',
    'An integer array.',
    'Return the second largest distinct element.',
    'Maintain the largest and second largest values while traversing the array.',
    'O(n)',
    'O(1)',
    'Keep two variables: largest and secondLargest.',
    'Tests understanding of one-pass array traversal.',
    'TCS, Infosys, Accenture, Cognizant',
    'public class Solution {
    public static int findSecondLargest(int[] arr) {
        // Write your solution here
        return 0;
    }
}',
    'Maintain the largest and second largest values. Update them whenever a larger value is found.',
    'public class Solution {
    public static int findSecondLargest(int[] arr) {
        int largest = Integer.MIN_VALUE;
        int secondLargest = Integer.MIN_VALUE;

        for (int value : arr) {
            if (value > largest) {
                secondLargest = largest;
                largest = value;
            } else if (value > secondLargest && value != largest) {
                secondLargest = value;
            }
        }

        return secondLargest;
    }
}',
    TRUE
FROM dsa_categories c
WHERE c.slug = 'arrays'
ON CONFLICT (slug) DO NOTHING;


-- =========================================================================
-- 3. STRING PROBLEMS
-- =========================================================================

INSERT INTO dsa_problems
(
    category_id,
    title,
    slug,
    description,
    difficulty,
    subtopic,
    constraints,
    input_format,
    output_format,
    expected_approach,
    time_complexity,
    space_complexity,
    hints,
    interview_points,
    company_tags,
    java_starter_code,
    solution_explanation,
    solution_java_code,
    is_published
)
SELECT
    c.id,
    'Reverse a String',
    'reverse-a-string',
    'Given a string, reverse the string and return the reversed result.',
    'EASY',
    'String Manipulation',
    'The string may contain letters, digits, and spaces.',
    'A string.',
    'Return the reversed string.',
    'Use StringBuilder to reverse the characters.',
    'O(n)',
    'O(n)',
    'StringBuilder provides a reverse method.',
    'Basic string manipulation is frequently asked in interviews.',
    'TCS, Infosys, Wipro, Accenture',
    'public class Solution {
    public static String reverse(String str) {
        // Write your solution here
        return "";
    }
}',
    'Create a StringBuilder using the input string and call reverse().',
    'public class Solution {
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}',
    TRUE
FROM dsa_categories c
WHERE c.slug = 'strings'
ON CONFLICT (slug) DO NOTHING;


INSERT INTO dsa_problems
(
    category_id,
    title,
    slug,
    description,
    difficulty,
    subtopic,
    constraints,
    input_format,
    output_format,
    expected_approach,
    time_complexity,
    space_complexity,
    hints,
    interview_points,
    company_tags,
    java_starter_code,
    solution_explanation,
    solution_java_code,
    is_published
)
SELECT
    c.id,
    'Check Palindrome',
    'check-palindrome',
    'Given a string, determine whether it reads the same forward and backward.',
    'EASY',
    'Two Pointer',
    'The string contains printable characters.',
    'A string.',
    'Return true if the string is a palindrome; otherwise return false.',
    'Compare characters from both ends moving toward the center.',
    'O(n)',
    'O(1)',
    'Use two indexes: left and right.',
    'Tests two-pointer technique and string manipulation.',
    'TCS, Infosys, Amazon, Accenture',
    'public class Solution {
    public static boolean isPalindrome(String str) {
        // Write your solution here
        return false;
    }
}',
    'Use two pointers. Compare the characters at left and right. If they differ, the string is not a palindrome.',
    'public class Solution {
    public static boolean isPalindrome(String str) {
        int left = 0;
        int right = str.length() - 1;

        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }
}',
    TRUE
FROM dsa_categories c
WHERE c.slug = 'strings'
ON CONFLICT (slug) DO NOTHING;


-- =========================================================================
-- 4. HASHMAP PROBLEMS
-- =========================================================================

INSERT INTO dsa_problems
(
    category_id,
    title,
    slug,
    description,
    difficulty,
    subtopic,
    constraints,
    input_format,
    output_format,
    expected_approach,
    time_complexity,
    space_complexity,
    hints,
    interview_points,
    company_tags,
    java_starter_code,
    solution_explanation,
    solution_java_code,
    is_published
)
SELECT
    c.id,
    'Two Sum',
    'two-sum',
    'Given an integer array and a target value, find two indices whose values add up to the target.',
    'EASY',
    'HashMap',
    'There is exactly one valid answer.',
    'An integer array and a target integer.',
    'Return the two indices whose values add up to the target.',
    'Store previously visited values in a HashMap and search for target minus current value.',
    'O(n)',
    'O(n)',
    'For every value, calculate target - value.',
    'HashMap based lookup is a common interview technique.',
    'Amazon, Google, Microsoft, TCS',
    'import java.util.*;

public class Solution {
    public static int[] twoSum(int[] nums, int target) {
        // Write your solution here
        return new int[0];
    }
}',
    'Store each number and its index in a HashMap. For every current number, calculate the required complement. If the complement exists, return both indices.',
    'import java.util.*;

public class Solution {
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }

            map.put(nums[i], i);
        }

        return new int[0];
    }
}',
    TRUE
FROM dsa_categories c
WHERE c.slug = 'hashmap'
ON CONFLICT (slug) DO NOTHING;


-- =========================================================================
-- 5. SORTING PROBLEMS
-- =========================================================================

INSERT INTO dsa_problems
(
    category_id,
    title,
    slug,
    description,
    difficulty,
    subtopic,
    constraints,
    input_format,
    output_format,
    expected_approach,
    time_complexity,
    space_complexity,
    hints,
    interview_points,
    company_tags,
    java_starter_code,
    solution_explanation,
    solution_java_code,
    is_published
)
SELECT
    c.id,
    'Bubble Sort',
    'bubble-sort',
    'Sort an integer array using the Bubble Sort algorithm.',
    'EASY',
    'Sorting',
    'The array may contain positive and negative integers.',
    'An integer array.',
    'Return the sorted array.',
    'Repeatedly compare adjacent elements and swap them when they are in the wrong order.',
    'O(n^2)',
    'O(1)',
    'After every pass, the largest remaining element moves to the end.',
    'Understand basic sorting and nested loops.',
    'TCS, Infosys, Wipro',
    'public class Solution {
    public static int[] bubbleSort(int[] arr) {
        // Write your solution here
        return arr;
    }
}',
    'Compare adjacent elements. Swap them when the left element is greater than the right element. Repeat until the array is sorted.',
    'public class Solution {
    public static int[] bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }

        return arr;
    }
}',
    TRUE
FROM dsa_categories c
WHERE c.slug = 'sorting'
ON CONFLICT (slug) DO NOTHING;


-- =========================================================================
-- 6. SEARCHING PROBLEMS
-- =========================================================================

INSERT INTO dsa_problems
(
    category_id,
    title,
    slug,
    description,
    difficulty,
    subtopic,
    constraints,
    input_format,
    output_format,
    expected_approach,
    time_complexity,
    space_complexity,
    hints,
    interview_points,
    company_tags,
    java_starter_code,
    solution_explanation,
    solution_java_code,
    is_published
)
SELECT
    c.id,
    'Binary Search',
    'binary-search',
    'Given a sorted integer array, find the index of a target value.',
    'EASY',
    'Binary Search',
    'The array is sorted in ascending order.',
    'A sorted integer array and a target integer.',
    'Return the index of the target, or -1 if it is not present.',
    'Use left, right, and middle pointers to repeatedly reduce the search range.',
    'O(log n)',
    'O(1)',
    'Calculate middle and compare arr[middle] with target.',
    'Binary search is a fundamental interview algorithm.',
    'Amazon, Microsoft, TCS, Infosys',
    'public class Solution {
    public static int binarySearch(int[] arr, int target) {
        // Write your solution here
        return -1;
    }
}',
    'If the middle value equals the target, return its index. If the target is greater, search the right half. Otherwise search the left half.',
    'public class Solution {
    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == target) {
                return mid;
            }

            if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }
}',
    TRUE
FROM dsa_categories c
WHERE c.slug = 'searching'
ON CONFLICT (slug) DO NOTHING;


-- =========================================================================
-- 7. RECURSION PROBLEMS
-- =========================================================================

INSERT INTO dsa_problems
(
    category_id,
    title,
    slug,
    description,
    difficulty,
    subtopic,
    constraints,
    input_format,
    output_format,
    expected_approach,
    time_complexity,
    space_complexity,
    hints,
    interview_points,
    company_tags,
    java_starter_code,
    solution_explanation,
    solution_java_code,
    is_published
)
SELECT
    c.id,
    'Factorial Using Recursion',
    'factorial-using-recursion',
    'Calculate the factorial of a non-negative integer using recursion.',
    'EASY',
    'Recursion',
    'The input is a non-negative integer.',
    'An integer n.',
    'Return n factorial.',
    'Use the recurrence n! = n * (n - 1)! with 0! = 1.',
    'O(n)',
    'O(n)',
    'The base case is n <= 1.',
    'Tests understanding of recursion and base cases.',
    'TCS, Infosys, Wipro',
    'public class Solution {
    public static long factorial(int n) {
        // Write your solution here
        return 0;
    }
}',
    'If n is 0 or 1, return 1. Otherwise return n multiplied by factorial(n - 1).',
    'public class Solution {
    public static long factorial(int n) {
        if (n <= 1) {
            return 1;
        }

        return n * factorial(n - 1);
    }
}',
    TRUE
FROM dsa_categories c
WHERE c.slug = 'recursion'
ON CONFLICT (slug) DO NOTHING;


-- =========================================================================
-- 8. SAMPLE TEST CASES
-- =========================================================================

INSERT INTO dsa_test_cases
(
    problem_id,
    input,
    expected_output,
    is_sample,
    is_hidden,
    explanation,
    display_order
)
SELECT
    p.id,
    '5, 2, 9, 1, 7',
    '9',
    TRUE,
    FALSE,
    '9 is the largest element in the array.',
    1
FROM dsa_problems p
WHERE p.slug = 'find-maximum-element'
  AND NOT EXISTS (
      SELECT 1
      FROM dsa_test_cases t
      WHERE t.problem_id = p.id
  );


INSERT INTO dsa_test_cases
(
    problem_id,
    input,
    expected_output,
    is_sample,
    is_hidden,
    explanation,
    display_order
)
SELECT
    p.id,
    '10, 5, 8, 20, 3',
    '8',
    TRUE,
    FALSE,
    '20 is the largest and 10 is the second largest distinct element.',
    1
FROM dsa_problems p
WHERE p.slug = 'find-second-largest-element'
  AND NOT EXISTS (
      SELECT 1
      FROM dsa_test_cases t
      WHERE t.problem_id = p.id
  );


INSERT INTO dsa_test_cases
(
    problem_id,
    input,
    expected_output,
    is_sample,
    is_hidden,
    explanation,
    display_order
)
SELECT
    p.id,
    'hello',
    'olleh',
    TRUE,
    FALSE,
    'The characters are reversed.',
    1
FROM dsa_problems p
WHERE p.slug = 'reverse-a-string'
  AND NOT EXISTS (
      SELECT 1
      FROM dsa_test_cases t
      WHERE t.problem_id = p.id
  );


INSERT INTO dsa_test_cases
(
    problem_id,
    input,
    expected_output,
    is_sample,
    is_hidden,
    explanation,
    display_order
)
SELECT
    p.id,
    'madam',
    'true',
    TRUE,
    FALSE,
    'The string reads the same from both directions.',
    1
FROM dsa_problems p
WHERE p.slug = 'check-palindrome'
  AND NOT EXISTS (
      SELECT 1
      FROM dsa_test_cases t
      WHERE t.problem_id = p.id
  );


INSERT INTO dsa_test_cases
(
    problem_id,
    input,
    expected_output,
    is_sample,
    is_hidden,
    explanation,
    display_order
)
SELECT
    p.id,
    '2,7,11,15; target=9',
    '[0,1]',
    TRUE,
    FALSE,
    'The values 2 and 7 add up to 9.',
    1
FROM dsa_problems p
WHERE p.slug = 'two-sum'
  AND NOT EXISTS (
      SELECT 1
      FROM dsa_test_cases t
      WHERE t.problem_id = p.id
  );


INSERT INTO dsa_test_cases
(
    problem_id,
    input,
    expected_output,
    is_sample,
    is_hidden,
    explanation,
    display_order
)
SELECT
    p.id,
    '5,1,4,2,8',
    '[1,2,4,5,8]',
    TRUE,
    FALSE,
    'The array is sorted in ascending order.',
    1
FROM dsa_problems p
WHERE p.slug = 'bubble-sort'
  AND NOT EXISTS (
      SELECT 1
      FROM dsa_test_cases t
      WHERE t.problem_id = p.id
  );


INSERT INTO dsa_test_cases
(
    problem_id,
    input,
    expected_output,
    is_sample,
    is_hidden,
    explanation,
    display_order
)
SELECT
    p.id,
    '1,3,5,7,9; target=7',
    '3',
    TRUE,
    FALSE,
    '7 is present at index 3.',
    1
FROM dsa_problems p
WHERE p.slug = 'binary-search'
  AND NOT EXISTS (
      SELECT 1
      FROM dsa_test_cases t
      WHERE t.problem_id = p.id
  );


INSERT INTO dsa_test_cases
(
    problem_id,
    input,
    expected_output,
    is_sample,
    is_hidden,
    explanation,
    display_order
)
SELECT
    p.id,
    '5',
    '120',
    TRUE,
    FALSE,
    '5! = 5 × 4 × 3 × 2 × 1 = 120.',
    1
FROM dsa_problems p
WHERE p.slug = 'factorial-using-recursion'
  AND NOT EXISTS (
      SELECT 1
      FROM dsa_test_cases t
      WHERE t.problem_id = p.id
  );