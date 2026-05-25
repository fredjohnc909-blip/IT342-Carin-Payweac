package edu.cit.carin.payweac;

import edu.cit.carin.payweac.features.rent.Rent;
import edu.cit.carin.payweac.features.rent.RentDto;
import edu.cit.carin.payweac.features.rent.RentRepository;
import edu.cit.carin.payweac.features.payment.Payment;
import edu.cit.carin.payweac.features.payment.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class DebugTest {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void debugRents() {
        System.out.println("DEBUGGING RENTS...");
        List<Rent> allRents = rentRepository.findAll();
        for (Rent rent : allRents) {
            try {
                List<Payment> payments = paymentRepository.findByRentOrderByPaymentDateDesc(rent);
                BigDecimal amountPaid = payments.stream()
                        .filter(p -> p.getStatus() == Payment.PaymentStatus.APPROVED)
                        .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal rentAmount = rent.getAmount() != null ? rent.getAmount() : BigDecimal.ZERO;
                BigDecimal remainingBalance = rentAmount.subtract(amountPaid);

                RentDto dto = new RentDto(
                        rent.getId(),
                        rent.getMonth(),
                        rent.getYear(),
                        rentAmount,
                        amountPaid,
                        remainingBalance,
                        rent.getStatus().name(),
                        rent.getStartDate(),
                        rent.getDueDate()
                );
                System.out.println("SUCCESS for rent: " + rent.getId());
            } catch (Exception e) {
                System.out.println("FAILED for rent: " + rent.getId());
                e.printStackTrace();
            }
        }
    }
}
