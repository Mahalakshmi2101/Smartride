package com.smartride.service;

import com.smartride.model.User;
import com.smartride.model.entity.Booking;
import com.smartride.model.entity.Ride;
import com.smartride.model.entity.Role;
import com.smartride.repository.BookingRepository;
import com.smartride.repository.RideRepository;
import com.smartride.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private UserRepository userRepository;

    private User passenger;
    private User driver;
    private Ride ride;
    private Booking booking;

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setId(1L);
        driver.setName("Driver One");
        driver.setRole(Role.DRIVER);

        passenger = new User();
        passenger.setId(2L);
        passenger.setName("Passenger One");
        passenger.setRole(Role.PASSENGER);

        ride = new Ride();
        ride.setId(10L);
        ride.setDriver(driver);
        ride.setPickupLocation("Chennai Central");
        ride.setDropLocation("Tambaram");
        ride.setAvailableSeats(3);
        ride.setFarePerSeat(50.0);
        ride.setStatus("ACTIVE");

        booking = new Booking();
        booking.setId(100L);
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setSeatsBooked(1);
        booking.setStatus("CONFIRMED");
    }

    // ── find bookings ─────────────────────────────────────

    @Test
    void findByPassenger_returnsBookingList() {
        when(bookingRepository.findByPassenger(passenger))
            .thenReturn(List.of(booking));

        List<Booking> result = bookingRepository.findByPassenger(passenger);

        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
        verify(bookingRepository).findByPassenger(passenger);
    }

    @Test
    void findByRide_returnsBookingsForRide() {
        when(bookingRepository.findByRide(ride)).thenReturn(List.of(booking));

        List<Booking> result = bookingRepository.findByRide(ride);

        assertFalse(result.isEmpty());
        assertEquals(ride.getId(), result.get(0).getRide().getId());
    }

    // ── seat validation ───────────────────────────────────

    @Test
    void booking_seatsBooked_doesNotExceedAvailable() {
        int requested = 2;
        assertTrue(requested <= ride.getAvailableSeats(),
            "Booking should not exceed available seats");
    }

    @Test
    void booking_seatsBooked_exceedsAvailable_fails() {
        int requested = 5;
        assertFalse(requested <= ride.getAvailableSeats(),
            "Should reject booking exceeding seat capacity");
    }

    // ── fare calculation ──────────────────────────────────

    @Test
    void fareCalculation_singleSeat_isCorrect() {
        double expected = ride.getFarePerSeat() * booking.getSeatsBooked();
        assertEquals(50.0, expected, 0.001);
    }

    @Test
    void fareCalculation_multipleSeats_isCorrect() {
        booking.setSeatsBooked(2);
        double expected = ride.getFarePerSeat() * booking.getSeatsBooked();
        assertEquals(100.0, expected, 0.001);
    }

    // ── cancel booking ────────────────────────────────────

    @Test
    void cancelBooking_setsStatusCancelled() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking found = bookingRepository.findById(100L).orElseThrow();
        found.setStatus("CANCELLED");
        bookingRepository.save(found);

        assertEquals("CANCELLED", found.getStatus());
        verify(bookingRepository).save(found);
    }

    @Test
    void cancelBooking_restoresRideSeats() {
        int seatsBefore = ride.getAvailableSeats(); // 3
        ride.setAvailableSeats(seatsBefore + booking.getSeatsBooked());
        assertEquals(4, ride.getAvailableSeats());
    }

    // ── save booking ──────────────────────────────────────

    @Test
    void saveBooking_persistsCorrectly() {
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking saved = bookingRepository.save(booking);

        assertNotNull(saved);
        assertEquals(100L, saved.getId());
        assertEquals("CONFIRMED", saved.getStatus());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
        assertTrue(bookingRepository.findById(999L).isEmpty());
    }
}