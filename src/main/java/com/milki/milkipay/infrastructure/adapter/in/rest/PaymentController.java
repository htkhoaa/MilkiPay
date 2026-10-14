package com.milki.milkipay.infrastructure.adapter.in.rest;

import com.milki.milkipay.application.port.in.CreatePaymentUseCase;
import com.milki.milkipay.application.port.in.QueryPaymentUseCase;
import com.milki.milkipay.application.port.in.RefundPaymentUseCase;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.model.vo.MerchantId;
import com.milki.milkipay.domain.model.vo.PaymentId;
import com.milki.milkipay.infrastructure.adapter.in.rest.dto.CreatePaymentRequest;
import com.milki.milkipay.infrastructure.adapter.in.rest.dto.PaymentResponse;
import com.milki.milkipay.infrastructure.adapter.in.rest.mapper.PaymentRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Handles HTTP payment operations through application use cases.
 */

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final QueryPaymentUseCase queryPaymentUseCase;
    private final RefundPaymentUseCase refundPaymentUseCase;
    private final PaymentRestMapper mapper;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = createPaymentUseCase.createPayment(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(payment));
    }

    @GetMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refund(@PathVariable UUID id) {
        Payment payment = refundPaymentUseCase.refundPayment(PaymentId.of(id));
        return ResponseEntity.ok(mapper.toResponse(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        Payment payment = queryPaymentUseCase.findById(PaymentId.of(id));
        return ResponseEntity.ok(mapper.toResponse(payment));
    }

    @GetMapping
    public ResponseEntity<?> listPayments(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String merchantId
            ) {

        if (status != null) return toResponseList(queryPaymentUseCase.findByStatus(status));

        if (from != null && to != null) return toResponseList(queryPaymentUseCase.findByDateRange(from, to));

        if (merchantId != null) return toResponseList(queryPaymentUseCase.findByMerchant(MerchantId.of(merchantId)));

        return ResponseEntity.ok(List.of());
    }

    private ResponseEntity<List<PaymentResponse>> toResponseList(List<Payment> payments) {
        return ResponseEntity.ok(payments.stream().map(mapper::toResponse).toList());
    }
}
