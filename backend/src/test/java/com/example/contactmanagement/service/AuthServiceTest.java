package com.example.contactmanagement.service;

import com.example.contactmanagement.dto.ChangePasswordRequest;
import com.example.contactmanagement.dto.JwtResponse;
import com.example.contactmanagement.dto.LoginRequest;
import com.example.contactmanagement.dto.SignupRequest;
import com.example.contactmanagement.dto.UserProfileDto;
import com.example.contactmanagement.entity.User;
import com.example.contactmanagement.exception.AuthenticationException;
import com.example.contactmanagement.exception.UserNotFoundException;
import com.example.contactmanagement.repository.UserRepository;
import com.example.contactmanagement.security.JwtService;
import com.example.contactmanagement.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private Authentication authentication;

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
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhoneNumber("123456");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");

        authService.registerUser(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void authenticateUser_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("auth@test.com");
        request.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("auth@test.com");
        user.setFirstName("Auth");
        user.setLastName("Test");

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateJwtToken(any(Authentication.class))).thenReturn("test-token");

        JwtResponse response = authService.authenticateUser(request);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertEquals(1L, response.getId());
    }

    @Test
    public void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");

        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(encoder.encode("newPass")).thenReturn("encodedNewPass");

        authService.changePassword(1L, request);

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    public void changePassword_InvalidCurrentPassword_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPass");
        request.setNewPassword("newPass");

        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("wrongPass", "encodedOldPass")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.changePassword(1L, request));
    }

    @Test
    public void changePassword_UserNotFound_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.changePassword(1L, request));
    }

    @Test
    public void getUserProfile_Success() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Profile");
        user.setLastName("Test");
        user.setEmail("profile@test.com");
        user.setPhoneNumber("789012");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileDto profile = authService.getUserProfile(1L);

        assertNotNull(profile);
        assertEquals(1L, profile.getId());
        assertEquals("Profile", profile.getFirstName());
    }

    @Test
    public void getUserProfile_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.getUserProfile(1L));
    }
}
