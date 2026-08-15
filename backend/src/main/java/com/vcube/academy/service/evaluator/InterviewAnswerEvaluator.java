package com.vcube.academy.service.evaluator;

import com.vcube.academy.entity.InterviewQuestion;

public interface InterviewAnswerEvaluator {
    InterviewEvaluationResultDto evaluate(InterviewQuestion question, String userAnswer);
}
