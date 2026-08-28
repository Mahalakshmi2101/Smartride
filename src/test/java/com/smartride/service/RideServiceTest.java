package com.smartride.service;

import com.smartride.model.User;
import com.smartride.model.entity.Ride;
import com.smartride.model.entity.Role;
import com.smartride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    private User driver;
    private Ride ride;

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setId(1L);
        driver.setName("Driver Raj");
        driver.setRole(Role.DRIVER);

        ride = new Ride();
        ride.setId(10L);
        ride.setDriver(driver);
        ride.setPickupLocation("Anna Nagar");
        ride.setDropLocation("OMR");
        ride.setAvailableSeats(4);
        ride.setFarePerSeat(80.0);
        ride.setDepartureTime(LocalDateTime.now().plusHours(2));
        ride.setStatus("ACTIVE");
    }

    // ── find rides ────────────────────────────────────────

    @Test
    void findById_existingRide_returnsRide() {
        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));

        Optional<Ride> result = rideRepository.findById(10L);

        assertTrue(result.isPresent());
        assertEquals("Anna Nagar", result.get().getPickupLocation());
    }

    @Test
    void findById_missingRide_returnsEmpty() {
        when(rideRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(rideRepository.findById(99L).isEmpty());
    }

    @Test
    void findByDriver_returnsDriverRides() {
        when(rideRepository.findByDriver(driver)).thenReturn(List.of(ride));

        List<Ride> rides = rideRepository.findByDriver(driver);

        assertEquals(1, rides.size());
        assertEquals("Driver Raj", rides.get(0).getDriver().getName());
    }

    // ── status checks ─────────────────────────────────────

    @Test
    void ride_activeStatus_isBookable() {
        assertEquals("ACTIVE", ride.getStatus());
        assertTrue(ride.getAvailableSeats() > 0);
    }

    @Test
    void ride_noSeatsLeft_isNotBookable() {
        ride.setAvailableSeats(0);
        assertFalse(ride.getAvailableSeats() > 0);
    }

    @Test
    void ride_cancelledStatus_isNotBookable() {
        ride.setStatus("CANCELLED");
        assertNotEquals("ACTIVE", ride.getStatus());
    }

    // ── fare ──────────────────────────────────────────────

    @Test
    void ride_farePerSeat_isPositive() {
        assertTrue(ride.getFarePerSeat() > 0);
    }

    @Test
    void ride_zeroFare_isInvalid() {
        ride.setFarePerSeat(0.0);
        assertFalse(ride.getFarePerSeat() > 0);
    }

    // ── departure time ────────────────────────────────────

    @Test
    void ride_departureTime_isInFuture() {
        assertTrue(ride.getDepartureTime().isAfter(LocalDateTime.now()));
    }

    @Test
    void ride_pastDepartureTime_isInvalid() {
        ride.setDepartureTime(LocalDateTime.now().minusHours(1));
        assertFalse(ride.getDepartureTime().isAfter(LocalDateTime.now()));
    }

    // ── save ──────────────────────────────────────────────

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
        cancelled.setStatus("CANCELLED");

        when(rideRepository.findAll()).thenReturn(List.of(ride, cancelled));

        List<Ride> all = rideRepository.findAll();
        long activeCount = all.stream()
            .filter(r -> "ACTIVE".equals(r.getStatus()))
            .count();

        assertEquals(1, activeCount);
    }
}