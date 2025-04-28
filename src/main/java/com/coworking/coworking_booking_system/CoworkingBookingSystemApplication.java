package com.coworking.coworking_booking_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.coworking.coworking_booking_system.entity.Role;
import com.coworking.coworking_booking_system.enums.ERole;
import com.coworking.coworking_booking_system.repository.RoleRepository;

@SpringBootApplication
public class CoworkingBookingSystemApplication {

	private static final Logger log = LoggerFactory.getLogger(CoworkingBookingSystemApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CoworkingBookingSystemApplication.class, args);
	}

	// This bean will run once the application context is loaded
	// @Bean
	// CommandLineRunner initRoles(RoleRepository roleRepository) {
	// 	return args -> {
	// 		log.info("Checking and initializing roles...");

	// 		// Check and create ROLE_USER if it doesn't exist
	// 		if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
	// 			log.info("Creating ROLE_USER");
	// 			roleRepository.save(new Role(ERole.ROLE_USER));
	// 		}

	// 		// Check and create ROLE_MANAGER if it doesn't exist
	// 		if (roleRepository.findByName(ERole.ROLE_MANAGER).isEmpty()) {
	// 			log.info("Creating ROLE_MANAGER");
	// 			roleRepository.save(new Role(ERole.ROLE_MANAGER));
	// 		}

	// 		// Check and create ROLE_ADMIN if it doesn't exist
	// 		if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
	// 			log.info("Creating ROLE_ADMIN");
	// 			roleRepository.save(new Role(ERole.ROLE_ADMIN));
	// 		}

	// 		log.info("Role initialization complete.");
	// 	};
	// }
}
