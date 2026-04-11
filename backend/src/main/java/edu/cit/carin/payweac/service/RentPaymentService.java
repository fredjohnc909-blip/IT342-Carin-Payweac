package edu.cit.carin.payweac.service;

import edu.cit.carin.payweac.dto.CreateRentPaymentRequest;
import edu.cit.carin.payweac.dto.RentPaymentResponse;
import edu.cit.carin.payweac.entity.RentPayment;
import edu.cit.carin.payweac.entity.User;
import edu.cit.carin.payweac.repository.RentPaymentRepository;
import edu.cit.carin.payweac.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentPaymentService {

    private final RentPaymentRepository rentPaymentRepository;
    private final UserRepository userRepository;

    public RentPaymentService(RentPaymentRepository rentPaymentRepository, UserRepository userRepository) {
        this.rentPaymentRepository = rentPaymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<RentPaymentResponse> listForUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return rentPaymentRepository.findByUser_IdOrderByPaidAtDesc(user.getId()).stream()
                .map(RentPaymentResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentPaymentResponse recordPayment(String email, CreateRentPaymentRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String period = request.getBillingPeriod().trim();
        if (rentPaymentRepository.existsByUser_IdAndBillingPeriod(user.getId(), period)) {
            throw new DuplicateRentPaymentException("Rent for " + period + " is already recorded as paid.");
        }

        String confirmation = UUID.randomUUID().toString();
        RentPayment payment = new RentPayment(user, request.getAmount(), period, confirmation);
        payment = rentPaymentRepository.save(payment);
        return new RentPaymentResponse(payment);
    }

    public static class DuplicateRentPaymentException extends RuntimeException {
        public DuplicateRentPaymentException(String message) {
            super(message);
        }
    }
}
