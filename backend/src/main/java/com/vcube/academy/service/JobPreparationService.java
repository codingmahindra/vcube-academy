package com.vcube.academy.service;

import com.vcube.academy.dto.job.JobPreparationRecommendationDto;
import com.vcube.academy.entity.Course;
import com.vcube.academy.entity.DsaProblem;
import com.vcube.academy.entity.InterviewQuestion;
import com.vcube.academy.entity.Job;
import com.vcube.academy.repository.CourseRepository;
import com.vcube.academy.repository.DsaProblemRepository;
import com.vcube.academy.repository.InterviewQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPreparationService {

    private final CourseRepository courseRepository;
    private final DsaProblemRepository dsaProblemRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    @Transactional(readOnly = true)
    public JobPreparationRecommendationDto generateRoadmap(Job job) {
        // 1. Recommended Courses
        List<Map<String, Object>> recommendedCourses = courseRepository.findAll().stream()
                .filter(Course::getIsPublished)
                .limit(2)
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId());
                    map.put("title", c.getTitle());
                    map.put("slug", c.getSlug());
                    map.put("difficulty", c.getDifficulty());
                    return map;
                })
                .collect(Collectors.toList());

        // 2. Recommended DSA Problems
        List<Map<String, Object>> recommendedDsa = dsaProblemRepository.findAll().stream()
                .filter(DsaProblem::getIsPublished)
                .limit(3)
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("title", p.getTitle());
                    map.put("difficulty", p.getDifficulty().name());
                    map.put("category", p.getCategory().getName());
                    return map;
                })
                .collect(Collectors.toList());

        // 3. Recommended Interview Questions
        List<Map<String, Object>> recommendedInterviewQ = interviewQuestionRepository.findByCompanyId(job.getCompany().getId()).stream()
                .limit(3)
                .map(q -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", q.getId());
                    map.put("questionText", q.getQuestionText());
                    map.put("difficulty", q.getDifficulty().name());
                    map.put("round", q.getInterviewRound().name());
                    return map;
                })
                .collect(Collectors.toList());

        if (recommendedInterviewQ.isEmpty()) {
            recommendedInterviewQ = interviewQuestionRepository.findAll().stream()
                    .filter(InterviewQuestion::getIsPublished)
                    .limit(3)
                    .map(q -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", q.getId());
                        map.put("questionText", q.getQuestionText());
                        map.put("difficulty", q.getDifficulty().name());
                        map.put("round", q.getInterviewRound().name());
                        return map;
                    })
                    .collect(Collectors.toList());
        }

        // 4. Technical Checklist
        List<String> checklist = new ArrayList<>();
        checklist.add("Master Java 17 Collections Framework & Streams internals");
        checklist.add("Review Spring Boot Dependency Injection, Annotations, & Transaction management");
        checklist.add("Practice SQL indexing, joins, and ACID transactional guarantees");
        checklist.add("Solve 5 Top Tier-1 array/string DSA coding problems");
        checklist.add("Perform a full simulated 1-on-1 Mock Interview for " + job.getCompany().getName());

        return JobPreparationRecommendationDto.builder()
                .recommendedCourses(recommendedCourses)
                .recommendedDsaProblems(recommendedDsa)
                .recommendedInterviewQuestions(recommendedInterviewQ)
                .technicalChecklist(checklist)
                .recommendedMockInterviewRole(job.getTitle())
                .build();
    }
}
