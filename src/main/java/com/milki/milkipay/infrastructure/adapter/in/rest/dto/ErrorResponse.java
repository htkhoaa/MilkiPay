package com.milki.milkipay.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.milki.milkipay.infrastructure.adapter.in.rest.error.ErrorCode;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * Carries error information returned to API clients.
 */

@Builder
public record ErrorResponse(
        Instant timestamp,
        int status,
        ErrorCode code,
        String message,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<String> details
) {
}
