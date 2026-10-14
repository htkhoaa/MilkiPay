package com.milki.milkipay.application.port.in;


import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.exception.PaymentNotFoundException;
import com.milki.milkipay.domain.model.vo.MerchantId;
import com.milki.milkipay.domain.model.vo.PaymentId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Retrieves payments by identifier, status, creation time, or merchant.
 */
public interface QueryPaymentUseCase {

    /**
     * @throws PaymentNotFoundException if the payment does not exist
     */
    Payment findById(PaymentId id);

    /**
     * @return matching payments, or an empty list if none exist
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * @param from the inclusive start timestamp
     * @param to the exclusive end timestamp
     * @return matching payments, or an empty list if none exist
     * @throws IllegalArgumentException if {@code from} is not before {@code to}
     */
    List<Payment> findByDateRange(Instant from, Instant to);

    /**
     * @return matching payments, or an empty list if none exist
     */
    List<Payment> findByMerchant(MerchantId merchantId);
}
