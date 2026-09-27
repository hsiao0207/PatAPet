/**
 * 檔案用途：寵物模組的商業邏輯層 (Service)，也就是這個模組的「大腦」。
 * 互動關係：
 * - 接收：由 PetController 傳入整理好的參數與身分資訊。
 * - 操作：呼叫 PetRepository 與 UserRepository 進行資料庫操作。
 * - 負責：負責所有關於寵物的安全檢查 (權限)、邏輯運算 (算體型)，並轉換 Entity 與 DTO。
 */
package com.patapet.service;

import com.patapet.dto.PetRequest;
import com.patapet.dto.PetResponse;
import com.patapet.entity.Pet;
import com.patapet.entity.User;
import com.patapet.repository.PetRepository;
import com.patapet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service // 告訴 Spring 這是一個業務邏輯元件，請把它交給 Spring 管理
@RequiredArgsConstructor // Lombok 會自動幫我們寫好 dependency injection (依賴注入) 的建構子
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // -----------------------------------------------------------------------
    // 新增寵物
    // -----------------------------------------------------------------------
    @Transactional // 確保這個方法裡的資料庫操作要嘛全成功，要嘛全失敗(Rollback)
    public PetResponse createPet(UUID ownerId, PetRequest request) {

        // 1. 檢查這位「主人(User)」存不存在
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("找不到該使用者"));
        // 2. 自動根據體重判斷體型 (這就是典型的業務邏輯！)
        Pet.PetSize calculatedSize = calculatePetSize(request.weightKg());
        // 3. 把前端傳來的 DTO 轉換成資料庫要的 Entity
        Pet pet = Pet.builder()
                .owner(owner) // 綁定關聯 (ManyToOne)
                .name(request.name())
                .breed(request.breed())
                .weightKg(request.weightKg())
                .sizeCategory(calculatedSize) // 塞入我們算好的體型
                .photoUrl(request.photoUrl())
                .tags(request.tags() != null ? request.tags() : List.of()) // 防止前端沒傳 tags 時變成 null
                .build();
        // 4. 存進資料庫
        Pet savedPet = petRepository.save(pet);
        // 5. 轉換成乾淨的 Response DTO 回傳
        return toResponse(savedPet);
    }

    // -----------------------------------------------------------------------
    // 查詢該主人的所有寵物
    // -----------------------------------------------------------------------
    @Transactional(readOnly = true) // readOnly = true 可以讓查詢速度變快
    public List<PetResponse> getPetsByOwner(UUID ownerId) {
        List<Pet> pets = petRepository.findByOwnerId(ownerId); // 呼叫我們剛剛在 Repository 寫的黑魔法

        // 把 List<Pet> 轉換成 List<PetResponse>
        return pets.stream()
                .map(this::toResponse)
                .toList();
    }

    // -----------------------------------------------------------------------
    // 修改寵物資料
    // -----------------------------------------------------------------------
    @Transactional
    public PetResponse updatePet(UUID ownerId, UUID petId, PetRequest request) {
        // 1. 先用 petId 找出這隻寵物
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("找不到該寵物"));

        // 2. 資安檢查：確認這隻寵物真的是這個登入者的！
        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("您無權限修改別人的寵物！");
        }

        // 3. 重新計算體型 (因為前端可能修改了體重)
        Pet.PetSize calculatedSize = calculatePetSize(request.weightKg());

        // 4. 更新寵物的資料
        pet.setName(request.name());
        pet.setBreed(request.breed());
        pet.setWeightKg(request.weightKg());
        pet.setSizeCategory(calculatedSize);
        pet.setPhotoUrl(request.photoUrl());
        pet.setTags(request.tags() != null ? request.tags() : List.of());

        // 5. 存檔並回傳 (因為有 @Transactional，JPA 其實會自動幫我們 Update，但呼叫 save 比較保險)
        Pet updatedPet = petRepository.save(pet);
        return toResponse(updatedPet);
    }

    // -----------------------------------------------------------------------
    // 刪除寵物
    // -----------------------------------------------------------------------
    @Transactional
    public void deletePet(UUID ownerId, UUID petId) {
        // 1. 先找出寵物
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("找不到該寵物"));

        // 2. 🚨 資安檢查：不能刪除別人的寵物
        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("您無權限刪除別人的寵物！");
        }

        // 3. 執行刪除
        petRepository.delete(pet);
    }

    // -----------------------------------------------------------------------
    // 私有工具方法 (Helper methods)
    // -----------------------------------------------------------------------

    // 工具方法：判斷體型
    private Pet.PetSize calculatePetSize(BigDecimal weight) {
        if (weight.compareTo(new BigDecimal("10")) < 0) {
            return Pet.PetSize.SMALL;
        } else if (weight.compareTo(new BigDecimal("25")) <= 0) {
            return Pet.PetSize.MEDIUM;
        } else {
            return Pet.PetSize.LARGE;
        }
    }

    // 工具方法：把 Entity 轉成 Response DTO
    private PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getWeightKg(),
                pet.getSizeCategory(),
                pet.getPhotoUrl(),
                pet.getTags(),
                pet.getOwner().getId());
    }
}
