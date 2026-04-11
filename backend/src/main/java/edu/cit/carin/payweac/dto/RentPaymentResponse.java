package edu.cit.carin.payweac.dto;

import edu.cit.carin.payweac.entity.RentPayment;

import java.math.BigDecimal;
import java.time.Instant;

public class RentPaymentResponse {

    private Long id;
    private BigDecimal amount;
    private String billingPeriod;
    private String status;
    private Instant paidAt;
    private String confirmationCode;

    public RentPaymentResponse() {
    }

    public RentPaymentResponse(RentPayment entity) {
        this.id = entity.getId();
        this.amount = entity.getAmount();
        this.billingPeriod = entity.getBillingPeriod();
        this.status = entity.getStatus();
        this.paidAt = entity.getPaidAt();
        this.confirmationCode = entity.getConfirmationCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}
