package com.vcube.academy.dto.resume;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeEducationDto {
    private Long id;
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private String startYear;
    private String endYear;
    private String scoreOrCgpa;
    private Integer displayOrder;
}
