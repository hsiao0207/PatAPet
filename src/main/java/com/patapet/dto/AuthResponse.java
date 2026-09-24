/**
 * 檔案用途：認證回應的資料傳輸物件 (DTO)，負責封裝登入或註冊成功後要回傳給前端的資料 (JWT Token, User ID, 角色)。
 * 互動關係：
 * - 產生於：AuthService
 * - 回傳至：AuthController (再轉為 JSON 回傳給前端)
 */
package com.patapet.dto;

public record AuthResponse(
        String token, // JWT
        String userId, // UUID 字串，前端可用來組 API 路徑
        String role // PATTER 或 OWNER，前端用來決定顯示哪些 UI
) {
}
