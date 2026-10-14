package com.milki.milkipay.unit.application;

import com.milki.milkipay.application.port.out.LoadPaymentPort;
import com.milki.milkipay.application.port.out.SavePaymentPort;
import com.milki.milkipay.application.service.RefundPaymentService;
import com.milki.milkipay.domain.exception.InvalidPaymentStateException;
import com.milki.milkipay.domain.exception.PaymentAlreadyRefundedException;
import com.milki.milkipay.domain.exception.PaymentNotFoundException;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentMethod;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.model.vo.Money;
import com.milki.milkipay.domain.model.vo.PaymentId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundPaymentServiceTest {

    @Mock
    private LoadPaymentPort loadPaymentPort;

    @Mock
    private SavePaymentPort savePaymentPort;

    @InjectMocks
    private RefundPaymentService service;

    private Payment newPayment() {
        return Payment.create(
                "merchant-001",
                new Money(100_000, Currency.getInstance("VND")),
                PaymentMethod.BANK_TRANSFER,
                "Order payment"
        );
    }

    @Test
    void shouldRefundAndSaveCompletedPayment() {
        Payment payment = newPayment();
        payment.complete("gateway-123");

        PaymentId id = payment.getPaymentId();

        when(loadPaymentPort.findById(id))
                .thenReturn(Optional.of(payment));

        when(savePaymentPort.save(payment))
                .thenReturn(payment);

        Payment result = service.refundPayment(id);

        assertThat(result).isSameAs(payment);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(result.getRefundedAt()).isNotNull();

        verify(loadPaymentPort).findById(id);
        verify(savePaymentPort).save(payment);
    }

    @Test
    void shouldThrowWhenPaymentNotFound() {
        PaymentId id = PaymentId.generate();

        when(loadPaymentPort.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refundPayment(id))
                .isInstanceOf(PaymentNotFoundException.class);

        verifyNoInteractions(savePaymentPort);
    }

    @Test
    void shouldRejectRefundingPendingPayment()   {
        Payment payment = newPayment();
        PaymentId id = payment.getPaymentId();

        when(loadPaymentPort.findById(id))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> service.refundPayment(id))
                .isInstanceOf(InvalidPaymentStateException.class);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        verifyNoInteractions(savePaymentPort);
    }

    @Test
    void shouldRejectRefundingFailedPayment() {
        Payment payment = newPayment();
        payment.fail("Gateway error");

        PaymentId id = payment.getPaymentId();

        when(loadPaymentPort.findById(id))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> service.refundPayment(id))
                .isInstanceOf(InvalidPaymentStateException.class);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        verifyNoInteractions(savePaymentPort);
    }

    @Test
    void shouldRejectDoubleRefund() {
        Payment payment = newPayment();
        payment.complete("gateway-123");
        payment.refund();

        PaymentId id = payment.getPaymentId();

        when(loadPaymentPort.findById(id))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> service.refundPayment(id))
                .isInstanceOf(PaymentAlreadyRefundedException.class);

        verifyNoInteractions(savePaymentPort);
    }

    @Test
    void shouldRejectNullPaymentId() {
        assertThatNullPointerException()
                .isThrownBy(() -> service.refundPayment(null));

        verifyNoInteractions(loadPaymentPort, savePaymentPort);
    }
}