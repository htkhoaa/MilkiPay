package com.milki.milkipay.application.port.in.command;

import com.milki.milkipay.domain.model.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Carries the input data for creating a payment.
 */
@Builder
public record CreatePaymentCommand(
        String merchantId,
        BigDecimal amount,
        Currency currency,
        PaymentMethod paymentMethod,
        String description
) {
}
