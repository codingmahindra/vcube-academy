package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopilotConversationSummaryDto {
    private Long id;
    private String title;
    private int messageCount;
    private Instant createdAt;
    private Instant updatedAt;
}
