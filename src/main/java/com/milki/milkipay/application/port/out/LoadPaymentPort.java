package com.milki.milkipay.application.port.out;

import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.model.vo.PaymentId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Loads persisted payments by identifier, status, creation time, or merchant.
 */
public interface LoadPaymentPort {

    Optional<Payment> findById(PaymentId id);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByDateRange(Instant from, Instant to);

    List<Payment> findByMerchantId(String merchantId);
}
