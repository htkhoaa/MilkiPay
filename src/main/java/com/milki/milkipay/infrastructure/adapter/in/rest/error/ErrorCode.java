package com.milki.milkipay.infrastructure.adapter.in.rest.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Defines error codes used in API responses.
 */

@Getter
public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "Request Validation Failed"),
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "Payment not found"),
    PAYMENT_ALREADY_REFUNDED("PAYMENT_ALREADY_REFUNDED", HttpStatus.CONFLICT,"Payment has already been refunded"),
    INVALID_PAYMENT_STATE("INVALID_PAYMENT_STATE", HttpStatus.UNPROCESSABLE_ENTITY, "Payment is not in a valid state for this operation"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"),
    INVALID_ARGUMENT("INVALID_ARGUMENT", HttpStatus.BAD_REQUEST, "Invalid argument provided");

    ErrorCode(String value, HttpStatus status, String defaultMessage) {
        this.value = value;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    private final String value;
    private final HttpStatus status;
    private final String defaultMessage;
}
