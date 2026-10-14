package com.milki.milkipay.infrastructure.adapter.in.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires a currency code supported by the payment domain.
 * Null and blank values pass; use a separate constraint to require a value.
 */

@Target({
    ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
public @interface ValidCurrency {

    String message() default "Invalid currency code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
