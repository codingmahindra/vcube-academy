package com.vcube.academy.dto.resume;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeExperienceDto {
    private Long id;
    private String companyName;
    private String roleTitle;
    private String location;
    private String startDate;
    private String endDate;
    private Boolean isCurrent;
    private String description;
    private List<String> bulletPoints;
    private Integer displayOrder;
}
