package com.milki.milkipay.integration;

import com.milki.milkipay.application.port.out.LoadPaymentPort;
import com.milki.milkipay.application.port.out.SavePaymentPort;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.model.vo.PaymentId;
import com.milki.milkipay.infrastructure.adapter.in.rest.dto.CreatePaymentRequest;
import com.milki.milkipay.infrastructure.adapter.in.rest.dto.ErrorResponse;
import com.milki.milkipay.infrastructure.adapter.in.rest.dto.PaymentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Payment API integration tests")
public class PaymentControllerIT {

    @Container
    static PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("paymentdb_test")
            .withUsername("payment")
            .withPassword("payment");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LoadPaymentPort loadPaymentPort;

    @Autowired
    private SavePaymentPort savePaymentPort;

    // ─── Create (POST /api/payments) ──────────────────────────────────────────

    @Test
    @DisplayName("POST /api/payments - should return 201 and a PENDING payment")
    void shouldCreatePayment() {
        var request = createRequest("merchant-001");

        ResponseEntity<PaymentResponse> response =
                restTemplate.postForEntity("/api/payments", request, PaymentResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().amount()).isEqualByComparingTo(new BigDecimal("100000"));
        assertThat(response.getBody().status()).isEqualTo(PaymentStatus.PENDING.name());
        assertThat(response.getBody().merchantId()).isEqualTo("merchant-001");
    }

    @Test
    @DisplayName("POST /api/payments - should return 400 for missing required fields")
    void shouldReturn400OnValidationError() {
        var invalid = new CreatePaymentRequest("", new BigDecimal("-100000"),
                "USD", "QR", "TEST");

        ResponseEntity<ErrorResponse> response =
                restTemplate.postForEntity("/api/payments", invalid, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().code().getValue()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().details()).isNotEmpty();
    }

    // ─── Get by ID (GET /api/payments/{id}) ───────────────────────────────────
    @Test
    @DisplayName("GET /api/payments/{id} - should return the payment after creation")
    void shouldGetPaymentById() {
        PaymentResponse created = createPaymentAndGet("merchant-002");

        ResponseEntity<PaymentResponse> response =
                restTemplate.getForEntity("/api/payments/" + created.id(), PaymentResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
    }

    @Test
    @DisplayName("GET /api/payments/{id} - should return 404 PAYMENT_NOT_FOUND for unknown ID")
    void shouldReturn404ForUnknownId() {
        ResponseEntity<ErrorResponse> response =
                restTemplate.getForEntity("/api/payments/" + UUID.randomUUID(), ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code().getValue()).isEqualTo("PAYMENT_NOT_FOUND");
    }

    // ─── Refund (POST /api/payments/{id}/refund) ──────────────────────────────
    @Test
    @DisplayName("GET /api/payments/{id}/refund - should return 200 and REFUNDED status")
    void shouldRefundPaymentSuccessfully() {
        PaymentResponse pending = createPaymentAndGet("merchant-003");

        Payment payment = loadPaymentPort.findById(PaymentId.of(pending.id())).orElseThrow();
        payment.complete("gateway-123");
        Payment saved = savePaymentPort.save(payment);

        ResponseEntity<PaymentResponse> response =
                restTemplate.getForEntity("/api/payments/" + pending.id() + "/refund", PaymentResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(PaymentStatus.REFUNDED.name());
    }

    @Test
    @DisplayName("GET /api/payments/{id}/refund - should return 422 for PENDING payment")
    void shouldReturn422WhenRefundingPendingPayment() {
        PaymentResponse pending = createPaymentAndGet("merchant-004");

        ResponseEntity<ErrorResponse> response =
                restTemplate.getForEntity("/api/payments/" + pending.id() + "/refund", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code().getValue()).isEqualTo("INVALID_PAYMENT_STATE");
    }

    @Test
    @DisplayName("POST /api/payments/{id}/refund — should return 409 when already refunded")
    void shouldReturn409WhenAlreadyRefunded() {
        PaymentResponse created = createPaymentAndGet("merchant-double-refund");

        Payment payment = loadPaymentPort.findById(PaymentId.of(created.id())).orElseThrow();
        payment.complete("gateway-123");
        payment.refund();
        savePaymentPort.save(payment);

        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/api/payments/" + created.id() + "/refund", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code().getValue()).isEqualTo("PAYMENT_ALREADY_REFUNDED");
    }

    // ─── List / Search (GET /api/payments) ────────────────────────────────────
    @Test
    @DisplayName("GET /api/payments?status=PENDING - should filter by status")
    void shouldFilterByStatus() {
        String specificMerchant = "merchant-special-1";
        createPaymentAndGet(specificMerchant);

        ResponseEntity<PaymentResponse[]> response =
                restTemplate.getForEntity("/api/payments?status=PENDING", PaymentResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
        assertThat(response.getBody())
                .allMatch(p -> p.status().equals(PaymentStatus.PENDING.name()));
    }

    @Test
    @DisplayName("GET /api/payments?from=...&to=... - should filter by date range")
    void shouldFilterByDateRange() {
        createPaymentAndGet("merchant-date-test");

        Instant now = Instant.now();
        Instant from = now.minus(1, ChronoUnit.HOURS);
        Instant to = now.plus(1, ChronoUnit.HOURS);

        ResponseEntity<PaymentResponse[]> response =
                restTemplate.getForEntity(
                        "/api/payments?from=%s&to=%s".formatted(from.toString(), to.toString()),
                        PaymentResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private CreatePaymentRequest createRequest(String merchantId) {
        return new CreatePaymentRequest(
                merchantId,
                new BigDecimal("100000"),
                "VND",
                "BANK_QR",
                "Integration test order"
        );
    }

    private PaymentResponse createPaymentAndGet(String merchantId) {
        ResponseEntity<PaymentResponse> response =
                restTemplate.postForEntity("/api/payments", createRequest(merchantId), PaymentResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }
}
