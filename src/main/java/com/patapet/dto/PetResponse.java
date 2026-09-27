/**
 * 檔案用途：包裝後端處理完成的寵物資料，回傳給前端 (Data Transfer Object)。
 * 互動關係：
 * - 來源：由 PetService 將資料庫的 Pet Entity 轉換而來。
 * - 去向：透過 PetController 轉換成 JSON 格式回傳給客戶端。
 */
package com.patapet.dto;

import com.patapet.entity.Pet.PetSize;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PetResponse(
        UUID id,
        String name,
        String breed,
        BigDecimal weightKg,
        PetSize sizeCategory,
        String photoUrl,
        List<String> tags,
        UUID ownerId // 告訴前端這隻寵物是哪個主人的
) {
}
