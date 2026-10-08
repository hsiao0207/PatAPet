/**
 * Purpose: Data Transfer Object (DTO) for login requests. Receives login credentials (email, password) from the frontend.
 * Interactions:
 * - Received from: AuthController (as an @RequestBody parameter)
 * - Passed to: AuthService (to verify user credentials against the database)
 */
package com.patapet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password) {
}
