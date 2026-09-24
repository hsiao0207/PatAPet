/**
 * 檔案用途：全域的例外處理器，攔截 Controller 層拋出的所有 Exception，並轉換為統一格式的 HTTP JSON 回應。
 * 互動關係：
 * - 攔截：所有 Controller 拋出的例外 (例如 IllegalArgumentException, MethodArgumentNotValidException)
 * - 影響：決定前端收到的 HTTP 狀態碼與錯誤訊息格式
 */
package com.patapet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // -----------------------------------------------------------------------
    // @Valid 驗證失敗 → 400 Bad Request
    // 例：email 格式錯、密碼太短
    // -----------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(err.getField(), err.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "errors", fieldErrors,
                "timestamp", Instant.now().toString()));
    }

    // -----------------------------------------------------------------------
    // 業務邏輯錯誤 → 400 Bad Request
    // 例：email 重複、Email 或密碼錯誤
    // -----------------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "message", ex.getMessage(),
                "timestamp", Instant.now().toString()));
    }

    // -----------------------------------------------------------------------
    // 找不到路由 → 404 Not Found
    // -----------------------------------------------------------------------
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", 404,
                "message", "找不到該 API 路由",
                "timestamp", Instant.now().toString()));
    }

    // -----------------------------------------------------------------------
    // 其他未預期的例外 → 500
    // -----------------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("未處理的例外", ex); // ← 加這行，印出完整 stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 500,
                "message", "伺服器發生錯誤，請稍後再試",
                "timestamp", Instant.now().toString()));
    }
}
