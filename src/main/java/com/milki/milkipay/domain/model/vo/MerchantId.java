package com.milki.milkipay.domain.model.vo;

import java.util.Objects;

/**
 * Identifies a merchant with a non-null, nonblank value.
 */
public record MerchantId(String value) {

    public MerchantId {
        Objects.requireNonNull(value, "Merchant Id must not be null");

        if (value.isBlank()) throw new IllegalArgumentException("Merchant Id must not be blank");
    }

    public static MerchantId of(String value) {
        return new MerchantId(value);
    }
}
