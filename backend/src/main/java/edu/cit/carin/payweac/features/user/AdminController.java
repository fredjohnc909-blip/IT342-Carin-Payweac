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

import edu.cit.carin.payweac.features.rent.RentDto;
import edu.cit.carin.payweac.features.rent.AdminRentDto;
import edu.cit.carin.payweac.features.rent.UpdateRentRequest;
import edu.cit.carin.payweac.features.rent.CreateRentRequest;
import edu.cit.carin.payweac.features.user.User;
import edu.cit.carin.payweac.features.user.UserRepository;
import edu.cit.carin.payweac.features.user.TenantDto;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1/admin")
@Transactional
public class AdminController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<TenantDto>>> getTenants() {
        List<TenantDto> tenants = userRepository.findByRole(User.Role.TENANT)
                .stream()
                .map(u -> new TenantDto(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getRoomNumber(), u.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(tenants));
    }

    @GetMapping("/users/{userId}/rents")
    public ResponseEntity<ApiResponse<List<AdminRentDto>>> getTenantRents(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<AdminRentDto> rents = rentRepository.findByUserOrderByYearDescMonthDesc(user)
                .stream()
                .map(rent -> {
                    List<Payment> payments = paymentRepository.findByRentOrderByPaymentDateDesc(rent);
                    String payMethod = null;
                    String refNum = null;
                    if (!payments.isEmpty()) {
                        payMethod = payments.get(0).getPaymentMethod().name();
                        refNum = payments.get(0).getReferenceNumber();
                    }
                    BigDecimal amountPaid = payments.stream()
                        .filter(p -> p.getStatus() == Payment.PaymentStatus.APPROVED)
                        .map(Payment::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal remainingBalance = rent.getAmount().subtract(amountPaid);

                    return new AdminRentDto(
                            rent.getId(),
                            user.getFirstName() + " " + user.getLastName(),
                            user.getEmail(),
                            user.getRoomNumber(),
                            rent.getMonth(),
                            rent.getYear(),
                            rent.getAmount(),
                            amountPaid,
                            remainingBalance,
                            rent.getStatus().name(),
                            rent.getStartDate(),
                            rent.getDueDate(),
                            payMethod,
                            refNum
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(rents));
    }

    @GetMapping("/rents")
    public ResponseEntity<ApiResponse<List<AdminRentDto>>> getAllRents() {
        List<AdminRentDto> rents = rentRepository.findAll()
                .stream()
                .sorted((r1, r2) -> {
                    if (r1.getDueDate() == null && r2.getDueDate() == null) return 0;
                    if (r1.getDueDate() == null) return 1;
                    if (r2.getDueDate() == null) return -1;
                    return r2.getDueDate().compareTo(r1.getDueDate());
                })
                .map(rent -> {
                    User u = rent.getUser();
                    List<edu.cit.carin.payweac.features.payment.PaymentSummary> payments = paymentRepository.findSummaryByRentOrderByPaymentDateDesc(rent);
                    String payMethod = null;
                    String refNum = null;
                    if (!payments.isEmpty()) {
                        payMethod = payments.get(0).getPaymentMethod().name();
                        refNum = payments.get(0).getReferenceNumber();
                    }
                    BigDecimal amountPaid = payments.stream()
                        .filter(p -> p.getStatus() == edu.cit.carin.payweac.features.payment.Payment.PaymentStatus.APPROVED)
                        .map(edu.cit.carin.payweac.features.payment.PaymentSummary::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal remainingBalance = rent.getAmount().subtract(amountPaid);

                    return new AdminRentDto(
                            rent.getId(),
                            u.getFirstName() + " " + u.getLastName(),
                            u.getEmail(),
                            u.getRoomNumber(),
                            rent.getMonth(),
                            rent.getYear(),
                            rent.getAmount(),
                            amountPaid,
                            remainingBalance,
                            rent.getStatus().name(),
                            rent.getStartDate(),
                            rent.getDueDate(),
                            payMethod,
                            refNum
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(rents));
    }

    @org.springframework.transaction.annotation.Transactional
    @PostMapping("/tenants/{tenantId}/rents")
    public ResponseEntity<ApiResponse<RentDto>> createRent(@PathVariable Long tenantId, @RequestBody CreateRentRequest request) {
        User user = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Rent rent = new Rent();
        rent.setUser(user);
        rent.setMonth(request.getMonth());
        rent.setYear(request.getYear());
        rent.setAmount(request.getAmount());
        rent.setStartDate(request.getStartDate());
        rent.setDueDate(request.getDueDate());
        rent.setStatus(Rent.RentStatus.PENDING);

        Rent saved = rentRepository.save(rent);

        return ResponseEntity.ok(ApiResponse.success(new RentDto(
                saved.getId(),
                saved.getMonth(),
                saved.getYear(),
                saved.getAmount(),
                BigDecimal.ZERO,
                saved.getAmount(),
                saved.getStatus().name(),
                saved.getStartDate(),
                saved.getDueDate()
        )));
    }

    @org.springframework.transaction.annotation.Transactional
    @DeleteMapping("/rents/{rentId}")
    public ResponseEntity<ApiResponse<Void>> deleteRent(@PathVariable Long rentId) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RuntimeException("Rent not found"));
                
        // Delete associated payments first to prevent foreign key constraint violation
        List<Payment> payments = paymentRepository.findByRentOrderByPaymentDateDesc(rent);
        paymentRepository.deleteAll(payments);

        rentRepository.delete(rent);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @org.springframework.transaction.annotation.Transactional
    @PutMapping("/rents/{rentId}")
    public ResponseEntity<ApiResponse<RentDto>> updateRent(@PathVariable Long rentId, @RequestBody UpdateRentRequest request) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RuntimeException("Rent not found"));
        
        if (request.getAmount() != null) {
            rent.setAmount(request.getAmount());
        }
        if (request.getStartDate() != null) {
            rent.setStartDate(request.getStartDate());
        }
        boolean statusChangedToPaidOrMissed = false;
        Rent.RentStatus oldStatus = rent.getStatus();

        if (request.getStatus() != null) {
            rent.setStatus(request.getStatus());
            if (oldStatus != request.getStatus() && 
                (request.getStatus() == Rent.RentStatus.PAID || request.getStatus() == Rent.RentStatus.MISSED)) {
                statusChangedToPaidOrMissed = true;
            }
        }
        if (request.getDueDate() != null) {
            rent.setDueDate(request.getDueDate());
        }
        
        Rent saved = rentRepository.save(rent);

        if (statusChangedToPaidOrMissed) {
            try {
                Payment p = new Payment();
                p.setUser(rent.getUser());
                p.setRent(saved);
                p.setAmount(saved.getAmount());
                p.setPaymentMethod(Payment.PaymentMethod.CASH);
                p.setReferenceNumber("OVERRIDE-" + saved.getId() + "-" + java.util.UUID.randomUUID().toString().substring(0, 6));
                p.setPaymentDate(java.time.Instant.now());
                p.setStatus(request.getStatus() == Rent.RentStatus.PAID ? Payment.PaymentStatus.APPROVED : Payment.PaymentStatus.REJECTED);
                p.setAdminRemarks("Manually marked as " + request.getStatus().name() + " by admin");
                paymentRepository.save(p);
            } catch (Exception e) {
                System.err.println("Failed to create dummy payment history record: " + e.getMessage());
                e.printStackTrace();
            }
        }
        List<Payment> payments = paymentRepository.findByRentOrderByPaymentDateDesc(saved);
        BigDecimal amountPaid = payments.stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.APPROVED)
            .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal rentAmount = saved.getAmount() != null ? saved.getAmount() : BigDecimal.ZERO;
        BigDecimal remainingBalance = rentAmount.subtract(amountPaid);

        return ResponseEntity.ok(ApiResponse.success(new RentDto(
                saved.getId(),
                saved.getMonth(),
                saved.getYear(),
                rentAmount,
                amountPaid,
                remainingBalance,
                saved.getStatus().name(),
                saved.getStartDate(),
                saved.getDueDate()
        )));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getAllPayments() {
        List<PaymentDto> payments = paymentRepository.findAllProjectedByOrderByPaymentDateDesc()
                .stream()
                .map(this::mapSummaryToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/payments/pending")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPendingPayments() {
        List<PaymentDto> pending = paymentRepository.findSummaryByStatusOrderByPaymentDateDesc(Payment.PaymentStatus.PENDING)
                .stream()
                .map(this::mapSummaryToDto)
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
        
        Payment saved = paymentRepository.save(payment);

        // Also update the associated rent status
        Rent rent = payment.getRent();
        BigDecimal currentPaid = paymentRepository.findByRentOrderByPaymentDateDesc(rent).stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.APPROVED)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        if (currentPaid.compareTo(rent.getAmount()) >= 0) {
            rent.setStatus(Rent.RentStatus.PAID);
        } else if (currentPaid.compareTo(BigDecimal.ZERO) > 0) {
            rent.setStatus(Rent.RentStatus.PARTIALLY_PAID);
        } else {
            rent.setStatus(Rent.RentStatus.PENDING);
        }
        rentRepository.save(rent);

        return ResponseEntity.ok(ApiResponse.success(mapToDto(saved)));
    }

    @PutMapping("/payments/{id}/reject")
    public ResponseEntity<ApiResponse<PaymentDto>> rejectPayment(
            @PathVariable Long id,
            @RequestParam(value = "remarks", required = false) String remarks) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.REJECTED);
        payment.setAdminRemarks(remarks);
        
        Payment saved = paymentRepository.save(payment);

        // Also update the associated rent status back to pending or missed
        Rent rent = payment.getRent();
        BigDecimal currentPaid = paymentRepository.findByRentOrderByPaymentDateDesc(rent).stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.APPROVED)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        if (currentPaid.compareTo(rent.getAmount()) >= 0) {
            rent.setStatus(Rent.RentStatus.PAID);
        } else if (currentPaid.compareTo(BigDecimal.ZERO) > 0) {
            rent.setStatus(Rent.RentStatus.PARTIALLY_PAID);
        } else {
            rent.setStatus(Rent.RentStatus.PENDING);
        }
        rentRepository.save(rent);

        return ResponseEntity.ok(ApiResponse.success(mapToDto(saved)));
    }

    @DeleteMapping("/payments/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentRepository.delete(payment);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/rents/delete-dupes")
    public ResponseEntity<String> deleteDuplicateRents() {
        List<Rent> allRents = rentRepository.findAll();
        java.util.Map<String, Rent> kept = new java.util.HashMap<>();
        int deletedCount = 0;
        
        for (Rent r : allRents) {
            String key = r.getUser().getId() + "-" + r.getMonth() + "-" + r.getYear();
            if (kept.containsKey(key)) {
                Rent existing = kept.get(key);
                if (existing.getStatus() == Rent.RentStatus.PAID && r.getStatus() != Rent.RentStatus.PAID) {
                    rentRepository.delete(r);
                    deletedCount++;
                } else if (existing.getStatus() != Rent.RentStatus.PAID && r.getStatus() == Rent.RentStatus.PAID) {
                    rentRepository.delete(existing);
                    kept.put(key, r);
                    deletedCount++;
                } else {
                    rentRepository.delete(r);
                    deletedCount++;
                }
            } else {
                kept.put(key, r);
            }
        }
        return ResponseEntity.ok("Deleted " + deletedCount + " duplicate rents.");
    }

    private PaymentDto mapSummaryToDto(edu.cit.carin.payweac.features.payment.PaymentSummary p) {
        edu.cit.carin.payweac.features.payment.PaymentSummary.UserSummary u = p.getUser();
        edu.cit.carin.payweac.features.payment.PaymentSummary.RentSummary r = p.getRent();
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
        edu.cit.carin.payweac.features.user.User u = p.getUser();
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
}
