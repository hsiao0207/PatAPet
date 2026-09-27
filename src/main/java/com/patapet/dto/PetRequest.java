/**
 * 檔案用途：接收前端建立或修改寵物時傳入的資料 (Data Transfer Object)。
 * 互動關係：
 * - 來源：由 PetController 接收並透過 @Valid 進行防呆驗證。
 * - 去向：傳遞給 PetService 作為商業邏輯處理的參數。
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
