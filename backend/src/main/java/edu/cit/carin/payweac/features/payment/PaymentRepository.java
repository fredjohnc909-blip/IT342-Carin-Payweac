package edu.cit.carin.payweac.features.payment;

import edu.cit.carin.payweac.features.payment.Payment;
import edu.cit.carin.payweac.features.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserOrderByPaymentDateDesc(User user);
    List<Payment> findByStatusOrderByPaymentDateDesc(Payment.PaymentStatus status);
    List<Payment> findByRentOrderByPaymentDateDesc(edu.cit.carin.payweac.features.rent.Rent rent);

    List<PaymentSummary> findSummaryByUserOrderByPaymentDateDesc(User user);
    List<PaymentSummary> findSummaryByStatusOrderByPaymentDateDesc(Payment.PaymentStatus status);
    List<PaymentSummary> findSummaryByRentOrderByPaymentDateDesc(edu.cit.carin.payweac.features.rent.Rent rent);
    List<PaymentSummary> findAllProjectedByOrderByPaymentDateDesc();
}
