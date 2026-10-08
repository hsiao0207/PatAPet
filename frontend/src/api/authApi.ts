/**
 * Purpose: API client library specifically for login and registration.
 * Interactions:
 * - Called by: LoginPage.tsx, RegisterPage.tsx
 * - Calls: http.ts (sends requests via the underlying engine), auth.ts (uses defined data types)
 */
import { apiRequest } from "./http";
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest
} from "../types/auth";

export function register(
  request: RegisterRequest
): Promise<AuthResponse> {
  return apiRequest<AuthResponse>(
    "/api/auth/register",
    {
      method: "POST",
      body: JSON.stringify(request)
    }
  );
}

export function login(
  request: LoginRequest
): Promise<AuthResponse> {
  return apiRequest<AuthResponse>(
    "/api/auth/login",
    {
      method: "POST",
      body: JSON.stringify(request)
    }
  );
}