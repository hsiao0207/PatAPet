package com.patapet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long expirationMs;

    // 從 application.yml 讀取 jwt.secret 和 jwt.expiration-ms
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        // JJWT 0.12 推薦：把 secret string 轉成 HMAC-SHA 用的 SecretKey
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    // -----------------------------------------------------------------------
    // 簽發 Token（登入成功後呼叫）
    // -----------------------------------------------------------------------
    public String generateToken(String userId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(userId) // sub = userId（UUID 字串，不可變的主鍵）
                .claim("role", role) // 自訂 claim 放 role
                .issuedAt(now) // iat = 簽發時間
                .expiration(expiry) // exp = 到期時間
                .signWith(key) // HS256 簽名
                .compact(); // 產出最終 Token 字串
    }

    // -----------------------------------------------------------------------
    // 從 Token 取出 userId（subject）
    // -----------------------------------------------------------------------
    public String getUserIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // -----------------------------------------------------------------------
    // 從 Token 取出 role
    // -----------------------------------------------------------------------
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }


    // -----------------------------------------------------------------------
    // 驗證 Token 是否有效（簽名正確 + 未過期）
    // -----------------------------------------------------------------------
    public boolean validateToken(String token) {
        try {
            parseClaims(token); // 只要不拋例外就代表有效
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT 已過期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT 格式不支援: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT 格式錯誤: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT 簽名驗證失敗: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 內容為空: {}", e.getMessage());
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // 私有方法：統一解析 Claims（payload）
    // -----------------------------------------------------------------------
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key) // 用同一把 key 驗簽
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
