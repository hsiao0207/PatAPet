/**
 * Purpose: Data Transfer Object (DTO) wrapping the processed pet data to be returned to the frontend.
 * Interactions:
 * - Source: Converted from Pet Entity fetched from the database by PetService.
 * - Destination: Returned to the client in JSON format via PetController.
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
