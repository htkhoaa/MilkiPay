package com.milki.milkipay.infrastructure.adapter.in.rest.validation;

import com.milki.milkipay.domain.model.PaymentMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates exact {@link PaymentMethod} constant names.
 * Null and blank values pass; use a separate constraint to require a value.
 */

public class PaymentMethodValidator implements ConstraintValidator<ValidPaymentMethod, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        try {
            PaymentMethod.valueOf(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
