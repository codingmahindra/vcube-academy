package com.vcube.academy.service;

import com.vcube.academy.dto.resume.ResumeAnalysisDto;
import com.vcube.academy.dto.resume.ResumeAnalyzeRequest;
import com.vcube.academy.dto.resume.ResumeOptimizationDto;
import com.vcube.academy.entity.ResumeVersion;

public interface ResumeAIService {
    ResumeAnalysisDto analyzeResume(ResumeAnalyzeRequest request, ResumeVersion version);
    ResumeOptimizationDto generateOptimizationSuggestions(ResumeVersion version, String jobDescription);
}
