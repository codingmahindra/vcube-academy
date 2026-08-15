import React, {
  createContext,
  useCallback,
  useEffect,
  useState,
} from 'react';
import { authApi } from '../api/auth';
import { tokenStorage } from '../utils/tokenStorage';
import type {
  AuthContextValue,
  AuthState,
  LoginRequest,
  RegisterRequest,
  UserDto,
} from '../types';

export const AuthContext = createContext<AuthContextValue | null>(null);

const initialState: AuthState = {
  user: null,
  accessToken: tokenStorage.getAccessToken(),
  refreshToken: tokenStorage.getRefreshToken(),
  isAuthenticated: false,
  isLoading: true,
};

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>(initialState);

  // ─── Restore session on mount ────────────────────────────────────────────
  useEffect(() => {
    const token = tokenStorage.getAccessToken();
    if (!token) {
      setState((s) => ({ ...s, isLoading: false }));
      return;
    }
    authApi
      .me()
      .then((user: UserDto) => {
        setState({
          user,
          accessToken: tokenStorage.getAccessToken(),
          refreshToken: tokenStorage.getRefreshToken(),
          isAuthenticated: true,
          isLoading: false,
        });
      })
      .catch(() => {
        tokenStorage.clearTokens();
        setState({
          user: null,
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
          isLoading: false,
        });
      });
  }, []);

  // ─── Login ───────────────────────────────────────────────────────────────
  const login = useCallback(async (data: LoginRequest) => {
    const auth = await authApi.login(data);
    tokenStorage.setTokens(auth.accessToken, auth.refreshToken);
    const user = await authApi.me();
    setState({
      user,
      accessToken: auth.accessToken,
      refreshToken: auth.refreshToken,
      isAuthenticated: true,
      isLoading: false,
    });
  }, []);

  // ─── Register ────────────────────────────────────────────────────────────
  const register = useCallback(async (data: RegisterRequest) => {
    const auth = await authApi.register(data);
    tokenStorage.setTokens(auth.accessToken, auth.refreshToken);
    const user = await authApi.me();
    setState({
      user,
      accessToken: auth.accessToken,
      refreshToken: auth.refreshToken,
      isAuthenticated: true,
      isLoading: false,
    });
  }, []);

  // ─── Logout ──────────────────────────────────────────────────────────────
  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } catch {
      // ignore — clear locally regardless
    } finally {
      tokenStorage.clearTokens();
      setState({
        user: null,
        accessToken: null,
        refreshToken: null,
        isAuthenticated: false,
        isLoading: false,
      });
    }
  }, []);

  // ─── Silent refresh ──────────────────────────────────────────────────────
  const refreshAuth = useCallback(async (): Promise<boolean> => {
    const rt = tokenStorage.getRefreshToken();
    if (!rt) return false;
    try {
      const auth = await authApi.refresh({ refreshToken: rt });
      tokenStorage.setTokens(auth.accessToken, auth.refreshToken);
      const user = await authApi.me();
      setState((s) => ({
        ...s,
        user,
        accessToken: auth.accessToken,
        refreshToken: auth.refreshToken,
        isAuthenticated: true,
      }));
      return true;
    } catch {
      tokenStorage.clearTokens();
      setState({
        user: null,
        accessToken: null,
        refreshToken: null,
        isAuthenticated: false,
        isLoading: false,
      });
      return false;
    }
  }, []);

  const value: AuthContextValue = {
    ...state,
    login,
    register,
    logout,
    refreshAuth,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
