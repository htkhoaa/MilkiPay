package com.milki.milkipay.infrastructure.adapter.in.rest.dto;

import com.milki.milkipay.domain.model.PaymentMethod;
import com.milki.milkipay.infrastructure.adapter.in.rest.validation.ValidCurrency;
import com.milki.milkipay.infrastructure.adapter.in.rest.validation.ValidPaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Carries payment creation data validated at the HTTP boundary.
 * Domain objects enforce business rules beyond request validation.
 */
public record CreatePaymentRequest(
        @NotBlank(message = "Merchant ID is required")
        @Size(max = 255, message = "Merchant ID must not exceed 255 characters")
        String merchantId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(
                regexp = "^[A-Z]{3}$",
                message = "Currency must be a 3-letter uppercase ISO 4217 code"
        )
        @ValidCurrency
        String currency,

        @NotBlank(message = "Payment method is required")
        @ValidPaymentMethod
        String paymentMethod,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description
) {
}
