package com.milki.milkipay.infrastructure.adapter.in.rest.mapper;

import com.milki.milkipay.application.port.in.command.CreatePaymentCommand;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentMethod;
import com.milki.milkipay.infrastructure.adapter.in.rest.dto.CreatePaymentRequest;
import com.milki.milkipay.infrastructure.adapter.in.rest.dto.PaymentResponse;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Objects;

/**
 * Maps payment requests to application commands and domain payments to API responses.
 */

@Component
public class PaymentRestMapper {

    public PaymentResponse toResponse(Payment payment) {
        Objects.requireNonNull(payment, "Payment must not be null");

        return PaymentResponse.builder()
                .id(payment.getPaymentId().value())
                .merchantId(payment.getMerchantId().value())
                .amount(payment.getAmount().toMajorUnits())
                .currency(payment.getAmount().currency().getCurrencyCode())
                .paymentMethod(payment.getMethod().name())
                .description(payment.getDescription())
                .status(payment.getStatus().name())
                .externalReference(payment.getExternalReference())
                .failureReason(payment.getFailureReason())
                .requestedAt(payment.getRequestedAt())
                .updatedAt(payment.getUpdatedAt())
                .refundedAt(payment.getRefundedAt())
                .build();
    }

    public CreatePaymentCommand toCommand(CreatePaymentRequest request) {

        if (request.amount().stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Decimal values are not supported");
        }

        return CreatePaymentCommand.builder()
                .merchantId(request.merchantId())
                .amount(request.amount().stripTrailingZeros())
                .currency(Currency.getInstance(request.currency()))
                .paymentMethod(PaymentMethod.valueOf(request.paymentMethod()))
                .description(request.description())
                .build();
    }
}
