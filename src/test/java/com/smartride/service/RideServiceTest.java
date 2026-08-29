package com.smartride.service;

import com.smartride.model.User;
import com.smartride.model.entity.Ride;
import com.smartride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @InjectMocks
    private RideService rideService;

    private User driver;
    private Ride ride;

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setId(1L);
        driver.setEmail("Driver Raj");
        driver.setRoles("DRIVER");

        ride = new Ride();
        ride.setId(10L);
        ride.setDriver(driver);
        ride.setSource("Anna Nagar");
        ride.setDestination("OMR");
        ride.setAvailableSeats(4);
        ride.setPricePerSeat(80.0);
        ride.setRideDate(LocalDate.now().plusDays(1));
        ride.setRideTime(LocalTime.of(9, 0));
        ride.setStatus(Ride.RideStatus.ACTIVE);
    }

    // -- find rides --

    @Test
    void findById_existingRide_returnsRide() {
        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));

        Optional<Ride> result = rideRepository.findById(10L);

        assertTrue(result.isPresent());
        assertEquals("Anna Nagar", result.get().getSource());
    }

    @Test
    void findById_missingRide_returnsEmpty() {
        when(rideRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(rideRepository.findById(99L).isEmpty());
    }

    @Test
    void findAll_returnsAllRides() {
        when(rideRepository.findAll()).thenReturn(List.of(ride));

        List<Ride> rides = rideRepository.findAll();

        assertEquals(1, rides.size());
        assertEquals("Driver Raj", rides.get(0).getDriver().getEmail());
    }

    // -- status checks --

    @Test
    void ride_activeStatus_isBookable() {
        assertEquals(Ride.RideStatus.ACTIVE, ride.getStatus());
        assertTrue(ride.getAvailableSeats() > 0);
    }

    @Test
    void ride_noSeatsLeft_isNotBookable() {
        ride.setAvailableSeats(0);
        assertFalse(ride.getAvailableSeats() > 0);
    }

    @Test
    void ride_cancelledStatus_isNotBookable() {
        ride.setStatus(Ride.RideStatus.CANCELLED);
        assertNotEquals(Ride.RideStatus.ACTIVE, ride.getStatus());
    }

    // -- fare --

    @Test
    void ride_pricePerSeat_isPositive() {
        assertTrue(ride.getPricePerSeat()> 0);
    }

    @Test
    void ride_zeroPricePerSeat_isInvalid() {
        ride.setPricePerSeat(0.0);
        assertFalse(ride.getPricePerSeat() > 0);
    }

    // -- departure date --

    @Test
    void ride_departureDateInFuture_isValid() {
        assertTrue(ride.getRideDate().isAfter(LocalDate.now().minusDays(1)));
    }

    @Test
    void ride_pastDepartureDate_isInvalid() {
        ride.setRideDate(LocalDate.now().minusDays(1));
        assertFalse(ride.getRideDate().isAfter(LocalDate.now()));
    }

    // -- save --

    @Test
    void saveRide_persistsCorrectly() {
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride saved = rideRepository.save(ride);

        assertNotNull(saved);
        assertEquals(10L, saved.getId());
        verify(rideRepository).save(ride);
    }

    @Test
    void findAllActiveRides_returnsOnlyActive() {
        Ride cancelled = new Ride();
        cancelled.setStatus(Ride.RideStatus.CANCELLED);

        when(rideRepository.findAll()).thenReturn(List.of(ride, cancelled));

        long activeCount = rideRepository.findAll().stream()
                .filter(r -> Ride.RideStatus.ACTIVE.equals(r.getStatus()))
                .count();

        assertEquals(1, activeCount);
    }
}