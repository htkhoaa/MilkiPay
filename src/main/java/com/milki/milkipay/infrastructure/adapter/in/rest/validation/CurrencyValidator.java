package com.milki.milkipay.infrastructure.adapter.in.rest.validation;

import com.milki.milkipay.domain.model.Payment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Validates ISO 4217 currency codes against the currencies supported by {@link Payment}.
 * Null and blank values pass; use a separate constraint to require a value.
 */

@Component
public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        try {
            return Payment.supportsCurrency(Currency.getInstance(value));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
