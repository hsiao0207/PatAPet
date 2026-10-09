/**
 * Purpose: HTTP API entry point (Controller) for the Pet module.
 * Interactions:
 * - Receives: Intercepts requests to /api/pets and uses the Authentication object set by JwtFilter to get the user's identity.
 * - Delegates: Passes request payloads to PetService for processing.
 * - Returns: Converts the PetResponse returned by PetService into JSON and returns it to the frontend with an HTTP status code.
 */
package com.patapet.controller;

import com.patapet.dto.PetRequest;
import com.patapet.dto.PetResponse;
import com.patapet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets") // 這個 Controller 底下的所有 API 網址都會以 /api/pets 開頭
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    // -----------------------------------------------------------------------
    // API: 新增一隻寵物
    // Method: POST /api/pets
    // -----------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<PetResponse> createPet(
            @Valid @RequestBody PetRequest request,
            Authentication authentication) {

        // 1. 從 Spring Security (JWT) 取得目前登入者的 userId
        // (因為我們在 JwtFilter 裡把 userId 塞進了 principal 裡)
        UUID ownerId = UUID.fromString(authentication.getName());

        // 2. 呼叫 Service 大腦做事
        PetResponse response = petService.createPet(ownerId, request);

        // 3. 回傳 201 Created 與新增完成的寵物資料
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -----------------------------------------------------------------------
    // API: 查詢我名下的所有寵物
    // Method: GET /api/pets/me
    // -----------------------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<List<PetResponse>> getMyPets(Authentication authentication) {

        // 1. 從 JWT 取得目前登入者的 userId
        UUID ownerId = UUID.fromString(authentication.getName());

        // 2. 呼叫 Service 找出該主人的所有寵物
        List<PetResponse> responses = petService.getPetsByOwner(ownerId);

        // 3. 回傳 200 OK 與寵物列表
        return ResponseEntity.ok(responses);
    }

    // -----------------------------------------------------------------------
    // API: 修改一隻寵物
    // Method: PUT /api/pets/{petId}
    // -----------------------------------------------------------------------
    @PutMapping("/{petId}")
    public ResponseEntity<PetResponse> updatePet(
            @PathVariable UUID petId, // 從網址抓出要修改的寵物 ID
            @Valid @RequestBody PetRequest request,
            Authentication authentication) {

        UUID ownerId = UUID.fromString(authentication.getName());
        PetResponse response = petService.updatePet(ownerId, petId, request);

        return ResponseEntity.ok(response); // 回傳 200 OK
    }

    // -----------------------------------------------------------------------
    // API: 刪除一隻寵物
    // Method: DELETE /api/pets/{petId}
    // -----------------------------------------------------------------------
    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(
            @PathVariable UUID petId,
            Authentication authentication) {

        UUID ownerId = UUID.fromString(authentication.getName());
        petService.deletePet(ownerId, petId);

        return ResponseEntity.noContent().build(); // 回傳 204 No Content (成功刪除且不帶任何資料)
    }

}
