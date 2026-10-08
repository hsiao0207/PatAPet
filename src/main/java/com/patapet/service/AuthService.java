/**
 * Purpose: Responsible for core business logic related to authentication, including password hashing, account registration, credential verification, and JWT issuance.
 * Interactions:
 * - Called by: AuthController
 * - Dependencies: UserRepository (query and save users), JwtTokenProvider (generate Tokens), PasswordEncoder (password encryption and matching)
 */
package com.patapet.service;

import com.patapet.dto.AuthResponse;
import com.patapet.dto.LoginRequest;
import com.patapet.dto.RegisterRequest;
import com.patapet.entity.User;
import com.patapet.repository.UserRepository;
import com.patapet.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // -----------------------------------------------------------------------
    // 註冊
    // -----------------------------------------------------------------------
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        // 1. 檢查 email 是否重複
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("此 Email 已被註冊");
        }
        // 2. 建立新使用者，密碼用 BCrypt 雜湊後再存
        User user = User.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .role(User.Role.PATTER) // 預設身份為 PATTER
                .build();
        User saved = userRepository.save(user);
        // 3. 簽發 JWT，userId 當 subject
        String token = jwtTokenProvider.generateToken(
                saved.getId().toString(),
                saved.getRole().name());
        return new AuthResponse(token, saved.getId().toString(), saved.getRole().name());
    }

    // -----------------------------------------------------------------------
    // 登入
    // -----------------------------------------------------------------------
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        // 1. 用 email 找使用者，找不到就回 401 語意的例外
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Email 或密碼錯誤"));
        // 2. 驗證密碼（BCrypt 比對）
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email 或密碼錯誤");
        }
        // 3. 簽發 JWT
        String token = jwtTokenProvider.generateToken(
                user.getId().toString(),
                user.getRole().name());
        return new AuthResponse(token, user.getId().toString(), user.getRole().name());
    }
}
