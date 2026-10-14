package com.milki.milkipay.application.service;

import com.milki.milkipay.application.port.in.RefundPaymentUseCase;
import com.milki.milkipay.application.port.out.LoadPaymentPort;
import com.milki.milkipay.application.port.out.SavePaymentPort;
import com.milki.milkipay.domain.exception.PaymentNotFoundException;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.vo.PaymentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Loads payments, applies refund rules, and persists the updated state.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefundPaymentService implements RefundPaymentUseCase {

    private final LoadPaymentPort loadPaymentPort;
    private final SavePaymentPort savePaymentPort;

    @Override
    @Transactional
    public Payment refundPayment(PaymentId paymentId) {
        Objects.requireNonNull(paymentId, "Payment ID must not be null");

        log.info("Processing refund for payment id='{}'", paymentId);

        Payment existing = loadPaymentPort.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId.value()));

        existing.refund();

        Payment saved = savePaymentPort.save(existing);
        log.info("Payment id='{}' successfully refunded", paymentId);
        return saved;
    }
}
