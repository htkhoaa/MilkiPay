package com.milki.milkipay.domain.model;

/**
 * Defines payment lifecycle states.
 * Valid transitions are {@code PENDING} to {@code COMPLETED} or {@code FAILED}, and {@code COMPLETED} to {@code REFUNDED}.
 */
public enum PaymentStatus {
    /** Awaiting gateway processing. */
    PENDING,

    /** Successfully processed and eligible for refund. */
    COMPLETED,

    /** Processing failed. */
    FAILED,

    /** Refunded; no further transitions are allowed. */
    REFUNDED
}
