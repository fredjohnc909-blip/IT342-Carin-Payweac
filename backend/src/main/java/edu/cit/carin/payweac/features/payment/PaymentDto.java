package edu.cit.carin.payweac.features.payment;
import edu.cit.carin.payweac.features.payment.Payment;

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
