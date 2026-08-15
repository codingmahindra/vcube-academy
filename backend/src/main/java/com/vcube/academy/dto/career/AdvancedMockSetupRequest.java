package com.vcube.academy.dto.career;

import com.vcube.academy.entity.InterviewDifficulty;
import com.vcube.academy.entity.MockInterviewMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedMockSetupRequest {
    private MockInterviewMode mode;
    private Long companyId;
    private Long jobId;
    private String customJobDescription;
    private InterviewDifficulty difficulty;
    private Integer durationMinutes;
    private Integer questionCount;
    private List<Long> topicIds;
}
