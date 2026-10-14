package com.milki.milkipay.unit.application;

import com.milki.milkipay.application.port.in.command.CreatePaymentCommand;
import com.milki.milkipay.application.port.out.SavePaymentPort;
import com.milki.milkipay.application.service.CreatePaymentService;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentMethod;
import com.milki.milkipay.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePaymentServiceTest {

    @Mock
    private SavePaymentPort savePaymentPort;

    @InjectMocks
    private CreatePaymentService service;

    private static final Currency VND = Currency.getInstance("VND");

    @Test
    void shouldCreateAndSavePayment() {
        CreatePaymentCommand command = new CreatePaymentCommand(
                "merchant-001",
                new BigDecimal("100000"),
                VND,
                PaymentMethod.BANK_TRANSFER,
                "Order payment"
        );

        when(savePaymentPort.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = service.createPayment(command);

        ArgumentCaptor<Payment> captor =
                ArgumentCaptor.forClass(Payment.class);

        verify(savePaymentPort).save(captor.capture());

        Payment saved = captor.getValue();

        assertThat(result).isSameAs(saved);
        assertThat(saved.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(saved.getAmount().amount()).isEqualTo(100_000);
        assertThat(saved.getAmount().currency()).isEqualTo(VND);
        assertThat(saved.getDescription()).isEqualTo("Order payment");
        assertThat(saved.getPaymentId()).isNotNull();
    }

    @Test
    void shouldNotSaveWhenAmountIsInvalid() {
        CreatePaymentCommand command = new CreatePaymentCommand(
                "merchant-001",
                BigDecimal.ZERO,
                VND,
                PaymentMethod.BANK_TRANSFER,
                "Order payment"
        );

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.createPayment(command));

        verifyNoInteractions(savePaymentPort);
    }
}