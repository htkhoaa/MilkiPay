package com.milki.milkipay.infrastructure.adapter.out.persistence.mapper;

import com.milki.milkipay.domain.model.vo.MerchantId;
import com.milki.milkipay.domain.model.vo.Money;
import com.milki.milkipay.domain.model.vo.PaymentId;
import org.springframework.stereotype.Component;
import com.milki.milkipay.infrastructure.adapter.out.persistence.entity.PaymentEntity;
import com.milki.milkipay.domain.model.Payment;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

/**
 * Maps between {@link PaymentEntity} and {@link Payment}.
 * Restores persisted payments through {@link Payment#reconstitute} to preserve their identity, state, and timestamps.
 */
@Component
public class PaymentPersistenceMapper {

    public PaymentEntity toEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getPaymentId().value())
                .merchantId(payment.getMerchantId().value())
                .amount(payment.getAmount().amount())
                .currency(payment.getAmount().currency().getCurrencyCode())
                .method(payment.getMethod())
                .description(payment.getDescription())
                .requestedAt(payment.getRequestedAt())
                .status(payment.getStatus())
                .externalReference(payment.getExternalReference())
                .failureReason(payment.getFailureReason())
                .updatedAt(payment.getUpdatedAt())
                .refundedAt(payment.getRefundedAt())
                .build();
    }

    public Payment toDomain(PaymentEntity entity) {
        return Payment.reconstitute(
                PaymentId.of(entity.getId()),
                MerchantId.of(entity.getMerchantId()),
                new Money(
                        entity.getAmount(),
                        Currency.getInstance(entity.getCurrency())
                ),
                entity.getMethod(),
                entity.getDescription(),
                entity.getRequestedAt(),
                entity.getStatus(),
                entity.getExternalReference(),
                entity.getFailureReason(),
                entity.getUpdatedAt(),
                entity.getRefundedAt()
        );
    }
}
