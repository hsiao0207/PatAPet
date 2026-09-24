/**
 * 檔案用途：發布案件 (Listing) 的實體類別 (Entity)，對應資料庫的 listings 表格。
 * 互動關係：
 * - 關聯：與 User(Owner) 為多對一、與 Pet 為一對一/多對多(視業務邏輯)
 */
package com.patapet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "listings", indexes = {
        @Index(name = "idx_listings_pet_id", columnList = "pet_id"),
        @Index(name = "idx_listings_status_location", columnList = "status, location_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // 多對一關聯：一個刊登時段對應一隻寵物
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "hourly_rate_aud", nullable = false, precision = 6, scale = 2)
    private BigDecimal hourlyRateAud;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ListingStatus status = ListingStatus.AVAILABLE;

    // ---------------------------------------------------------------------------
    // Enum 定義
    // ---------------------------------------------------------------------------

    public enum ListingStatus {
        AVAILABLE, // 可預約
        BOOKED, // 已被預約成功
        CANCELLED // 飼主取消刊登
    }
}
