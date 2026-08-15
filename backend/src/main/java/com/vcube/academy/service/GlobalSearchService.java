package com.vcube.academy.service;

import com.vcube.academy.dto.search.GlobalSearchResultDto;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GlobalSearchService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final DsaProblemRepository dsaProblemRepository;
    private final InterviewTopicRepository interviewTopicRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final PlacementPaperRepository placementPaperRepository;

    @Transactional(readOnly = true)
    public List<GlobalSearchResultDto> search(String rawQuery) {
        List<GlobalSearchResultDto> results = new ArrayList<>();
        if (rawQuery == null || rawQuery.trim().length() < 2) {
            return results;
        }

        String q = rawQuery.trim().toLowerCase();

        // 1. Courses
        courseRepository.findAll().stream()
                .filter(c -> c.getTitle().toLowerCase().contains(q) || (c.getDescription() != null && c.getDescription().toLowerCase().contains(q)))
                .limit(4)
                .forEach(c -> results.add(GlobalSearchResultDto.builder()
                        .id("course-" + c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .category("COURSE")
                        .categoryLabel("Course")
                        .route("/student/courses/" + c.getId())
                        .badge("Java Full Stack")
                        .build()));

        // 2. Topics
        topicRepository.findAll().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(q))
                .limit(5)
                .forEach(t -> results.add(GlobalSearchResultDto.builder()
                        .id("topic-" + t.getId())
                        .title(t.getTitle())
                        .description("Difficulty: " + t.getDifficulty() + " | Duration: " + t.getEstimatedMinutes() + " mins")
                        .category("TOPIC")
                        .categoryLabel("Course Topic")
                        .route("/student/topics/" + t.getId())
                        .badge(t.getDifficulty())
                        .build()));

        // 3. DSA Problems
        dsaProblemRepository.findAll().stream()
                .filter(d -> d.getTitle().toLowerCase().contains(q) || (d.getSubtopic() != null && d.getSubtopic().toLowerCase().contains(q)))
                .limit(5)
                .forEach(d -> results.add(GlobalSearchResultDto.builder()
                        .id("dsa-" + d.getId())
                        .title(d.getTitle())
                        .description("Subtopic: " + (d.getSubtopic() != null ? d.getSubtopic() : "Algorithms") + " | Difficulty: " + d.getDifficulty())
                        .category("DSA")
                        .categoryLabel("DSA Challenge")
                        .route("/student/dsa/problems/" + d.getId())
                        .badge(d.getDifficulty().name())
                        .build()));

        // 4. Interview Topics & Questions
        interviewTopicRepository.findAll().stream()
                .filter(it -> it.getName().toLowerCase().contains(q) || (it.getDescription() != null && it.getDescription().toLowerCase().contains(q)))
                .limit(3)
                .forEach(it -> results.add(GlobalSearchResultDto.builder()
                        .id("itopic-" + it.getId())
                        .title(it.getName())
                        .description(it.getDescription())
                        .category("INTERVIEW_TOPIC")
                        .categoryLabel("Interview Topic")
                        .route("/student/interview")
                        .badge("Interview Q&A")
                        .build()));

        interviewQuestionRepository.findAll().stream()
                .filter(iq -> iq.getQuestionText().toLowerCase().contains(q))
                .limit(5)
                .forEach(iq -> results.add(GlobalSearchResultDto.builder()
                        .id("iquestion-" + iq.getId())
                        .title(iq.getQuestionText())
                        .description("Source: " + iq.getQuestionSource() + " | Difficulty: " + iq.getDifficulty())
                        .category("INTERVIEW_QUESTION")
                        .categoryLabel("Interview Question")
                        .route("/student/interview/questions")
                        .badge(iq.getQuestionSource().name())
                        .build()));

        // 5. Companies
        companyRepository.findAll().stream()
                .filter(comp -> comp.getName().toLowerCase().contains(q))
                .limit(3)
                .forEach(comp -> results.add(GlobalSearchResultDto.builder()
                        .id("company-" + comp.getId())
                        .title(comp.getName())
                        .description(comp.getDescription())
                        .category("COMPANY")
                        .categoryLabel("Company Hub")
                        .route("/student/interview/companies/" + comp.getId())
                        .badge("Hiring Partner")
                        .build()));

        // 6. Jobs
        jobRepository.findAll().stream()
                .filter(j -> j.getTitle().toLowerCase().contains(q) || (j.getCompany() != null && j.getCompany().getName().toLowerCase().contains(q)))
                .limit(4)
                .forEach(j -> {
                    String compName = j.getCompany() != null ? j.getCompany().getName() : "Partner Company";
                    results.add(GlobalSearchResultDto.builder()
                            .id("job-" + j.getId())
                            .title(j.getTitle() + " at " + compName)
                            .description(j.getLocation() + " • " + j.getExperienceLevel())
                            .category("JOB")
                            .categoryLabel("Placement Job")
                            .route("/student/jobs/" + j.getId())
                            .badge(j.getSource().name())
                            .build());
                });

        // 7. Placement Papers
        placementPaperRepository.findAll().stream()
                .filter(pp -> pp.getTitle().toLowerCase().contains(q) || (pp.getCompany() != null && pp.getCompany().getName().toLowerCase().contains(q)))
                .limit(3)
                .forEach(pp -> {
                    String compName = pp.getCompany() != null ? pp.getCompany().getName() : "General Assessment";
                    results.add(GlobalSearchResultDto.builder()
                            .id("paper-" + pp.getId())
                            .title(pp.getTitle())
                            .description(compName + " • Duration: " + pp.getDurationMinutes() + " mins")
                            .category("PLACEMENT_PAPER")
                            .categoryLabel("Placement Exam")
                            .route("/student/placement-papers/" + pp.getId())
                            .badge(pp.getPaperSource().name())
                            .build());
                });

        return results;
    }
}
