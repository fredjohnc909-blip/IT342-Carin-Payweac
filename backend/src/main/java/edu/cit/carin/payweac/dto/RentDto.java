package edu.cit.carin.payweac.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentDto(
    Long id,
    String month,
    int year,
    BigDecimal amount,
    String status,
    LocalDate dueDate
) {}
