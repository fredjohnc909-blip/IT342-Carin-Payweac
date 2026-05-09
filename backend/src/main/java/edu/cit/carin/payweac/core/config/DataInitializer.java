package edu.cit.carin.payweac.core.config;

import edu.cit.carin.payweac.features.rent.Rent;
import edu.cit.carin.payweac.features.user.User;
import edu.cit.carin.payweac.features.rent.RentRepository;
import edu.cit.carin.payweac.features.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Check if there are users and create a sample rent if none exists for them
            userRepository.findAll().forEach(user -> {
                if (rentRepository.findByUserAndStatus(user, Rent.RentStatus.PENDING).isEmpty()) {
                    Rent sampleRent = new Rent();
                    sampleRent.setUser(user);
                    sampleRent.setMonth("April");
                    sampleRent.setYear(2026);
                    sampleRent.setAmount(new BigDecimal("5000.00"));
                    sampleRent.setStatus(Rent.RentStatus.PENDING);
                    sampleRent.setDueDate(LocalDate.now().plusDays(5));
                    rentRepository.save(sampleRent);
                    
                    Rent webRent = new Rent();
                    webRent.setUser(user);
                    webRent.setMonth("May");
                    webRent.setYear(2026);
                    webRent.setAmount(new BigDecimal("5000.00"));
                    webRent.setStatus(Rent.RentStatus.PENDING);
                    webRent.setDueDate(LocalDate.now().plusDays(35));
                    rentRepository.save(webRent);
                }
            });
        };
    }
}
