package edu.cit.carin.payweac.features.rent;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateRentRequest {
    private String month;
    private int year;
    private BigDecimal amount;
    private LocalDate dueDate;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
