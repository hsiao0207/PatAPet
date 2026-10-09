/**
 * Purpose: Entity class representing a Pet. Maps to the pets table in the database.
 * Interactions:
 * - Relationships: Many-to-One with User (Owner)
 */
package com.patapet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pets", indexes = {
        @Index(name = "idx_pets_owner_id", columnList = "owner_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // 多對一關聯：一隻寵物屬於一位飼主 (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "breed", nullable = false, length = 50)
    private String breed;

    @Column(name = "weight_kg", nullable = false, precision = 4, scale = 1)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "size_category", nullable = false, columnDefinition = "pet_size")
    private PetSize sizeCategory;

    @Column(name = "photo_url", nullable = false, length = 512)
    private String photoUrl;

    // 映射 PostgreSQL TEXT[] 陣列欄位 (例如: ["Friendly", "Needs Leash"])
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // ---------------------------------------------------------------------------
    // Enum 定義
    // ---------------------------------------------------------------------------

    public enum PetSize {
        SMALL, // 小型犬 (< 10kg)
        MEDIUM, // 中型犬 (10kg - 25kg)
        LARGE // 大型犬 (> 25kg)
    }
}
