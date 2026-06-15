package com.coursehelper.backend.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.coursehelper.backend.auth.util.JwtUtil;
import com.coursehelper.backend.exceptions.UsernameAlreadyExistsException;
import com.coursehelper.backend.exceptions.ResourceNotFoundException;
import com.coursehelper.backend.user.dto.RegisterRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
    }

    @Test
    void createUser_duplicateUsername_throwsUsernameAlreadyExistsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        assertThrows(UsernameAlreadyExistsException.class,
            () -> userService.createUser(registerRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_newUsername_savesAndReturnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(registerRequest);

        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findByUserId_missingUser_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> userService.findByUserId(99L));
    }
}
