package edu.cit.carin.payweac.features.rent;
import edu.cit.carin.payweac.features.rent.Rent;

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
