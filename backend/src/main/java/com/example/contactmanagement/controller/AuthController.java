package com.example.contactmanagement.controller;

import com.example.contactmanagement.dto.ChangePasswordRequest;
import com.example.contactmanagement.dto.JwtResponse;
import com.example.contactmanagement.dto.LoginRequest;
import com.example.contactmanagement.dto.MessageResponse;
import com.example.contactmanagement.dto.SignupRequest;
import com.example.contactmanagement.dto.UserProfileDto;
import com.example.contactmanagement.security.UserDetailsImpl;
import com.example.contactmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserProfileDto profile = authService.getUserProfile(userDetails.getId());
        return ResponseEntity.ok(profile);
    }
}
