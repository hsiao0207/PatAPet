/**
 * 檔案用途：登入請求的資料傳輸物件 (DTO)，負責接收前端傳來的登入憑證 (email, 密碼)。
 * 互動關係：
 * - 接收自：AuthController (作為 @RequestBody 參數)
 * - 傳遞給：AuthService (用於比對資料庫的使用者憑證)
 */
package com.patapet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password) {
}
