package com.vcube.academy.service;

import com.vcube.academy.dto.career.CopilotChatResponse;
import com.vcube.academy.entity.User;

public interface CareerAIService {
    CopilotChatResponse generateCopilotResponse(User student, String userQuery, Long conversationId);
}
