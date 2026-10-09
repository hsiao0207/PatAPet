/**
 * Purpose: Base class for entities (MappedSuperclass). Provides automatic tracking of creation and update timestamps.
 * Interactions:
 * - Inherited by: All Entities such as User, Pet, Booking, Listing, OwnerProfile
 * - Dependencies: JpaAuditing (driven by @EnableJpaAuditing in PatAPetApplication)
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
