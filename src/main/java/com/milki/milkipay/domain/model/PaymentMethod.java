package com.milki.milkipay.domain.model;

/**
 * Defines the supported payment instruments.
 */
public enum PaymentMethod {
    CREDIT_CARD,

    DEBIT_CARD,

    /** Payment via a bank QR code. */
    BANK_QR,

    BANK_TRANSFER;
}
