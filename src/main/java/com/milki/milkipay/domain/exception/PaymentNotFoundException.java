package com.milki.milkipay.domain.exception;

import com.milki.milkipay.infrastructure.adapter.in.rest.error.AppException;
import com.milki.milkipay.infrastructure.adapter.in.rest.error.ErrorCode;

import java.util.UUID;

public class PaymentNotFoundException extends AppException {

    public PaymentNotFoundException(UUID id) {
        super(ErrorCode.PAYMENT_NOT_FOUND, "Payment '%s' not found".formatted(id));
    }
}
