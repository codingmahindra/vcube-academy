package com.vcube.academy.service;

import com.vcube.academy.dto.career.CompanyPrepHubDto;
import com.vcube.academy.dto.career.PlacementPaperSummaryDto;
import com.vcube.academy.dto.dsa.DsaProblemSummaryDto;
import com.vcube.academy.dto.interview.InterviewQuestionSummaryDto;
import com.vcube.academy.entity.Company;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyPrepService {

    private final CompanyRepository companyRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final PlacementPaperRepository placementPaperRepository;
    private final DsaProblemRepository dsaProblemRepository;

    public CompanyPrepHubDto getCompanyPrep(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));

        List<InterviewQuestionSummaryDto> questions = interviewQuestionRepository.findByCompanyId(companyId).stream().map(q ->
                InterviewQuestionSummaryDto.builder()
                        .id(q.getId())
                        .topicId(q.getTopic() != null ? q.getTopic().getId() : null)
                        .topicName(q.getTopic() != null ? q.getTopic().getName() : "General")
                        .categoryId(q.getTopic() != null && q.getTopic().getCategory() != null ? q.getTopic().getCategory().getId() : null)
                        .categoryName(q.getTopic() != null && q.getTopic().getCategory() != null ? q.getTopic().getCategory().getName() : "General")
                        .questionText(q.getQuestionText())
                        .questionType(q.getQuestionType())
                        .difficulty(q.getDifficulty())
                        .interviewRound(q.getInterviewRound())
                        .questionSource(q.getQuestionSource())
                        .build()
        ).toList();

        var papers = placementPaperRepository.findByCompanyIdAndIsActiveTrue(companyId).stream().map(p ->
                PlacementPaperSummaryDto.builder()
                        .id(p.getId())
                        .companyId(company.getId())
                        .companyName(company.getName())
                        .title(p.getTitle())
                        .slug(p.getSlug())
                        .year(p.getYear())
                        .targetRole(p.getTargetRole())
                        .roundName(p.getRoundName())
                        .durationMinutes(p.getDurationMinutes())
                        .totalMarks(p.getTotalMarks())
                        .passingMarks(p.getPassingMarks())
                        .difficulty(p.getDifficulty())
                        .paperSource(p.getPaperSource())
                        .questionCount(p.getQuestions().size())
                        .build()
        ).toList();

        var dsaProblems = dsaProblemRepository.findAll().stream().limit(3).map(p ->
                DsaProblemSummaryDto.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .slug(p.getSlug())
                        .difficulty(p.getDifficulty())
                        .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                        .categoryName(p.getCategory() != null ? p.getCategory().getName() : "General")
                        .subtopic(p.getSubtopic())
                        .build()
        ).toList();

        int verifiedCount = (int) questions.stream().filter(q -> "VERIFIED_COMPANY_QUESTION".equals(q.getQuestionSource().name())).count();
        int reportedCount = (int) questions.stream().filter(q -> "REPORTED_PLACEMENT_QUESTION".equals(q.getQuestionSource().name())).count();

        return CompanyPrepHubDto.builder()
                .companyId(company.getId())
                .companyName(company.getName())
                .slug(company.getSlug())
                .industry(company.getIndustry())
                .description(company.getDescription())
                .hiringRounds(List.of("Round 1: Cognitive + Technical OA", "Round 2: Technical Interview (Core Java & DSA)", "Round 3: Techno-Managerial & HR"))
                .frequentlyTestedSkills(List.of("Java 17", "Spring Boot", "PostgreSQL", "Data Structures", "Multithreading"))
                .verifiedQuestionCount(verifiedCount)
                .reportedQuestionCount(reportedCount)
                .companyQuestions(questions)
                .placementPapers(papers)
                .recommendedDsaProblems(dsaProblems)
                .build();
    }
}
