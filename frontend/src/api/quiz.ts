import apiClient from './client';
import type {
  QuizAttemptDto,
  QuestionDto,
  AnswerFeedbackDto,
  QuizResultDto,
  StartQuizRequest,
  SubmitAnswerRequest,
} from '../types/quiz';

export const quizApi = {
  /** Start a new quiz attempt */
  start: (request: StartQuizRequest) =>
    apiClient.post<QuizAttemptDto>('/quiz/start', request).then((r) => r.data),

  /** Get the current question for an attempt */
  getCurrentQuestion: (attemptId: number) =>
    apiClient.get<QuestionDto>(`/quiz/${attemptId}/question`).then((r) => r.data),

  /** Submit an answer for the current question */
  submitAnswer: (attemptId: number, request: SubmitAnswerRequest) =>
    apiClient
      .post<AnswerFeedbackDto>(`/quiz/${attemptId}/answer`, request)
      .then((r) => r.data),

  /** Complete the quiz and get the result */
  complete: (attemptId: number) =>
    apiClient.post<QuizResultDto>(`/quiz/${attemptId}/complete`).then((r) => r.data),

  /** Fetch the result of a completed quiz */
  getResult: (attemptId: number) =>
    apiClient.get<QuizResultDto>(`/quiz/${attemptId}/result`).then((r) => r.data),
};
