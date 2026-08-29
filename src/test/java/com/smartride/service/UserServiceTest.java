package com.smartride.service;

import com.smartride.model.User;
import com.smartride.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("maha@smartride.com");
        mockUser.setPassword("encoded_password");
        mockUser.setRoles("PASSENGER");
    }

    // -- findByUsername --

    @Test
    void findByUsername_existingUser_returnsUser() {
        when(userRepository.findByEmail("maha@smartride.com"))
                .thenReturn(Optional.of(mockUser));

        Optional<User> result = userRepository.findByEmail("maha@smartride.com");

        assertTrue(result.isPresent());
        assertEquals("maha@smartride.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("maha@smartride.com");
    }

    @Test
    void findByUsername_nonExistentUser_returnsEmpty() {
        when(userRepository.findByEmail("ghost@smartride.com"))
                .thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByEmail("ghost@smartride.com");

        assertFalse(result.isPresent());
    }

    // -- save user --

    @Test
    void saveUser_encodesPasswordBeforeSaving() {
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User saved = userRepository.save(mockUser);

        assertEquals("encoded_password", saved.getPassword());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void saveUser_nullUsername_throwsException() {
        User badUser = new User();
        badUser.setEmail(null);

        when(userRepository.save(badUser))
                .thenThrow(new IllegalArgumentException("Username cannot be null"));

        assertThrows(IllegalArgumentException.class,
                () -> userRepository.save(badUser));
    }

    // -- role check --

    @Test
    void user_defaultRole_isPassenger() {
        assertEquals("PASSENGER", mockUser.getRoles());
    }

    @Test
    void user_assignDriverRole_works() {
        mockUser.setRoles("DRIVER");
        assertEquals("DRIVER", mockUser.getRoles());
    }
}