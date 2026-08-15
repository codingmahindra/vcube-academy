package com.vcube.academy.dto.interview;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAdminRequest {
    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Slug is required")
    private String slug;

    private String logoUrl;
    private String description;
    private String industry;
    private String tier;
    private String hiringRoundsInfo;
    private Boolean isActive;
}
