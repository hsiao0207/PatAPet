/**
 * 檔案用途：寵物模組的資料庫存取層 (Repository)。
 * 互動關係：
 * - 被操作：由 PetService 呼叫，用來對 pets 表格進行 CRUD (新增、修改、刪除、查詢)。
 * - 黑魔法：透過繼承 JpaRepository 自動產生 SQL 語法，例如 findByOwnerId。
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
