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

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Check if admin exists, if not create one
            if (!userRepository.existsByEmail("admin@payweac.com")) {
                User admin = new User(
                        "admin@payweac.com",
                        passwordEncoder.encode("admin123"),
                        "System",
                        "Admin",
                        "ADMIN",
                        User.Role.ADMIN
                );
                userRepository.save(admin);
                System.out.println("Created default admin user: admin@payweac.com / admin123");
            }

            // Removing automatic rent generation to prevent duplicate rents
            // after the backend restarts.
        };
    }
}
