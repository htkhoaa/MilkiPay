package com.milki.milkipay.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifies a payment with a non-null UUID.
 */
public record PaymentId(UUID value) {

    public PaymentId {
        Objects.requireNonNull(value, "Payment ID must not be null");
    }

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId of(UUID value) {
        return new PaymentId(value);
    }
}
