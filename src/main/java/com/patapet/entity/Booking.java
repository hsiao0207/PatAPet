package com.patapet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_bookings_listing_id", columnList = "listing_id"),
        @Index(name = "idx_bookings_renter_id", columnList = "renter_id"),
        @Index(name = "idx_bookings_stripe_pi", columnList = "stripe_payment_intent_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // 關聯：一筆預約綁定一個刊登時段 (Listing)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    // 關聯：一筆預約由一位租客 (Renter) 發起
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_id", nullable = false)
    private User renter;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_mode", nullable = false, length = 20)
    @Builder.Default
    private SocialMode socialMode = SocialMode.QUIET;

    // 金額拆解 (單位：AUD)
    @Column(name = "base_fee_aud", nullable = false, precision = 6, scale = 2)
    private BigDecimal baseFeeAud;

    @Column(name = "deposit_fee_aud", nullable = false, precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal depositFeeAud = new BigDecimal("50.00");

    @Column(name = "total_authorized_aud", nullable = false, precision = 6, scale = 2)
    private BigDecimal totalAuthorizedAud;

    @Column(name = "platform_fee_aud", nullable = false, precision = 6, scale = 2)
    private BigDecimal platformFeeAud;

    @Column(name = "owner_earnings_aud", nullable = false, precision = 6, scale = 2)
    private BigDecimal ownerEarningsAud;

    // Stripe Payment Intent ID (用於跟 Stripe API 通訊)
    @Column(name = "stripe_payment_intent_id", nullable = false, unique = true, length = 255)
    private String stripePaymentIntentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING_CONFIRM;

    // ---------------------------------------------------------------------------
    // Enum 定義
    // ---------------------------------------------------------------------------

    public enum SocialMode {
        QUIET, // 社恐友善模式 (不聊天)
        CHITCHAT // 暢聊模式 (歡迎溝通)
    }

    public enum BookingStatus {
        PENDING_CONFIRM, // 待飼主確認預約
        CONFIRMED, // 預約已確認 (等待散步)
        CANCELLED, // 已取消 (釋放 Stripe 預授權)
        COMPLETED // 散步完成 (已解扣押金並派發收益)
    }
}
