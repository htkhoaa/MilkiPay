package com.milki.milkipay.domain.model;

import com.milki.milkipay.domain.exception.InvalidPaymentStateException;
import com.milki.milkipay.domain.exception.PaymentAlreadyRefundedException;
import com.milki.milkipay.domain.model.vo.MerchantId;
import com.milki.milkipay.domain.model.vo.Money;
import com.milki.milkipay.domain.model.vo.PaymentId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * Enforces payment validation and lifecycle rules.
 * A {@code PENDING} payment may complete or fail; only a {@code COMPLETED} payment may be refunded.
 */
@Getter
public class Payment {

    private final PaymentId paymentId;
    private final MerchantId merchantId;
    private final Money amount;
    private final PaymentMethod method;
    private final String description;
    private final Instant requestedAt;

    private PaymentStatus status;
    private String externalReference;
    private String failureReason;
    private Instant updatedAt;
    private Instant refundedAt;

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("VND");

    @Builder(access = AccessLevel.PRIVATE)
    private Payment(
            PaymentId paymentId,
            MerchantId merchantId,
            Money amount,
            PaymentMethod method,
            String description,
            Instant requestedAt,
            PaymentStatus status,
            String externalReference,
            String failureReason,
            Instant updatedAt,
            Instant refundedAt) {

        validate(paymentId, merchantId, amount, method, status, description, requestedAt, updatedAt);

        this.paymentId = paymentId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.method = method;
        this.description = description;
        this.requestedAt = requestedAt;
        this.status = status;
        this.externalReference = externalReference;
        this.failureReason = failureReason;
        this.updatedAt = updatedAt;
        this.refundedAt = refundedAt;
    }


    /**
     * Creates a payment in the {@code PENDING} state.
     */
    public static Payment create(String merchantId, Money amount, PaymentMethod method, String description) {
        final Instant now = Instant.now();

        return Payment.builder()
                .paymentId(PaymentId.generate())
                .merchantId(MerchantId.of(merchantId))
                .amount(amount)
                .method(method)
                .description(description)
                .status(PaymentStatus.PENDING)
                .requestedAt(now)
                .updatedAt(now)
                .build();
    }


    /**
     * Restores persisted identity, state, and timestamps while validating required fields.
     */
    public static Payment reconstitute(
            PaymentId paymentId, MerchantId merchantId, Money amount, PaymentMethod method, String description, Instant requestedAt,
            PaymentStatus status, String externalReference, String failureReason, Instant updatedAt, Instant refundedAt) {

        return Payment.builder()
                .paymentId(paymentId)
                .merchantId(merchantId)
                .amount(amount)
                .method(method)
                .description(description)
                .requestedAt(requestedAt)
                .status(status)
                .externalReference(externalReference)
                .failureReason(failureReason)
                .updatedAt(updatedAt)
                .refundedAt(refundedAt)
                .build();
    }

    /**
     * @throws NullPointerException if a required field is {@code null}
     * @throws IllegalArgumentException if the description is blank, the amount is zero,
     *     or {@code updatedAt} precedes {@code requestedAt}
     */
    private static void validate(
            PaymentId paymentId, MerchantId merchantId, Money amount, PaymentMethod method, PaymentStatus status, String description,
            Instant requestedAt, Instant updatedAt) {

        Objects.requireNonNull(paymentId, "Payment ID must not be null");
        Objects.requireNonNull(merchantId, "Merchant ID must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(method, "Payment method must not be null");
        Objects.requireNonNull(status, "Payment status must not be null");
        Objects.requireNonNull(description, "Description must not be null");
        Objects.requireNonNull(requestedAt, "Requested at must not be null");
        Objects.requireNonNull(updatedAt, "Updated at must not be null");

        if (!supportsCurrency(amount.currency())) throw new IllegalArgumentException("Payment only supports VND");
        if (description.isBlank()) throw new IllegalArgumentException("Description must not be blank");
        if (updatedAt.isBefore(requestedAt)) throw new IllegalArgumentException("Updated at must not be before requested at");
    }


    /**
     * @param externalReference the gateway transaction ID
     * @throws InvalidPaymentStateException if the payment is not {@code PENDING}
     * @throws NullPointerException if {@code externalReference} is {@code null}
     * @throws IllegalArgumentException if {@code externalReference} is blank
     */
    public void complete(String externalReference) {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException(
                    "Payment can only be completed from PENDING state. Current state: " + status);
        }

        Objects.requireNonNull(externalReference, "External Reference must not be null");
        if (externalReference.isBlank()) throw new IllegalArgumentException("External Reference must not be blank");

        this.status = PaymentStatus.COMPLETED;
        this.externalReference = externalReference;
        this.updatedAt = Instant.now();
    }

    /**
     * @throws InvalidPaymentStateException if the payment is not {@code PENDING}
     * @throws NullPointerException if {@code reason} is {@code null}
     * @throws IllegalArgumentException if {@code reason} is blank
     */
    public void fail(String reason) {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException(
                    "Payment can only be failed from PENDING state. Current state: " + status);
        }

        Objects.requireNonNull(reason, "Reason must not be null");
        if (reason.isBlank()) throw new IllegalArgumentException("Reason must not be blank");

        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    /**
     * @throws PaymentAlreadyRefundedException if the payment is already refunded
     * @throws InvalidPaymentStateException if the payment is neither {@code COMPLETED} nor {@code REFUNDED}
     */
    public void refund() {
        if (this.status == PaymentStatus.REFUNDED) {
            throw new PaymentAlreadyRefundedException(this.paymentId.value());
        }
        if (this.status != PaymentStatus.COMPLETED) {
            throw new InvalidPaymentStateException(
                    "Only COMPLETED payment can be refunded. Current state: " + status
            );
        }

        this.status = PaymentStatus.REFUNDED;
        this.updatedAt = Instant.now();
        this.refundedAt = this.updatedAt;
    }

    public boolean isRefundable() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public static boolean supportsCurrency(Currency currency) {
        Objects.requireNonNull(currency, "Currency must not be null");

        return SUPPORTED_CURRENCIES.contains(currency.getCurrencyCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment that)) return false;
        return Objects.equals(this.paymentId, that.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", status=" + status +
                ", amount=" + amount +
                '}';
    }
}
