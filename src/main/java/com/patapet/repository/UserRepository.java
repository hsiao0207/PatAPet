/**
 * Purpose: Data Access Object (Repository) for User entities. Provides CRUD operations on the users table in PostgreSQL.
 * Interactions:
 * - Called by: AuthService (to find users, check if an email exists, and save new users)
 * - Operates on: User (Entity class)
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
