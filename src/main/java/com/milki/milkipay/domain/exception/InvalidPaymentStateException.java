package com.milki.milkipay.domain.exception;

import com.milki.milkipay.infrastructure.adapter.in.rest.error.AppException;
import com.milki.milkipay.infrastructure.adapter.in.rest.error.ErrorCode;

public class InvalidPaymentStateException extends AppException {

    public InvalidPaymentStateException(String message) {
        super(ErrorCode.INVALID_PAYMENT_STATE, message);
    }
}
