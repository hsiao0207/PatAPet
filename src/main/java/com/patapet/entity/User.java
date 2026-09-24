/**
 * 檔案用途：使用者的實體類別 (Entity)，對應資料庫的 users 表格。
 * 互動關係：
 * - 關聯：與 OwnerProfile (一對一)、Pet (一對多)、Booking (一對多) 互動
 * - 被操作：由 UserRepository 進行存取
 */
package com.patapet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    @Builder.Default
    private Role role = Role.PATTER;

    // Stripe 顧客 ID (Patter 付款綁定信用卡用)
    @Column(name = "stripe_customer_id", length = 255)
    private String stripeCustomerId;

    // ---------------------------------------------------------------------------
    // JPA 關聯對映 (Entity Relationships)
    // ---------------------------------------------------------------------------

    // 1. 一對一：一個使用者可擁有一個飼主審核檔案 (OwnerProfile)
    // mappedBy 指向 OwnerProfile 類別內的 "user" 屬性
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private OwnerProfile ownerProfile;

    // 2. 一對多：一個使用者作為飼主，可擁有隻寵物 (Pets)
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pet> pets = new ArrayList<>();

    // 3. 一對多：一個使用者作為租客，可發起多筆預約 (Bookings)
    @OneToMany(mappedBy = "renter", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> renterBookings = new ArrayList<>();

    // ---------------------------------------------------------------------------
    // Enum 定義
    // ---------------------------------------------------------------------------

    public enum Role {
        PATTER, // 散步者 (預設，包含租客與未審核/已審核飼主)
        OWNER; // 飼主 (用於審核 OwnerProfile)
    }
}
