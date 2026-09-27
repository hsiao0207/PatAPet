/**
 * 檔案用途：Spring Security 的核心設定檔，負責設定 API 的存取規則、CORS、CSRF，以及將自訂的 JWT 攔截器加入過濾鍊。
 * 互動關係：
 * - 依賴：JwtFilter (加在 UsernamePasswordAuthenticationFilter 之前)
 * - 保護目標：所有的 Controller API (除了 /api/auth/** 放行之外)
 */
package com.patapet.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 啟用 @PreAuthorize 方法級別權限控制
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 關掉 CSRF（REST API 用 JWT，不需要 CSRF token）
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 無狀態 Session（不建立也不使用 HttpSession）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 路徑授權規則
                .authorizeHttpRequests(auth -> auth
                        // 開放：登入 / 註冊 不需要 Token
                        .requestMatchers("/api/auth/**").permitAll()
                        // 開放：Swagger UI 文件頁面
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // 其他所有路徑都需要驗證
                        .anyRequest().authenticated())
                // 4. 把 JwtFilter 插在 Spring 內建的帳密 Filter 之前
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // BCrypt 密碼雜湊器（注冊時加密密碼，登入時比對用)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
