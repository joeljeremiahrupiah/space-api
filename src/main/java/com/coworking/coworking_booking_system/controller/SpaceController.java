package com.coworking.coworking_booking_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coworking.coworking_booking_system.dto.SpaceDto;
import com.coworking.coworking_booking_system.dto.SpaceSearchCriteria;
import com.coworking.coworking_booking_system.service.SpaceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    @Autowired
    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    // Get all spaces
    @GetMapping
    public ResponseEntity<List<SpaceDto>> getAllSpaces() {
        List<SpaceDto> spaces = spaceService.getAllSpaces();
        return ResponseEntity.ok(spaces);
    }

    // Get a specific space by ID
    @GetMapping("/{id}")
    public ResponseEntity<SpaceDto> getSpaceById(@PathVariable Long id) {
        return spaceService.getSpaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new space (Requires ADMIN or MANAGER role
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> createSpace(@Valid @RequestBody SpaceDto spaceDto) {
        try {
            SpaceDto createdSpace = spaceService.createSpace(spaceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSpace);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal error occurred during space creation: " + e.getMessage());
        }
    }

    // Update an existing space Requires ADMIN or MANAGER roles
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> updateSpace(@PathVariable Long id, @Valid @RequestBody SpaceDto spaceDto) {
        try {
            return spaceService.updateSpace(id, spaceDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal error occurred during space update: " + e.getMessage());
        }
    }

    // Delete a space Requires ADMIN role
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteSpace(@PathVariable Long id) {
        try {
            boolean deleted = spaceService.deleteSpace(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal error occurred during space deletion.");
        }
    }

    // Search/filter spaces
    @GetMapping("/search")
    public ResponseEntity<?> searchSpaces(SpaceSearchCriteria criteria) {
        try {
            List<SpaceDto> availableSpaces = spaceService.findAvailableSpaces(criteria);
            if (availableSpaces.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(availableSpaces);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid search criteria: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal error occurred during space search.");
        }
    }

}
