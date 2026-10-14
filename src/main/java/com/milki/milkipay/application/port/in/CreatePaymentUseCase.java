package com.milki.milkipay.application.port.in;

import com.milki.milkipay.application.port.in.command.CreatePaymentCommand;
import com.milki.milkipay.domain.model.Payment;

/**
 * Creates and persists payments in the {@code PENDING} state.
 */
public interface CreatePaymentUseCase {

    Payment createPayment(CreatePaymentCommand command);
}
