package edu.cit.carin.payweac.features.payment;

import java.math.BigDecimal;
import java.time.Instant;

public interface PaymentSummary {
    Long getId();
    RentSummary getRent();
    UserSummary getUser();
    BigDecimal getAmount();
    Payment.PaymentMethod getPaymentMethod();
    String getReferenceNumber();
    Payment.PaymentStatus getStatus();
    Instant getPaymentDate();
    String getAdminRemarks();

    interface RentSummary {
        Long getId();
        String getMonth();
        Integer getYear();
    }

    interface UserSummary {
        String getFirstName();
        String getLastName();
        String getRoomNumber();
    }
}
