/**
 * Purpose: Data Transfer Object (DTO) for receiving pet creation or modification data from the frontend.
 * Interactions:
 * - Source: Received by PetController and validated using @Valid.
 * - Destination: Passed to PetService as parameters for business logic processing.
 */
package com.patapet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record PetRequest(
        @NotBlank(message = "寵物名稱不可為空") String name,

        @NotBlank(message = "品種不可為空") String breed,

        @NotNull(message = "體重不可為空") @Positive(message = "體重必須大於 0") BigDecimal weightKg,

        @NotBlank(message = "寵物照片不可為空") String photoUrl,

        List<String> tags) {
    // 這裡我們不請前端傳 sizeCategory，因為我們可以稍後在 Service 層
    // 透過體重 (weightKg) 自動幫他判斷是 SMALL / MEDIUM / LARGE。
}
