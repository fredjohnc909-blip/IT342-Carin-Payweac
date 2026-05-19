package edu.cit.carin.payweac.features.rent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminRentDto(
    Long id,
    String tenantName,
    String tenantEmail,
    String roomNumber,
    String month,
    int year,
    BigDecimal amount,
    String status,
    LocalDate dueDate,
    String paymentMethod,
    String referenceNumber
) {}
