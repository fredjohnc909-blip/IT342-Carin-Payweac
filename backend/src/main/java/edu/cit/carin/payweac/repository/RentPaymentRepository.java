package edu.cit.carin.payweac.repository;

import edu.cit.carin.payweac.entity.RentPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {

    List<RentPayment> findByUser_IdOrderByPaidAtDesc(Long userId);

    boolean existsByUser_IdAndBillingPeriod(Long userId, String billingPeriod);
}
