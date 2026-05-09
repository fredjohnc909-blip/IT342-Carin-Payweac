package edu.cit.carin.payweac.features.payment;

import edu.cit.carin.payweac.core.dto.ApiResponse;
import edu.cit.carin.payweac.features.payment.PaymentDto;
import edu.cit.carin.payweac.features.payment.Payment;
import edu.cit.carin.payweac.features.rent.Rent;
import edu.cit.carin.payweac.features.user.User;
import edu.cit.carin.payweac.features.payment.PaymentRepository;
import edu.cit.carin.payweac.features.rent.RentRepository;
import edu.cit.carin.payweac.features.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<PaymentDto>> submitPayment(
            @RequestParam("rentId") Long rentId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("referenceNumber") String referenceNumber,
            @RequestParam(value = "receipt", required = false) MultipartFile receipt) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RuntimeException("Rent record not found"));

        Payment payment = new Payment();
        payment.setRent(rent);
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        payment.setReferenceNumber(referenceNumber);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        if (receipt != null && !receipt.isEmpty()) {
            payment.setReceiptImage(receipt.getBytes());
        }

        Payment saved = paymentRepository.save(payment);

        return ResponseEntity.ok(ApiResponse.success(mapToDto(saved)));
    }

    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getMyHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PaymentDto> history = paymentRepository.findByUserOrderByPaymentDateDesc(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    private PaymentDto mapToDto(Payment p) {
        return new PaymentDto(
                p.getId(),
                p.getRent().getId(),
                p.getRent().getMonth() + " " + p.getRent().getYear(),
                p.getAmount(),
                p.getPaymentMethod().name(),
                p.getReferenceNumber(),
                p.getStatus().name(),
                p.getPaymentDate(),
                p.getAdminRemarks()
        );
    }
}
