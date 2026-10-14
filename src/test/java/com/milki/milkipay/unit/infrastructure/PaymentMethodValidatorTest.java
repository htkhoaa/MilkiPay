package com.milki.milkipay.unit.infrastructure;

import com.milki.milkipay.domain.model.PaymentMethod;
import com.milki.milkipay.infrastructure.adapter.in.rest.validation.PaymentMethodValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodValidatorTest {

    private final PaymentMethodValidator validator = new PaymentMethodValidator();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void shouldLeaveMissingValueToRequiredConstraint(String value) {
        assertThat(validator.isValid(value, null)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(PaymentMethod.class)
    void shouldAcceptEverySupportedPaymentMethod(PaymentMethod method) {
        assertThat(validator.isValid(method.name(), null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN", "bank_qr", " BANK_QR "})
    void shouldRejectUnknownOrMalformedPaymentMethod(String value) {
        assertThat(validator.isValid(value, null)).isFalse();
    }
}
