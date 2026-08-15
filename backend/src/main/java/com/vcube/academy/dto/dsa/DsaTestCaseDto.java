package com.vcube.academy.dto.dsa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsaTestCaseDto {
    private Long id;
    private String input;
    private String expectedOutput;
    private Boolean isSample;
    private Boolean isHidden;
    private String explanation;
}
