package com.milki.milkipay.infrastructure.adapter.in.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires an exact {@link com.milki.milkipay.domain.model.PaymentMethod} constant name.
 * Null and blank values pass; use a separate constraint to require a value.
 */

@Target({
        ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentMethodValidator.class)
public @interface ValidPaymentMethod {
    String message() default "Invalid payment method";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
