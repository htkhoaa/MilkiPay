package com.milki.milkipay.application.service;

import com.milki.milkipay.application.port.in.command.CreatePaymentCommand;
import com.milki.milkipay.application.port.in.CreatePaymentUseCase;
import com.milki.milkipay.application.port.out.SavePaymentPort;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.vo.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates payments and saves them through {@link SavePaymentPort}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentService implements CreatePaymentUseCase {

    private final SavePaymentPort savePaymentPort;

    @Override
    @Transactional
    public Payment createPayment(CreatePaymentCommand command) {
        log.info("Creating payment for merchant='{}' amount={} {}",
                command.merchantId(), command.amount(), command.currency());

        Money money = new Money(command.amount(), command.currency());

        Payment payment = Payment.create(
                command.merchantId(),
                money,
                command.paymentMethod(),
                command.description()
        );

        Payment saved = savePaymentPort.save(payment);
        log.info("Payment created successfully with id='{}'", saved.getPaymentId().value());
        return saved;
    }
}
