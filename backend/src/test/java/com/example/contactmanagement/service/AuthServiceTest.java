package com.example.contactmanagement.service;

import com.example.contactmanagement.dto.LoginRequest;
import com.example.contactmanagement.dto.SignupRequest;
import com.example.contactmanagement.entity.User;
import com.example.contactmanagement.exception.AuthenticationException;
import com.example.contactmanagement.repository.UserRepository;
import com.example.contactmanagement.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_EmailAlreadyExists_ThrowsException() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@test.com");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> authService.registerUser(request));
    }

    @Test
    public void registerUser_Success() {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@test.com");
        request.setPassword("password");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");

        authService.registerUser(request);

        verify(userRepository, times(1)).save(any(User.class));
    }
}
