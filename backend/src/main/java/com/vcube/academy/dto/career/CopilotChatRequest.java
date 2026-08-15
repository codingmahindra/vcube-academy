package com.vcube.academy.dto.career;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopilotChatRequest {
    private Long conversationId;
    private String message;
}
