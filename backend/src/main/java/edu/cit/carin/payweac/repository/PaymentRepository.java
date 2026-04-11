package edu.cit.carin.payweac.repository;

import edu.cit.carin.payweac.entity.Payment;
import edu.cit.carin.payweac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserOrderByPaymentDateDesc(User user);
    List<Payment> findByStatusOrderByPaymentDateDesc(Payment.PaymentStatus status);
}
