package com.vcube.academy.dto.dsa;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsaSubmissionRequest {

    @NotBlank(message = "Source code is required")
    private String sourceCode;

    @Builder.Default
    private String language = "JAVA";
}
