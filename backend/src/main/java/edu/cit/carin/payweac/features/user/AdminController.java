package edu.cit.carin.payweac.features.user;

import edu.cit.carin.payweac.core.dto.ApiResponse;
import edu.cit.carin.payweac.features.payment.PaymentDto;
import edu.cit.carin.payweac.features.payment.Payment;
import edu.cit.carin.payweac.features.rent.Rent;
import edu.cit.carin.payweac.features.payment.PaymentRepository;
import edu.cit.carin.payweac.features.rent.RentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RentRepository rentRepository;

    @GetMapping("/payments/pending")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPendingPayments() {
        List<PaymentDto> pending = paymentRepository.findByStatusOrderByPaymentDateDesc(Payment.PaymentStatus.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(pending));
    }

    @PutMapping("/payments/{id}/approve")
    public ResponseEntity<ApiResponse<PaymentDto>> approvePayment(
            @PathVariable Long id,
            @RequestParam(value = "remarks", required = false) String remarks) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.APPROVED);
        payment.setAdminRemarks(remarks);
        
        // Also update the associated rent status
        Rent rent = payment.getRent();
        rent.setStatus(Rent.RentStatus.PAID);
        rentRepository.save(rent);

        Payment saved = paymentRepository.save(payment);

        return ResponseEntity.ok(ApiResponse.success(mapToDto(saved)));
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
