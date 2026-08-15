package com.vcube.academy.service;

import com.vcube.academy.dto.dsa.DsaCategoryDto;
import com.vcube.academy.dto.dsa.DsaProgressSummaryDto;
import com.vcube.academy.entity.DsaDifficulty;
import com.vcube.academy.repository.DsaProblemRepository;
import com.vcube.academy.repository.DsaStudentProgressRepository;
import com.vcube.academy.repository.DsaSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DsaProgressService {

    private final DsaProblemRepository problemRepository;
    private final DsaStudentProgressRepository progressRepository;
    private final DsaSubmissionRepository submissionRepository;
    private final DsaProblemService problemService;

    @Transactional(readOnly = true)
    public DsaProgressSummaryDto getStudentDsaProgress(Long userId) {
        long totalProblems = problemRepository.countByIsPublishedTrue();
        long solvedProblems = progressRepository.countByUserIdAndIsSolvedTrue(userId);
        long attemptedProblems = progressRepository.countByUserId(userId);

        long easyTotal = problemRepository.countByIsPublishedTrueAndDifficulty(DsaDifficulty.EASY);
        long mediumTotal = problemRepository.countByIsPublishedTrueAndDifficulty(DsaDifficulty.MEDIUM);
        long hardTotal = problemRepository.countByIsPublishedTrueAndDifficulty(DsaDifficulty.HARD);

        long easySolved = progressRepository.countSolvedByUserIdAndDifficulty(userId, DsaDifficulty.EASY);
        long mediumSolved = progressRepository.countSolvedByUserIdAndDifficulty(userId, DsaDifficulty.MEDIUM);
        long hardSolved = progressRepository.countSolvedByUserIdAndDifficulty(userId, DsaDifficulty.HARD);

        long totalSubmissions = submissionRepository.countByUserId(userId);

        BigDecimal successRate = (attemptedProblems > 0)
                ? BigDecimal.valueOf(solvedProblems * 100.0 / attemptedProblems).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<DsaCategoryDto> categoryProgress = problemService.getCategories(userId);

        return DsaProgressSummaryDto.builder()
                .totalProblems(totalProblems)
                .solvedProblems(solvedProblems)
                .attemptedProblems(attemptedProblems)
                .easySolved(easySolved)
                .easyTotal(easyTotal)
                .mediumSolved(mediumSolved)
                .mediumTotal(mediumTotal)
                .hardSolved(hardSolved)
                .hardTotal(hardTotal)
                .successRate(successRate)
                .totalSubmissions(totalSubmissions)
                .categoryProgress(categoryProgress)
                .build();
    }
}
