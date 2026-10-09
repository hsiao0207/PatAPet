/**
 * Purpose: Data Transfer Object (DTO) for authentication responses. Encapsulates data (JWT Token, User ID, role) returned to the frontend upon successful login or registration.
 * Interactions:
 * - Produced by: AuthService
 * - Returned to: AuthController (which then converts it to JSON to return to the frontend)
 */
package com.patapet.dto;

public record AuthResponse(
        String token, // JWT
        String userId, // UUID 字串，前端可用來組 API 路徑
        String role // PATTER 或 OWNER，前端用來決定顯示哪些 UI
) {
}
