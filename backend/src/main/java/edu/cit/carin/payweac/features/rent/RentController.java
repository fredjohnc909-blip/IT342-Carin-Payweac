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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rents")
public class RentController {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-dues")
    public ResponseEntity<ApiResponse<List<RentDto>>> getMyDues() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<RentDto> dues = rentRepository.findByUserOrderByYearDescMonthDesc(user)
                .stream()
                .map(rent -> new RentDto(
                        rent.getId(),
                        rent.getMonth(),
                        rent.getYear(),
                        rent.getAmount(),
                        rent.getStatus().name(),
                        rent.getDueDate()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dues));
    }
}
