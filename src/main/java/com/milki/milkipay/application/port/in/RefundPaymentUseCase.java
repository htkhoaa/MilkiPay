package com.milki.milkipay.application.port.in;

import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.exception.*;
import com.milki.milkipay.domain.model.vo.PaymentId;

import java.util.UUID;


/**
 * Refunds completed payments according to payment lifecycle rules.
 */
public interface RefundPaymentUseCase {

    /**
     * @throws PaymentNotFoundException if the payment does not exist
     * @throws PaymentAlreadyRefundedException if the payment is already refunded
     * @throws InvalidPaymentStateException if the payment is neither {@code COMPLETED} nor {@code REFUNDED}
     */
    Payment refundPayment(PaymentId paymentId);
}
