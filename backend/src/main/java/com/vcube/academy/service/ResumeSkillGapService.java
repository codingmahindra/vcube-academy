package com.vcube.academy.service;

import com.vcube.academy.dto.resume.ResumeMissingSkillDto;
import com.vcube.academy.entity.*;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeSkillGapService {

    private final CourseRepository courseRepository;
    private final DsaProblemRepository dsaProblemRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    @Transactional(readOnly = true)
    public ResumeMissingSkillDto resolveSkillGap(String skillName, KeywordCategory category, String importance) {
        String skillLower = skillName.toLowerCase().trim();

        // 1. Resolve Course
        Course matchedCourse = null;
        List<Course> courses = courseRepository.findAll();
        for (Course c : courses) {
            if (c.getIsPublished() && (c.getTitle().toLowerCase().contains(skillLower) || c.getDescription().toLowerCase().contains(skillLower))) {
                matchedCourse = c;
                break;
            }
        }
        if (matchedCourse == null && !courses.isEmpty()) {
            matchedCourse = courses.get(0);
        }

        // 2. Resolve DSA problem if category is technical/algorithms
        DsaProblem matchedDsa = null;
        if (category == KeywordCategory.TECHNICAL_SKILL || skillLower.contains("dsa") || skillLower.contains("algorithm") || skillLower.contains("array") || skillLower.contains("string") || skillLower.contains("tree")) {
            List<DsaProblem> problems = dsaProblemRepository.findAll();
            for (DsaProblem p : problems) {
                if (p.getIsPublished() && (p.getTitle().toLowerCase().contains(skillLower) || p.getCategory().getName().toLowerCase().contains(skillLower))) {
                    matchedDsa = p;
                    break;
                }
            }
            if (matchedDsa == null && !problems.isEmpty()) {
                matchedDsa = problems.get(0);
            }
        }

        // 3. Resolve Interview Question
        InterviewQuestion matchedQuestion = null;
        List<InterviewQuestion> questions = interviewQuestionRepository.findAll();
        for (InterviewQuestion q : questions) {
            if (q.getIsPublished() && (q.getQuestionText().toLowerCase().contains(skillLower) || (q.getTopic() != null && q.getTopic().getName().toLowerCase().contains(skillLower)))) {
                matchedQuestion = q;
                break;
            }
        }
        if (matchedQuestion == null && !questions.isEmpty()) {
            matchedQuestion = questions.get(0);
        }

        // 4. Build Course map
        Map<String, Object> courseMap = null;
        if (matchedCourse != null) {
            courseMap = new HashMap<>();
            courseMap.put("id", matchedCourse.getId());
            courseMap.put("title", matchedCourse.getTitle());
            courseMap.put("slug", matchedCourse.getSlug());
            courseMap.put("difficulty", matchedCourse.getDifficulty());
        }

        // 5. Build DSA map
        Map<String, Object> dsaMap = null;
        if (matchedDsa != null) {
            dsaMap = new HashMap<>();
            dsaMap.put("id", matchedDsa.getId());
            dsaMap.put("title", matchedDsa.getTitle());
            dsaMap.put("slug", matchedDsa.getSlug());
            dsaMap.put("difficulty", matchedDsa.getDifficulty().name());
            dsaMap.put("category", matchedDsa.getCategory().getName());
        }

        // 6. Build Interview Question map
        Map<String, Object> questionMap = null;
        if (matchedQuestion != null) {
            questionMap = new HashMap<>();
            questionMap.put("id", matchedQuestion.getId());
            questionMap.put("questionText", matchedQuestion.getQuestionText());
            questionMap.put("difficulty", matchedQuestion.getDifficulty().name());
            questionMap.put("round", matchedQuestion.getInterviewRound().name());
        }

        String whyItMatters = "Mastering " + skillName + " is a core prerequisite for production Java Full Stack systems and frequently tested in technical screening rounds.";

        return ResumeMissingSkillDto.builder()
                .skillName(skillName)
                .category(category)
                .importance(importance)
                .whyItMatters(whyItMatters)
                .recommendedCourse(courseMap)
                .recommendedDsaProblem(dsaMap)
                .recommendedInterviewQuestion(questionMap)
                .recommendedMockRole("Java Backend Engineer")
                .build();
    }
}
