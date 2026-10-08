/**
 * Purpose: Data Transfer Object (DTO) for registration requests. Receives and validates registration data (email, password, full name) from the frontend.
 * Interactions:
 * - Received from: AuthController (as an @RequestBody parameter)
 * - Passed to: AuthService (to create a new User entity)
 * - Validation Intercept: GlobalExceptionHandler (throws errors if @NotBlank or other validations fail)
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
