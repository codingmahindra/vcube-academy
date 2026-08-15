import apiClient from './client';
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  RefreshTokenRequest,
  UserDto,
} from '../types';

export const authApi = {
  register: (data: RegisterRequest) =>
    apiClient.post<AuthResponse>('/auth/register', data).then((r) => r.data),

  login: (data: LoginRequest) =>
    apiClient.post<AuthResponse>('/auth/login', data).then((r) => r.data),

  refresh: (data: RefreshTokenRequest) =>
    apiClient.post<AuthResponse>('/auth/refresh', data).then((r) => r.data),

  logout: () =>
    apiClient.post<void>('/auth/logout').then((r) => r.data),

  me: () =>
    apiClient.get<UserDto>('/auth/me').then((r) => r.data),
};
