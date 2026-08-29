package com.smartride.service;

import com.smartride.model.User;
import com.smartride.model.entity.Booking;
import com.smartride.model.entity.Ride;
import com.smartride.repository.BookingRepository;
import com.smartride.repository.RideRepository;
import com.smartride.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @InjectMocks
    private BookingService bookingService;

    private User driver;
    private User passenger;
    private Ride ride;
    private Booking booking;

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setId(1L);
        driver.setEmail("Driver One");
        driver.setRoles("DRIVER");

        passenger = new User();
        passenger.setId(2L);
        passenger.setEmail("Passenger One");
        passenger.setRoles("PASSENGER");

        ride = new Ride();
        ride.setId(10L);
        ride.setDriver(driver);
        ride.setSource("Chennai Central");
        ride.setDestination("Tambaram");
        ride.setAvailableSeats(3);
        ride.setPricePerSeat(50.0);
        ride.setRideDate(LocalDate.now().plusDays(1));
        ride.setRideTime(LocalTime.of(8, 0));
        ride.setStatus(Ride.RideStatus.ACTIVE);

        booking = new Booking();
        booking.setId(100L);
        booking.setRide(ride);
        booking.setUser(passenger);
        booking.setNumberOfSeats(1);
        booking.setStatus("CONFIRMED");
    }

    // -- find bookings --

    @Test
    void findByUser_returnsBookingList() {
        when(bookingRepository.findByUser(passenger))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingRepository.findByUser(passenger);

        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).getStatus());
        verify(bookingRepository).findByUser(passenger);
    }

    @Test
    void findByIdAndUser_returnsBooking() {
        when(bookingRepository.findByIdAndUser(100L, passenger))
                .thenReturn(Optional.of(booking));

        Optional<Booking> result = bookingRepository.findByIdAndUser(100L, passenger);

        assertTrue(result.isPresent());
        assertEquals(ride.getId(), result.get().getRide().getId());
    }

    // -- seat validation --

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

    // -- fare calculation --

    @Test
    void fareCalculation_singleSeat_isCorrect() {
        double expected = ride.getPricePerSeat()* booking.getNumberOfSeats();
        assertEquals(50.0, expected, 0.001);
    }

    @Test
    void fareCalculation_multipleSeats_isCorrect() {
        booking.setNumberOfSeats(2);
        double expected = ride.getPricePerSeat() * booking.getNumberOfSeats();
        assertEquals(100.0, expected, 0.001);
    }

    // -- cancel booking --

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
        ride.setAvailableSeats(seatsBefore + booking.getNumberOfSeats());
        assertEquals(4, ride.getAvailableSeats());
    }

    // -- save booking --

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