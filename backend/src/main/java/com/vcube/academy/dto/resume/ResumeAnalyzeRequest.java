package com.vcube.academy.dto.resume;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalyzeRequest {
    private Long versionId;
    private String resumeText;
    private Long jobId;
    private String jobDescriptionText;
    private String targetRole;
    private String targetCompany;
}
