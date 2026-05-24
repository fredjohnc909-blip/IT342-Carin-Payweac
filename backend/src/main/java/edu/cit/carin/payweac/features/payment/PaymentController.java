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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@Transactional
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
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

        if (rent.getStatus() == Rent.RentStatus.PAID) {
            throw new RuntimeException("This rent has already been paid.");
        }

        Payment payment = new Payment();
        payment.setRent(rent);
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        payment.setReferenceNumber(referenceNumber);
        payment.setStatus(Payment.PaymentStatus.APPROVED);

        if (receipt != null && !receipt.isEmpty()) {
            payment.setReceiptImage(receipt.getBytes());
        }

        // Auto-update rent status so it reflects immediately
        java.math.BigDecimal currentPaid = paymentRepository.findByRentOrderByPaymentDateDesc(rent).stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.APPROVED)
            .map(p -> p.getAmount() != null ? p.getAmount() : java.math.BigDecimal.ZERO)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal rentAmount = rent.getAmount() != null ? rent.getAmount() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal newTotalPaid = currentPaid.add(amount);

        if (newTotalPaid.compareTo(rentAmount) >= 0) {
            rent.setStatus(Rent.RentStatus.PAID);
        } else {
            rent.setStatus(Rent.RentStatus.PARTIALLY_PAID);
        }
        rentRepository.save(rent);

        Payment saved = paymentRepository.save(payment);

        return ResponseEntity.ok(ApiResponse.success(mapToDto(saved)));
    }

    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getMyHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PaymentDto> history = paymentRepository.findSummaryByUserOrderByPaymentDateDesc(user)
                .stream()
                .map(this::mapSummaryToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    private PaymentDto mapSummaryToDto(PaymentSummary p) {
        PaymentSummary.UserSummary u = p.getUser();
        PaymentSummary.RentSummary r = p.getRent();
        return new PaymentDto(
                p.getId(),
                r.getId(),
                u.getFirstName() + " " + u.getLastName(),
                u.getRoomNumber(),
                r.getMonth() + " " + r.getYear(),
                p.getAmount(),
                p.getPaymentMethod().name(),
                p.getReferenceNumber(),
                p.getStatus().name(),
                p.getPaymentDate(),
                p.getAdminRemarks()
        );
    }

    private PaymentDto mapToDto(Payment p) {
        User u = p.getUser();
        return new PaymentDto(
                p.getId(),
                p.getRent().getId(),
                u.getFirstName() + " " + u.getLastName(),
                u.getRoomNumber(),
                p.getRent().getMonth() + " " + p.getRent().getYear(),
                p.getAmount(),
                p.getPaymentMethod().name(),
                p.getReferenceNumber(),
                p.getStatus().name(),
                p.getPaymentDate(),
                p.getAdminRemarks()
        );
    }

    @GetMapping("/delete-dupes")
    public ResponseEntity<String> deleteDupes() {
        List<Payment> all = paymentRepository.findAll();
        java.util.Map<Long, Payment> kept = new java.util.HashMap<>();
        int deletedCount = 0;
        for (Payment p : all) {
            Long rentId = p.getRent().getId();
            if (kept.containsKey(rentId)) {
                paymentRepository.delete(p);
                deletedCount++;
            } else {
                kept.put(rentId, p);
            }
        }
        return ResponseEntity.ok("Deleted " + deletedCount + " duplicates.");
    }
}
