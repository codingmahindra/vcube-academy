package com.vcube.academy.service.execution;

import com.vcube.academy.dto.dsa.CodeExecutionResult;
import com.vcube.academy.entity.DsaTestCase;

import java.util.List;

public interface CodeExecutionService {

    CodeExecutionResult execute(String sourceCode, List<DsaTestCase> testCases);
}
