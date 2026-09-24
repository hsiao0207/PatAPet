/**
 * 檔案用途：實體的基礎類別 (MappedSuperclass)，提供自動化紀錄創建時間與更新時間的功能。
 * 互動關係：
 * - 繼承：被 User, Pet, Booking, Listing, OwnerProfile 等所有 Entity 繼承
 * - 依賴：JpaAuditing (由 PatAPetApplication 的 @EnableJpaAuditing 驅動)
 */
package com.patapet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
