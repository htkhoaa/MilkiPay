package com.milki.milkipay.unit.domain;

import com.milki.milkipay.domain.exception.InvalidPaymentStateException;
import com.milki.milkipay.domain.exception.PaymentAlreadyRefundedException;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentMethod;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

class PaymentTest {

    private static final Currency VND = Currency.getInstance("VND");
    private static final Currency USD = Currency.getInstance("USD");

    private Payment newPayment() {
        return Payment.create(
                "merchant-001",
                new Money(100_000, VND),
                PaymentMethod.BANK_QR,
                "Order payment");
    }

    @Test
    void shouldCreatePendingPayment() {
        Payment payment = newPayment();

        assertThat(payment.getPaymentId()).isNotNull();
        assertThat(payment.getMerchantId()).isNotNull();
        assertThat(payment.getAmount()).isEqualTo(new Money(100_000, VND));
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getRequestedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isEqualTo(payment.getRequestedAt());
        assertThat(payment.isRefundable()).isFalse();
    }

    @Test
    void shouldRejectUnsupportedCurrency() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Payment.create(
                        "merchant-001",
                        new Money(100, USD),
                        PaymentMethod.BANK_TRANSFER,
                        "Order payment"));
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Payment.create(
                        "merchant-001",
                        new Money(100_000, VND),
                        PaymentMethod.BANK_TRANSFER,
                        " "));
    }

    @Test
    void shouldCompletePendingPayment() {
        Payment payment = newPayment();

        payment.complete("gateway-123");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getExternalReference()).isEqualTo("gateway-123");
        assertThat(payment.getUpdatedAt()).isAfterOrEqualTo(payment.getRequestedAt());
        assertThat(payment.isRefundable()).isTrue();
    }

    @Test
    void shouldFailPendingPayment() {
        Payment payment = newPayment();

        payment.fail("Gateway error");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailureReason()).isEqualTo("Gateway error");
        assertThat(payment.isRefundable()).isFalse();
    }

    @Test
    void shouldRefundCompletedPayment() {
        Payment payment = newPayment();
        payment.complete("gateway-123");

        payment.refund();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.getRefundedAt()).isNotNull();
        assertThat(payment.getRefundedAt()).isEqualTo(payment.getUpdatedAt());
        assertThat(payment.isRefundable()).isFalse();
    }

    @Test
    void shouldRejectCompletingNonPendingPayment() {
        Payment payment = newPayment();
        payment.fail("Gateway error");

        assertThatThrownBy(() -> payment.complete("gateway-123"))
                .isInstanceOf(InvalidPaymentStateException.class);

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void shouldRejectFailingNonPendingPayment() {
        Payment payment = newPayment();
        payment.complete("gateway-123");

        assertThatThrownBy(() -> payment.fail("Gateway error"))
                .isInstanceOf(InvalidPaymentStateException.class);

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void shouldRejectRefundingPendingPayment() {
        Payment payment = newPayment();

        assertThatThrownBy(payment::refund)
                .isInstanceOf(InvalidPaymentStateException.class);

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void shouldRejectRefundingFailedPayment() {
        Payment payment = newPayment();
        payment.fail("Gateway error");

        assertThatThrownBy(payment::refund)
                .isInstanceOf(InvalidPaymentStateException.class);

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void shouldRejectDoubleRefund() {
        Payment payment = newPayment();
        payment.complete("gateway-123");
        payment.refund();

        assertThatThrownBy(payment::refund)
                .isInstanceOf(PaymentAlreadyRefundedException.class);

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    void shouldRejectBlankExternalReference() {
        Payment payment = newPayment();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> payment.complete(" "));

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void shouldRejectBlankFailureReason() {
        Payment payment = newPayment();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> payment.fail(" "));

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.PENDING);
    }
}
