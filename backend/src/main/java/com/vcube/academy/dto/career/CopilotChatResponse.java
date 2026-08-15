package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopilotChatResponse {
    private Long conversationId;
    private String responseText;
    private List<ActionRecommendation> recommendedActions;
    private String aiProvider;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionRecommendation {
        private String label;
        private String actionType; // COURSE, DSA, INTERVIEW, MOCK, RESUME, JOB
        private String link;
    }
}
