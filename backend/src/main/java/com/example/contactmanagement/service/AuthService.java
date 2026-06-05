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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public void registerUser(SignupRequest signUpRequest) {
        logger.info("Registering user with email: {}", signUpRequest.getEmail());
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AuthenticationException("Error: Email is already in use!");
        }

        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);
        logger.info("User registered successfully: {}", signUpRequest.getEmail());
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticating user with email: {}", loginRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        logger.info("User authenticated successfully: {}", loginRequest.getEmail());
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getEmail());
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        logger.info("Changing password for user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            logger.warn("Password change failed for user id {}: Invalid current password", userId);
            throw new AuthenticationException("Error: Invalid current password");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        logger.info("Password changed successfully for user id: {}", userId);
    }

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
                
        return new UserProfileDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }
}
