/**
 * Purpose: Data Access layer (Repository) for the Pet module.
 * Interactions:
 * - Operated by: Called by PetService to perform CRUD operations on the pets table.
 * - Magic: Automatically generates SQL queries (e.g., findByOwnerId) by extending JpaRepository.
 */
package com.patapet.repository;

import com.patapet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PetRepository extends JpaRepository<Pet, UUID> {

    // Spring Data JPA 的黑魔法：
    // 只要你的方法命名符合規則 (findBy + 關聯欄位名稱 + 屬性)，
    // 它就會自動幫你寫好 SQL 語法！

    // 這個方法可以找出「特定飼主 (Owner)」名下的所有寵物
    List<Pet> findByOwnerId(UUID ownerId);
}
