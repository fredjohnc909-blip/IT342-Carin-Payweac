package edu.cit.carin.payweac.features.rent;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.carin.payweac.features.user.User;
import edu.cit.carin.payweac.features.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RentRepository rentRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login a test tenant
        Map<String, String> payload = Map.of(
                "firstName",  "Rent",
                "lastName",   "Tester",
                "email",      "renttester@test.com",
                "password",   "password123",
                "confirmPassword", "password123",
                "roomNumber", "Room202"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(body).path("data").path("accessToken").asText();

        // Seed a rent record for this user
        User user = userRepository.findByEmail("renttester@test.com").orElseThrow();
        Rent rent = new Rent();
        rent.setUser(user);
        rent.setMonth("May");
        rent.setYear(2026);
        rent.setAmount(new BigDecimal("5000.00"));
        rent.setDueDate(LocalDate.of(2026, 5, 31));
        rent.setStatus(Rent.RentStatus.PENDING);
        rentRepository.save(rent);
    }

    // ── TC-RENT-001: Authenticated user can get their dues ───────────────────

    @Test
    @DisplayName("TC-RENT-001: Authenticated tenant can fetch their rent dues")
    void getMyDues_withValidToken_returnsDues() throws Exception {
        mockMvc.perform(get("/api/v1/rents/my-dues")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].month").value("May"))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    // ── TC-RENT-002: Unauthenticated user is denied ──────────────────────────

    @Test
    @DisplayName("TC-RENT-002: Request without JWT token is rejected with 401/403")
    void getMyDues_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/rents/my-dues"))
                .andExpect(status().is(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(401),
                        org.hamcrest.Matchers.is(403)
                )));
    }
}
