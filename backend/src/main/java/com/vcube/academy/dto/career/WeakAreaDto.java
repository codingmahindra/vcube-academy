package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeakAreaDto {
    private Long id;
    private String skillOrTopicName;
    private String category;
    private int weaknessScore; // 0-100
    private String sourceModule;
    private String recommendationText;
    private String actionLink;
}
