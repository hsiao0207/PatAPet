/**
 * 檔案用途：飼主個人檔案的實體類別 (Entity)，對應資料庫的 owner_profiles 表格。
 * 互動關係：
 * - 關聯：與 User 實體為一對一 (OneToOne) 關聯
 */
package com.patapet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "owner_profiles", indexes = {
        @Index(name = "idx_owner_profiles_user_id", columnList = "user_id"),
        @Index(name = "idx_owner_profiles_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // 關聯：一對一綁定 User，設置為 LAZY 防止無謂的全表 JOIN 查詢
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // Stripe Connect 帳號 ID (取代原本危險的明文銀行帳號)
    @Column(name = "stripe_account_id", length = 255)
    private String stripeAccountId;

    @Column(name = "vaccine_c5_declared", nullable = false)
    private Boolean vaccineC5Declared;

    @Column(name = "non_aggressive_declared", nullable = false)
    private Boolean nonAggressiveDeclared;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OwnerStatus status = OwnerStatus.PENDING;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // ---------------------------------------------------------------------------
    // Enum 定義
    // ---------------------------------------------------------------------------

    public enum OwnerStatus {
        PENDING, // 待管理員審核 (初始狀態)
        APPROVED, // 審核通過 (允許發布 Listing)
        REJECTED // 審核不通過 (退回補件)
    }
}
