package com.milki.milkipay.infrastructure.adapter.in.rest.error;

import com.milki.milkipay.domain.exception.InvalidPaymentStateException;
import com.milki.milkipay.domain.exception.PaymentAlreadyRefundedException;
import com.milki.milkipay.domain.exception.PaymentNotFoundException;
import com.milki.milkipay.infrastructure.adapter.in.rest.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

/**
 * Handles REST API exceptions and converts them into
 * consistent HTTP error responses.
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        log.warn("Validation failed: {}", details);
        return buildResponse(ErrorCode.VALIDATION_ERROR, details);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        log.warn("Payment not found: {}", ex.getMessage());
        return buildResponse(ErrorCode.PAYMENT_NOT_FOUND, List.of(ex.getMessage()));
    }

    @ExceptionHandler(PaymentAlreadyRefundedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentAlreadyRefundedException(PaymentAlreadyRefundedException ex) {
        log.warn("Conflict — payment already refunded: {}", ex.getMessage());
        return buildResponse(ErrorCode.PAYMENT_ALREADY_REFUNDED, List.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentStateException(InvalidPaymentStateException ex) {
        log.warn("Invalid payment state transition: {}", ex.getMessage());
        return buildResponse(ErrorCode.INVALID_PAYMENT_STATE, List.of(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildResponse(ErrorCode.INVALID_ARGUMENT, List.of(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, List.of(ex.getMessage()));
    }

    private static ResponseEntity<ErrorResponse> buildResponse(ErrorCode code, List<String> details) {
        ErrorResponse errRes = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(code.getStatus().value())
                .code(code)
                .message(code.getDefaultMessage())
                .details(details)
                .build();

        return ResponseEntity.status(code.getStatus()).body(errRes);
    }
}
