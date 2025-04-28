package com.coworking.coworking_booking_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coworking.coworking_booking_system.entity.Role;
import com.coworking.coworking_booking_system.enums.ERole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
     // Find a role by its enum name
    Optional<Role> findByName(ERole name);

}
