package com.coworking.coworking_booking_system.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coworking.coworking_booking_system.dto.RegisterRequest;
import com.coworking.coworking_booking_system.entity.Role;
import com.coworking.coworking_booking_system.entity.User;
import com.coworking.coworking_booking_system.enums.ERole;
import com.coworking.coworking_booking_system.repository.RoleRepository;
import com.coworking.coworking_booking_system.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection or dependency injection
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Ensures the entire method runs within a single database transaction
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {

        // 1. Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Error: Username is already taken!");
        }

        // 2. Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFirstName(),
                registerRequest.getLastName());

        // Assign default roles
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_USER is not found."));

        roles.add(userRole);
        user.setRoles(roles);

        // Save user to the database
        User savedUser = userRepository.save(user);

        return savedUser;
    }

}
