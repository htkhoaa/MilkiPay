package com.milki.milkipay.domain.model.vo;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Represents a nonnegative monetary amount in minor units and its currency.
 * The currency must define monetary precision.
 */
public record Money(long amount, Currency currency) {

    public Money {
        Objects.requireNonNull(currency, "Currency must not be null");

        if (currency.getDefaultFractionDigits() < 0) {
            throw new IllegalArgumentException("Currency has no monetary precision");
        }

        if (amount <= 0) throw new IllegalArgumentException("Money amount must be greater than zero");
    }

    /**
     * Accepts an amount in major currency units using exact currency scaling.
     *
     * @throws IllegalArgumentException if currency precision is undefined or the scaled amount is negative
     * @throws ArithmeticException if scaling does not produce an exact {@code long}
     */
    public Money(BigDecimal amount, Currency currency) {
        this(toMinorUnits(amount, currency), currency);
    }

    /**
     * @throws IllegalArgumentException if currencies differ
     * @throws ArithmeticException if the sum overflows
     */
    public Money add(Money other) {
        requireSameCurrency(other);

        return new Money(
                Math.addExact(this.amount, other.amount),
                currency);
    }

    /**
     * @throws IllegalArgumentException if currencies differ or the result is negative
     * @throws ArithmeticException if the difference overflows
     */
    public Money subtract(Money other) {
        requireSameCurrency(other);

        return new Money(
                Math.subtractExact(this.amount, other.amount),
                currency);
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "Money must not be null");

        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch %s expected %s".formatted(other.currency, currency));
        }
    }

    /**
     * @throws IllegalArgumentException if currency precision is undefined
     * @throws ArithmeticException if scaling does not produce an exact {@code long}
     */
    private static long toMinorUnits(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");

        int digits = currency.getDefaultFractionDigits();
        if (digits < 0) {
            throw new IllegalArgumentException("Currency has no monetary precision");
        }

        return amount
                .movePointRight(digits)
                .longValueExact();
    }

    public BigDecimal toMajorUnits() {
        return BigDecimal.valueOf(amount)
                .movePointLeft(currency.getDefaultFractionDigits());
    }
}
