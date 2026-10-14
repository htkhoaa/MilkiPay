package com.milki.milkipay.unit.application;

import com.milki.milkipay.application.port.out.LoadPaymentPort;
import com.milki.milkipay.application.service.QueryPaymentService;
import com.milki.milkipay.domain.exception.PaymentNotFoundException;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentMethod;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.model.vo.MerchantId;
import com.milki.milkipay.domain.model.vo.Money;
import com.milki.milkipay.domain.model.vo.PaymentId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryPaymentServiceTest {

    @Mock
    private LoadPaymentPort loadPaymentPort;

    @InjectMocks
    private QueryPaymentService service;

    private Payment newPayment() {
        return Payment.create(
                "merchant-001",
                new Money(100_000, Currency.getInstance("VND")),
                PaymentMethod.BANK_TRANSFER,
                "Order payment"
        );
    }

    @Test
    void shouldFindPaymentById() {
        Payment payment = newPayment();
        PaymentId id = payment.getPaymentId();

        when(loadPaymentPort.findById(id))
                .thenReturn(Optional.of(payment));

        Payment result = service.findById(id);

        assertThat(result).isSameAs(payment);
        verify(loadPaymentPort).findById(id);
    }

    @Test
    void shouldThrowWhenPaymentNotFound() {
        PaymentId id = PaymentId.generate();

        when(loadPaymentPort.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void shouldFindPaymentsByStatus() {
        Payment payment = newPayment();
        List<Payment> payments = List.of(payment);

        when(loadPaymentPort.findByStatus(PaymentStatus.PENDING))
                .thenReturn(payments);

        List<Payment> result = service.findByStatus(PaymentStatus.PENDING);

        assertThat(result).containsExactly(payment);
        verify(loadPaymentPort).findByStatus(PaymentStatus.PENDING);
    }

    @Test
    void shouldFindPaymentsByDateRange() {
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-02-01T00:00:00Z");

        Payment payment = newPayment();

        when(loadPaymentPort.findByDateRange(from, to))
                .thenReturn(List.of(payment));

        List<Payment> result = service.findByDateRange(from, to);

        assertThat(result).containsExactly(payment);
        verify(loadPaymentPort).findByDateRange(from, to);
    }

    @Test
    void shouldRejectInvalidDateRange() {
        Instant from = Instant.parse("2026-02-01T00:00:00Z");
        Instant to = Instant.parse("2026-01-01T00:00:00Z");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.findByDateRange(from, to));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.findByDateRange(from, from));

        verifyNoInteractions(loadPaymentPort);
    }

    @Test
    void shouldFindPaymentsByMerchant() {
        MerchantId merchantId = MerchantId.of("merchant-001");
        Payment payment = newPayment();

        when(loadPaymentPort.findByMerchantId(merchantId.value()))
                .thenReturn(List.of(payment));

        List<Payment> result = service.findByMerchant(merchantId);

        assertThat(result).containsExactly(payment);
        verify(loadPaymentPort).findByMerchantId(merchantId.value());
    }

    @Test
    void shouldRejectNullQueryInputs() {
        assertThatNullPointerException()
                .isThrownBy(() -> service.findById(null));

        assertThatNullPointerException()
                .isThrownBy(() -> service.findByStatus(null));

        assertThatNullPointerException()
                .isThrownBy(() -> service.findByMerchant(null));

        assertThatNullPointerException()
                .isThrownBy(() -> service.findByDateRange(null, Instant.now()));

        assertThatNullPointerException()
                .isThrownBy(() -> service.findByDateRange(Instant.now(), null));

        verifyNoInteractions(loadPaymentPort);
    }
}