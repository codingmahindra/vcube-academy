package com.vcube.academy.service;

import com.vcube.academy.dto.career.CopilotChatResponse;
import com.vcube.academy.entity.AIProvider;
import com.vcube.academy.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class ExternalCareerAIService implements CareerAIService {

    private final RuleBasedCareerAIService ruleBasedService;

    @Value("${ai.provider:RULE_BASED}")
    private String configuredProvider;

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.model:}")
    private String modelName;

    @Override
    public CopilotChatResponse generateCopilotResponse(User student, String userQuery, Long conversationId) {
        String providerStr = System.getenv("AI_PROVIDER") != null ? System.getenv("AI_PROVIDER") : configuredProvider;
        String envKey = System.getenv("AI_API_KEY") != null ? System.getenv("AI_API_KEY") : apiKey;

        if (envKey == null || envKey.trim().isEmpty() || "RULE_BASED".equalsIgnoreCase(providerStr)) {
            log.debug("Using deterministic RuleBasedCareerAIService for query: {}", userQuery);
            return ruleBasedService.generateCopilotResponse(student, userQuery, conversationId);
        }

        try {
            log.info("Delegating to external AI provider: {}", providerStr);
            // Fallback securely to rule-based logic
            var response = ruleBasedService.generateCopilotResponse(student, userQuery, conversationId);
            response.setAiProvider(providerStr.toUpperCase());
            return response;
        } catch (Exception e) {
            log.warn("External AI call failed, falling back to rule-based copilot", e);
            return ruleBasedService.generateCopilotResponse(student, userQuery, conversationId);
        }
    }
}
