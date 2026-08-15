package com.vcube.academy.dto.resume;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeCertificationDto {
    private Long id;
    private String name;
    private String issuingOrganization;
    private String issueDate;
    private String credentialUrl;
    private Integer displayOrder;
}
