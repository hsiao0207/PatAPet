/**
 * Purpose: Controller for authentication and authorization. Exposes RESTful API endpoints under /api/auth.
 * Interactions:
 * - Receives from: HTTP requests sent by the frontend (RegisterRequest, LoginRequest)
 * - Dependencies: AuthService (handles registration, login logic, and JWT issuance)
 */
package com.patapet.controller;

import com.patapet.dto.AuthResponse;
import com.patapet.dto.LoginRequest;
import com.patapet.dto.RegisterRequest;
import com.patapet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse response = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse response = authService.login(req);
        return ResponseEntity.ok(response); // 200
    }
}
