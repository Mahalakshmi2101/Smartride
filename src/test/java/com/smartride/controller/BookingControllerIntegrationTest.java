package com.smartride.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartride.dto.LoginRequest;
import com.smartride.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String passengerToken;

    @BeforeEach
    void obtainToken() throws Exception {
        // register passenger
        RegisterRequest reg = new RegisterRequest();
        reg.setName("Booking Tester");
        reg.setEmail("booker_" + System.currentTimeMillis() + "@smartride.com");
        reg.setPassword("Test@1234");
        reg.setRole("PASSENGER");

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reg)));

        // login
        LoginRequest login = new LoginRequest();
        login.setEmail(reg.getEmail());
        login.setPassword("Test@1234");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andReturn();

        String body = result.getResponse().getContentAsString();
        // extract token from response JSON {"token":"..."}
        passengerToken = objectMapper.readTree(body).get("token").asText();
    }

    @Test
    void getMyBookings_withValidToken_returns200() throws Exception {
        mockMvc.perform(get("/api/bookings/my")
                .header("Authorization", "Bearer " + passengerToken))
            .andExpect(status().isOk());
    }

    @Test
    void getMyBookings_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/bookings/my"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyBookings_invalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/bookings/my")
                .header("Authorization", "Bearer fake.token.here"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void bookRide_invalidRideId_returns4xx() throws Exception {
        mockMvc.perform(post("/api/bookings/book/99999")
                .header("Authorization", "Bearer " + passengerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seatsBooked\": 1}"))
            .andExpect(status().is4xxClientError());
    }
}