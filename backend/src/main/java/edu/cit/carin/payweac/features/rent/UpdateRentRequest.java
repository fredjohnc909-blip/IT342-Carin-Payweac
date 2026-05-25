package edu.cit.carin.payweac.features.rent;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateRentRequest {
    private BigDecimal amount;
    private Rent.RentStatus status;
    private LocalDate startDate;
    private LocalDate dueDate;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Rent.RentStatus getStatus() {
        return status;
    }

    public void setStatus(Rent.RentStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
