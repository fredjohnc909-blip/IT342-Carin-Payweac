package edu.cit.carin.payweac.features.rent;

import edu.cit.carin.payweac.core.dto.ApiResponse;
import edu.cit.carin.payweac.features.rent.RentDto;
import edu.cit.carin.payweac.features.user.User;
import edu.cit.carin.payweac.features.rent.RentRepository;
import edu.cit.carin.payweac.features.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import edu.cit.carin.payweac.features.payment.PaymentRepository;
import edu.cit.carin.payweac.features.payment.Payment;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1/rents")
@Transactional
public class RentController {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/my-dues")
    public ResponseEntity<ApiResponse<List<RentDto>>> getMyDues() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<RentDto> dues = rentRepository.findByUserOrderByYearDescMonthDesc(user)
                .stream()
                .map(rent -> {
                    List<Payment> payments = paymentRepository.findByRentOrderByPaymentDateDesc(rent);
                    BigDecimal amountPaid = payments.stream()
                        .filter(p -> p.getStatus() == Payment.PaymentStatus.APPROVED)
                        .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal rentAmount = rent.getAmount() != null ? rent.getAmount() : BigDecimal.ZERO;
                    BigDecimal remainingBalance = rentAmount.subtract(amountPaid);

                    return new RentDto(
                        rent.getId(),
                        rent.getMonth(),
                        rent.getYear(),
                        rentAmount,
                        amountPaid,
                        remainingBalance,
                        rent.getStatus() != null ? rent.getStatus().name() : "PENDING",
                        rent.getStartDate(),
                        rent.getDueDate()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dues));
    }
}
