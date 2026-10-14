package com.milki.milkipay.infrastructure.adapter.out.persistence.entity;

import com.milki.milkipay.domain.model.PaymentMethod;
import com.milki.milkipay.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores payment data in the {@code payments} table.
 */
@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payments_status", columnList = "status"),
                @Index(name = "idx_payments_merchant_id", columnList = "merchant_id"),
                @Index(name = "idx_payments_created_at", columnList = "created_at"),
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentEntity {

    @Id @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "merchant_id", nullable = false, updatable = false)
    private String merchantId;

    @Column(nullable = false, updatable = false)
    private Long amount;

    @Column(nullable = false, updatable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(name = "description", nullable = false, updatable = false)
    private String description;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;
}
