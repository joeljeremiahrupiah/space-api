package com.coworking.coworking_booking_system.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coworking.coworking_booking_system.dto.JwtResponse;
import com.coworking.coworking_booking_system.dto.LoginRequest;
import com.coworking.coworking_booking_system.dto.RegisterRequest;
import com.coworking.coworking_booking_system.entity.Role;
import com.coworking.coworking_booking_system.entity.User;
import com.coworking.coworking_booking_system.enums.ERole;
import com.coworking.coworking_booking_system.repository.RoleRepository;
import com.coworking.coworking_booking_system.repository.UserRepository;
import com.coworking.coworking_booking_system.security.JwtUtils;
import com.coworking.coworking_booking_system.security.UserDetailsImpl;
import com.coworking.coworking_booking_system.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // Dependency injection
    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            userService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");

        } catch (DataIntegrityViolationException e) {
            String message = "Error: Username or Email is already taken!";
            if (e.getMessage() != null) {
                if (e.getMessage().toLowerCase().contains("users_username_key")
                        || e.getMessage().toLowerCase().contains("uk_username")) {
                    message = "Error: Username is already taken!";
                } else if (e.getMessage().toLowerCase().contains("users_email_key")
                        || e.getMessage().toLowerCase().contains("uk_email")) {
                    message = "Error: Email is already in use!";
                }
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal error occurred during registration.");
        }
    }

    // Handles POST requests to /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Authentication attempt for user: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            log.info("User '{}' authenticated successfully. Generating JWT.", userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Invalid username or password");
        } catch (Exception e) {
            log.error("Internal error during authentication for user {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: An internal error occurred during login.");
        }
    }

}
