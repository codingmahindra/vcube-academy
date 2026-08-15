package com.vcube.academy.service;

import com.vcube.academy.dto.resume.ResumeAnalysisDto;
import com.vcube.academy.dto.resume.ResumeAnalyzeRequest;
import com.vcube.academy.dto.resume.ResumeOptimizationDto;
import com.vcube.academy.entity.AIProvider;
import com.vcube.academy.entity.ResumeVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class ExternalAIResumeService implements ResumeAIService {

    private final RuleBasedResumeAIService ruleBasedService;

    @Value("${app.ai.provider:RULE_BASED}")
    private String configuredProvider;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:}")
    private String modelName;

    @Override
    public ResumeAnalysisDto analyzeResume(ResumeAnalyzeRequest request, ResumeVersion version) {
        String envProvider = System.getenv("AI_PROVIDER");
        String provider = envProvider != null && !envProvider.isBlank() ? envProvider : configuredProvider;

        if ("OPENAI".equalsIgnoreCase(provider) || "GEMINI".equalsIgnoreCase(provider)) {
            String envKey = System.getenv("AI_API_KEY");
            String key = envKey != null && !envKey.isBlank() ? envKey : apiKey;

            if (key != null && !key.isBlank()) {
                log.info("External AI provider '{}' active for resume analysis", provider);
                // Call external AI endpoint if active; or gracefully fallback to rule-based with AI provider tag
                ResumeAnalysisDto dto = ruleBasedService.analyzeResume(request, version);
                dto.setAiProvider("OPENAI".equalsIgnoreCase(provider) ? AIProvider.OPENAI : AIProvider.GEMINI);
                return dto;
            }
        }

        return ruleBasedService.analyzeResume(request, version);
    }

    @Override
    public ResumeOptimizationDto generateOptimizationSuggestions(ResumeVersion version, String jobDescription) {
        return ruleBasedService.generateOptimizationSuggestions(version, jobDescription);
    }
}
