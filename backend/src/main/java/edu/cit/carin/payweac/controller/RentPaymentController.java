package edu.cit.carin.payweac.controller;

import edu.cit.carin.payweac.dto.ApiResponse;
import edu.cit.carin.payweac.dto.CreateRentPaymentRequest;
import edu.cit.carin.payweac.dto.RentPaymentResponse;
import edu.cit.carin.payweac.service.RentPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rent/payments")
public class RentPaymentController {

    private final RentPaymentService rentPaymentService;

    public RentPaymentController(RentPaymentService rentPaymentService) {
        this.rentPaymentService = rentPaymentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RentPaymentResponse>>> list(Authentication authentication) {
        String email = authentication.getName();
        List<RentPaymentResponse> list = rentPaymentService.listForUserEmail(email);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RentPaymentResponse>> create(
            @Valid @RequestBody CreateRentPaymentRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        RentPaymentResponse created = rentPaymentService.recordPayment(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }
}
