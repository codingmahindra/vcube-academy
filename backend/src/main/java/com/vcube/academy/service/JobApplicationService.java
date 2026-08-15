package com.vcube.academy.service;

import com.vcube.academy.dto.job.*;
import com.vcube.academy.entity.*;
import com.vcube.academy.exception.BadRequestException;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobPortalService jobPortalService;

    @Transactional
    public JobApplicationDto createApplication(JobApplicationRequest request, Long userId) {
        Job job = jobRepository.findById(request.getJobId())
                .filter(Job::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.getJobId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (applicationRepository.existsByUserIdAndJobId(userId, request.getJobId())) {
            JobApplication existing = applicationRepository.findByUserIdAndJobId(userId, request.getJobId()).orElseThrow();
            return updateApplication(existing.getId(), request, userId);
        }

        ApplicationStatus initialStatus = request.getStatus() != null ? request.getStatus() : ApplicationStatus.APPLIED;

        JobApplication app = JobApplication.builder()
                .user(user)
                .job(job)
                .status(initialStatus)
                .appliedDate(Instant.now())
                .notes(request.getNotes())
                .nextAction(request.getNextAction())
                .interviewDate(request.getInterviewDate())
                .build();

        app = applicationRepository.save(app);

        // Record initial status history
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(app)
                .previousStatus(null)
                .newStatus(initialStatus)
                .notes(request.getNotes() != null ? request.getNotes() : "Application recorded")
                .build();
        historyRepository.save(history);

        return mapToDto(app, userId);
    }

    @Transactional
    public JobApplicationDto updateApplication(Long applicationId, JobApplicationRequest request, Long userId) {
        JobApplication app = applicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        ApplicationStatus oldStatus = app.getStatus();
        ApplicationStatus newStatus = request.getStatus() != null ? request.getStatus() : oldStatus;

        if (oldStatus != newStatus) {
            ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                    .application(app)
                    .previousStatus(oldStatus)
                    .newStatus(newStatus)
                    .notes(request.getNotes())
                    .build();
            historyRepository.save(history);
            app.setStatus(newStatus);
        }

        if (request.getNotes() != null) app.setNotes(request.getNotes());
        if (request.getNextAction() != null) app.setNextAction(request.getNextAction());
        if (request.getInterviewDate() != null) app.setInterviewDate(request.getInterviewDate());

        app = applicationRepository.save(app);
        return mapToDto(app, userId);
    }

    @Transactional
    public void deleteApplication(Long applicationId, Long userId) {
        JobApplication app = applicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        applicationRepository.delete(app);
    }

    @Transactional(readOnly = true)
    public Page<JobApplicationDto> getApplications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return applicationRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable)
                .map(app -> mapToDto(app, userId));
    }

    @Transactional(readOnly = true)
    public JobApplicationDto getApplicationDetail(Long applicationId, Long userId) {
        JobApplication app = applicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        return mapToDto(app, userId);
    }

    @Transactional(readOnly = true)
    public ApplicationDashboardDto getApplicationDashboard(Long userId) {
        long total = applicationRepository.countByUserId(userId);
        long applied = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.APPLIED);
        long assessment = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.ASSESSMENT);
        long interview = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.INTERVIEW);
        long offer = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.OFFER);
        long rejected = applicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.REJECTED);

        List<JobApplication> allApps = applicationRepository.findByUserId(userId);

        List<JobApplicationDto> upcomingInterviews = allApps.stream()
                .filter(a -> a.getInterviewDate() != null && a.getInterviewDate().isAfter(Instant.now()))
                .sorted(Comparator.comparing(JobApplication::getInterviewDate))
                .limit(5)
                .map(a -> mapToDto(a, userId))
                .collect(Collectors.toList());

        List<JobApplicationDto> recent = allApps.stream()
                .sorted(Comparator.comparing(JobApplication::getUpdatedAt).reversed())
                .limit(5)
                .map(a -> mapToDto(a, userId))
                .collect(Collectors.toList());

        return ApplicationDashboardDto.builder()
                .totalApplications(total)
                .appliedCount(applied)
                .assessmentCount(assessment)
                .interviewCount(interview)
                .offerCount(offer)
                .rejectedCount(rejected)
                .upcomingInterviews(upcomingInterviews)
                .recentApplications(recent)
                .build();
    }

    private JobApplicationDto mapToDto(JobApplication app, Long userId) {
        List<ApplicationStatusHistoryDto> histories = app.getStatusHistories().stream()
                .map(h -> ApplicationStatusHistoryDto.builder()
                        .id(h.getId())
                        .previousStatus(h.getPreviousStatus())
                        .newStatus(h.getNewStatus())
                        .changedAt(h.getChangedAt())
                        .notes(h.getNotes())
                        .build())
                .collect(Collectors.toList());

        return JobApplicationDto.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .job(jobPortalService.mapToSummaryDto(app.getJob(), userId))
                .status(app.getStatus())
                .appliedDate(app.getAppliedDate())
                .notes(app.getNotes())
                .nextAction(app.getNextAction())
                .interviewDate(app.getInterviewDate())
                .statusHistories(histories)
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
