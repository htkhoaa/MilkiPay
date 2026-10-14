package com.milki.milkipay.application.service;

import com.milki.milkipay.application.port.in.QueryPaymentUseCase;
import com.milki.milkipay.application.port.out.LoadPaymentPort;
import com.milki.milkipay.domain.exception.PaymentNotFoundException;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.model.vo.MerchantId;
import com.milki.milkipay.domain.model.vo.PaymentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Retrieves payments through {@link LoadPaymentPort} and validates query inputs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueryPaymentService implements QueryPaymentUseCase {

    private final LoadPaymentPort loadPaymentPort;

    @Override
    @Transactional(readOnly = true)
    public Payment findById(PaymentId id) {
        Objects.requireNonNull(id, "Payment ID must be not null");

        log.debug("Querying payment with id='{}'", id.value());

        return loadPaymentPort.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id.value()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByStatus(PaymentStatus status) {
        Objects.requireNonNull(status, "Payment status must not be null");

        log.debug("Querying payments with status='{}'", status);

        return loadPaymentPort.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByDateRange(Instant from, Instant to) {
        Objects.requireNonNull(from, "From date must not be null");
        Objects.requireNonNull(to, "To date must not be null");

        if (!from.isBefore(to)) {
            throw new IllegalArgumentException(
                    "The 'from' date must be before the 'to' date. from=" + from + "to=" + to
            );
        }

        log.debug("Querying payments from {} (inclusive) to {} (exclusive)", from, to);

        return loadPaymentPort.findByDateRange(from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByMerchant(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID must not be null");

        log.debug("Querying payments with merchantId='{}'", merchantId.value());

        return loadPaymentPort.findByMerchantId(merchantId.value());
    }
}
