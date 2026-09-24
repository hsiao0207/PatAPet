/**
 * 檔案用途：認證與授權的 Controller，負責暴露 /api/auth 下的 RESTful API 端點。
 * 互動關係：
 * - 接收自：前端發送的 HTTP 請求 (RegisterRequest, LoginRequest)
 * - 依賴：AuthService (處理註冊、登入邏輯並核發 JWT)
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
