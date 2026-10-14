package com.milki.milkipay.application.port.out;

import com.milki.milkipay.domain.model.Payment;

/**
 * Persists new payments and updates existing payments.
 */
public interface SavePaymentPort {

    Payment save(Payment payment);
}
