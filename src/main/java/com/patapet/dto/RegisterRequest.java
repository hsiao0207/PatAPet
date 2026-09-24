/**
 * 檔案用途：註冊請求的資料傳輸物件 (DTO)，負責接收並驗證前端傳來的註冊資料 (email, 密碼, 姓名)。
 * 互動關係：
 * - 接收自：AuthController (作為 @RequestBody 參數)
 * - 傳遞給：AuthService (用於建立新 User 實體)
 * - 驗證攔截：GlobalExceptionHandler (若 @NotBlank 等驗證失敗，會拋出錯誤)
 */
package com.patapet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email 不可為空") @Email(message = "Email 格式不正確") String email,
        @NotBlank(message = "密碼不可為空") @Size(min = 8, message = "密碼至少 8 個字元") String password,
        @NotBlank(message = "姓名不可為空") @Size(max = 100, message = "姓名不可超過 100 字元") String fullName) {
}
