package com.vcube.academy.service;

import com.vcube.academy.dto.job.SavedJobDto;
import com.vcube.academy.entity.Job;
import com.vcube.academy.entity.SavedJob;
import com.vcube.academy.entity.User;
import com.vcube.academy.exception.BadRequestException;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.JobRepository;
import com.vcube.academy.repository.SavedJobRepository;
import com.vcube.academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobPortalService jobPortalService;

    @Transactional
    public SavedJobDto saveJob(Long jobId, Long userId) {
        if (savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            SavedJob existing = savedJobRepository.findByUserIdAndJobId(userId, jobId).orElseThrow();
            return mapToDto(existing, userId);
        }

        Job job = jobRepository.findById(jobId)
                .filter(Job::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SavedJob saved = SavedJob.builder()
                .user(user)
                .job(job)
                .build();

        saved = savedJobRepository.save(saved);
        return mapToDto(saved, userId);
    }

    @Transactional
    public void unsaveJob(Long jobId, Long userId) {
        savedJobRepository.deleteByUserIdAndJobId(userId, jobId);
    }

    @Transactional(readOnly = true)
    public Page<SavedJobDto> getSavedJobs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return savedJobRepository.findByUserIdOrderBySavedAtDesc(userId, pageable)
                .map(s -> mapToDto(s, userId));
    }

    private SavedJobDto mapToDto(SavedJob s, Long userId) {
        return SavedJobDto.builder()
                .id(s.getId())
                .jobId(s.getJob().getId())
                .job(jobPortalService.mapToSummaryDto(s.getJob(), userId))
                .savedAt(s.getSavedAt())
                .build();
    }
}
