/**
 * Purpose: Defines TypeScript type contracts related to Authentication.
 * Interactions:
 * - Imported by: authApi.ts, LoginPage.tsx, RegisterPage.tsx
 */
export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    password: string;
    fullName: string;
}

export interface AuthResponse {
    token: string;
    userId: string;
    role: "PATTER" | "OWNER";
}