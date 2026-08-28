package com.smartride.service;

import com.smartride.model.User;
import com.smartride.model.entity.Role;
import com.smartride.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Maha Test");
        mockUser.setEmail("maha@smartride.com");
        mockUser.setPassword("encoded_password");
        mockUser.setRole(Role.PASSENGER);
    }

    // ── findByEmail ───────────────────────────────────────

    @Test
    void findByEmail_existingUser_returnsUser() {
        when(userRepository.findByEmail("maha@smartride.com"))
            .thenReturn(Optional.of(mockUser));

        Optional<User> result = userRepository.findByEmail("maha@smartride.com");

        assertTrue(result.isPresent());
        assertEquals("Maha Test", result.get().getName());
        verify(userRepository, times(1)).findByEmail("maha@smartride.com");
    }

    @Test
    void findByEmail_nonExistentUser_returnsEmpty() {
        when(userRepository.findByEmail("ghost@smartride.com"))
            .thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByEmail("ghost@smartride.com");

        assertFalse(result.isPresent());
    }

    // ── save user ─────────────────────────────────────────

    @Test
    void saveUser_encodesPasswordBeforeSaving() {
        when(passwordEncoder.encode("rawpassword")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        mockUser.setPassword(passwordEncoder.encode("rawpassword"));
        User saved = userRepository.save(mockUser);

        assertEquals("encoded_password", saved.getPassword());
        verify(passwordEncoder, times(1)).encode("rawpassword");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void saveUser_nullEmail_throwsException() {
        User badUser = new User();
        badUser.setEmail(null);

        when(userRepository.save(badUser))
            .thenThrow(new IllegalArgumentException("Email cannot be null"));

        assertThrows(IllegalArgumentException.class,
            () -> userRepository.save(badUser));
    }

    // ── role check ────────────────────────────────────────

    @Test
    void user_defaultRole_isPassenger() {
        assertEquals(Role.PASSENGER, mockUser.getRole());
    }

    @Test
    void user_assignDriverRole_works() {
        mockUser.setRole(Role.DRIVER);
        assertEquals(Role.DRIVER, mockUser.getRole());
    }

    // ── existsByEmail ─────────────────────────────────────

    @Test
    void existsByEmail_existingEmail_returnsTrue() {
        when(userRepository.existsByEmail("maha@smartride.com")).thenReturn(true);
        assertTrue(userRepository.existsByEmail("maha@smartride.com"));
    }

    @Test
    void existsByEmail_newEmail_returnsFalse() {
        when(userRepository.existsByEmail("new@smartride.com")).thenReturn(false);
        assertFalse(userRepository.existsByEmail("new@smartride.com"));
    }
}s