/**
 * 檔案用途：JWT 攔截器，負責攔截每個 HTTP 請求，從 Authorization Header 取出 Token 並驗證，若有效則將使用者身分放入 SecurityContext。
 * 互動關係：
 * - 被註冊於：SecurityConfig
 * - 依賴：JwtTokenProvider (用來解析與驗證 Token)
 * - 影響範圍：所有需要權限的 Controller API
 */
package com.patapet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        // 1. 從 Authorization Header 取出 Token
        String token = extractToken(request);
        // 2. Token 存在且有效，設定 Authentication 進 SecurityContext
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token); // 從 token 直接取 role，不查 DB
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, // principal（UUID 字串）
                    null, // credentials（不需要）
                    List.of(new SimpleGrantedAuthority("ROLE_" + role)) // e.g. ROLE_PATTER / ROLE_OWNER
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("JWT 驗證成功，userId: {}, role: {}", userId, role);
        }
        // 3. 繼續往下傳給下一個 Filter 或 Controller
        filterChain.doFilter(request, response);
    }

    // ---------------------------------------------------------------------------
    // 從 "Authorization: Bearer <token>" Header 取出 token 字串
    // ---------------------------------------------------------------------------
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 去掉 "Bearer " 前綴（7個字元）
        }
        return null;
    }
}
