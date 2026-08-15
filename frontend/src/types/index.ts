// ─── Auth ─────────────────────────────────────────────────────────────────────

export type RoleName = 'STUDENT' | 'TRAINER' | 'ADMIN';

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  userId: number;
  fullName: string;
  email: string;
  roles: RoleName[];
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  phone?: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

// ─── User ─────────────────────────────────────────────────────────────────────

export interface UserDto {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  isActive: boolean;
  roles: RoleName[];
  createdAt: string;
  updatedAt: string;
}

// ─── API Error ────────────────────────────────────────────────────────────────

export interface ApiError {
  status: number;
  error: string;
  message: string;
  path?: string;
  timestamp?: string;
  validationErrors?: Record<string, string>;
}

// ─── Auth Context ─────────────────────────────────────────────────────────────

export interface AuthState {
  user: UserDto | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export interface AuthContextValue extends AuthState {
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshAuth: () => Promise<boolean>;
}
