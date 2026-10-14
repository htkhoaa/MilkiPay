package com.milki.milkipay.unit.infrastructure;

import com.milki.milkipay.infrastructure.adapter.in.rest.validation.CurrencyValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyValidatorTest {

    private final CurrencyValidator validator = new CurrencyValidator();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "VND"})
    void shouldAcceptSupportedCurrencyOrLeaveMissingValueToRequiredConstraint(String value) {
        assertThat(validator.isValid(value, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"USD", "XXX", "INVALID", "vnd", " VND "})
    void shouldRejectUnsupportedOrMalformedCurrency(String value) {
        assertThat(validator.isValid(value, null)).isFalse();
    }
}
