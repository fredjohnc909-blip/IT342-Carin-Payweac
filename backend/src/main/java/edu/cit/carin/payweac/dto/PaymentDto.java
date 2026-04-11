package edu.cit.carin.payweac.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentDto(
    Long id,
    Long rentId,
    String rentMonthYear,
    BigDecimal amount,
    String paymentMethod,
    String referenceNumber,
    String status,
    Instant paymentDate,
    String adminRemarks
) {}
