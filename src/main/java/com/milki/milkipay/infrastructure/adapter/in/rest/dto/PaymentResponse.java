package com.milki.milkipay.infrastructure.adapter.in.rest.dto;

import com.milki.milkipay.domain.model.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Carries payment details returned to API clients.
 */

@Builder
public record PaymentResponse (
        UUID id,
        String merchantId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String description,
        String status,
        String externalReference,
        String failureReason,
        Instant requestedAt,
        Instant updatedAt,
        Instant refundedAt
) {}
