package com.milki.milkipay.domain.exception;

import com.milki.milkipay.infrastructure.adapter.in.rest.error.AppException;
import com.milki.milkipay.infrastructure.adapter.in.rest.error.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class PaymentAlreadyRefundedException extends AppException {

    public PaymentAlreadyRefundedException(UUID id) {
        super(ErrorCode.PAYMENT_ALREADY_REFUNDED, "Payment '%s' has already been refunded".formatted(id));
    }
}
