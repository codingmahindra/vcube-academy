package com.vcube.academy.dto.resume;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeProjectDto {
    private Long id;
    private String title;
    private String techStack;
    private String liveUrl;
    private String githubUrl;
    private String description;
    private List<String> bulletPoints;
    private Integer displayOrder;
}
