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
}
