/**
 * 檔案用途：User 實體的資料存取層 (Data Access Object)，提供對 PostgreSQL 中 users 表格的 CRUD 操作。
 * 互動關係：
 * - 被呼叫：AuthService (用於尋找使用者、檢查 email 是否存在、儲存新使用者)
 * - 操作對象：User (實體類別)
 */
package com.patapet.repository;

import com.patapet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // 登入時用 email 查使用者
    Optional<User> findByEmail(String email);

    // 註冊時檢查 email 是否已被使用
    boolean existsByEmail(String email);
}
