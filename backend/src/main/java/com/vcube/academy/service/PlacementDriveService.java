package com.vcube.academy.service;

import com.vcube.academy.dto.job.PlacementDriveAdminRequest;
import com.vcube.academy.dto.job.PlacementDriveDto;
import com.vcube.academy.entity.Company;
import com.vcube.academy.entity.PlacementDrive;
import com.vcube.academy.entity.PlacementDriveStatus;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.CompanyRepository;
import com.vcube.academy.repository.PlacementDriveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlacementDriveService {

    private final PlacementDriveRepository driveRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<PlacementDriveDto> getActivePlacementDrives() {
        return driveRepository.findAllByOrderByDriveDateAsc(Pageable.unpaged()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlacementDriveDto getPlacementDriveDetail(Long id) {
        PlacementDrive drive = driveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement drive not found with id: " + id));
        return mapToDto(drive);
    }

    @Transactional
    public PlacementDriveDto createPlacementDrive(PlacementDriveAdminRequest req) {
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + req.getCompanyId()));

        PlacementDrive drive = PlacementDrive.builder()
                .company(company)
                .title(req.getTitle())
                .description(req.getDescription())
                .location(req.getLocation())
                .driveDate(req.getDriveDate())
                .registrationDeadline(req.getRegistrationDeadline())
                .packageDetails(req.getPackageDetails())
                .eligibilityCriteria(req.getEligibilityCriteria())
                .selectionProcess(req.getSelectionProcess())
                .applicationLink(req.getApplicationLink())
                .status(req.getStatus() != null ? req.getStatus() : PlacementDriveStatus.UPCOMING)
                .build();

        drive = driveRepository.save(drive);
        return mapToDto(drive);
    }

    @Transactional
    public PlacementDriveDto updatePlacementDrive(Long id, PlacementDriveAdminRequest req) {
        PlacementDrive drive = driveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement drive not found with id: " + id));

        if (req.getCompanyId() != null) {
            Company company = companyRepository.findById(req.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + req.getCompanyId()));
            drive.setCompany(company);
        }

        if (req.getTitle() != null) drive.setTitle(req.getTitle());
        if (req.getDescription() != null) drive.setDescription(req.getDescription());
        if (req.getLocation() != null) drive.setLocation(req.getLocation());
        if (req.getDriveDate() != null) drive.setDriveDate(req.getDriveDate());
        if (req.getRegistrationDeadline() != null) drive.setRegistrationDeadline(req.getRegistrationDeadline());
        if (req.getPackageDetails() != null) drive.setPackageDetails(req.getPackageDetails());
        if (req.getEligibilityCriteria() != null) drive.setEligibilityCriteria(req.getEligibilityCriteria());
        if (req.getSelectionProcess() != null) drive.setSelectionProcess(req.getSelectionProcess());
        if (req.getApplicationLink() != null) drive.setApplicationLink(req.getApplicationLink());
        if (req.getStatus() != null) drive.setStatus(req.getStatus());

        drive = driveRepository.save(drive);
        return mapToDto(drive);
    }

    @Transactional
    public void deletePlacementDrive(Long id) {
        if (!driveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Placement drive not found with id: " + id);
        }
        driveRepository.deleteById(id);
    }

    private PlacementDriveDto mapToDto(PlacementDrive d) {
        return PlacementDriveDto.builder()
                .id(d.getId())
                .companyId(d.getCompany().getId())
                .companyName(d.getCompany().getName())
                .companyLogoUrl(d.getCompany().getLogoUrl())
                .companyTier(d.getCompany().getTier())
                .title(d.getTitle())
                .description(d.getDescription())
                .location(d.getLocation())
                .driveDate(d.getDriveDate())
                .registrationDeadline(d.getRegistrationDeadline())
                .packageDetails(d.getPackageDetails())
                .eligibilityCriteria(d.getEligibilityCriteria())
                .selectionProcess(d.getSelectionProcess())
                .applicationLink(d.getApplicationLink())
                .status(d.getStatus())
                .build();
    }
}
