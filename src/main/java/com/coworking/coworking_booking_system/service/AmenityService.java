package com.coworking.coworking_booking_system.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coworking.coworking_booking_system.dto.AmenityDto;
import com.coworking.coworking_booking_system.entity.Amenity;
import com.coworking.coworking_booking_system.repository.AmenityRepository;

@Service
public class AmenityService {

    private final AmenityRepository amenityRepository;

    @Autowired
    public AmenityService(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    private AmenityDto convertToDto(Amenity amenity) {
        return new AmenityDto(amenity.getId(), amenity.getName());
    }

    private Amenity convertToEntity(AmenityDto amenityDto) {
        return new Amenity(amenityDto.getName());
    }

    @Transactional(readOnly = true)
    public List<AmenityDto> getAllAmenities() {
        return amenityRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AmenityDto> getAmenityById(Long id) {
        return amenityRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional
    public AmenityDto createAmenity(AmenityDto amenityDto) {
        Optional<Amenity> existingAmenity = amenityRepository.findByNameIgnoreCase(amenityDto.getName());
        if (existingAmenity.isPresent()) {
            return convertToDto(existingAmenity.get());
        }

        Amenity amenity = convertToEntity(amenityDto);
        Amenity savedAmenity = amenityRepository.save(amenity);
        return convertToDto(savedAmenity);
    }

    @Transactional
    public Optional<AmenityDto> updateAmenity(Long id, AmenityDto amenityDto) {
        return amenityRepository.findById(id)
                .map(existingAmenity -> {
                    Optional<Amenity> conflictingAmenity = amenityRepository.findByNameIgnoreCase(amenityDto.getName());
                    if (conflictingAmenity.isPresent() && !conflictingAmenity.get().getId().equals(id)) {
                        throw new IllegalArgumentException("Update failed: Another amenity with name '"
                                + amenityDto.getName() + "' already exists.");
                    }

                    existingAmenity.setName(amenityDto.getName());
                    Amenity updatedAmenity = amenityRepository.save(existingAmenity);
                    return convertToDto(updatedAmenity);
                });
    }

    @Transactional
    public boolean deleteAmenity(Long id) {
        if (amenityRepository.existsById(id)) {
            // TODO: Consider implications - should we prevent deletion if the amenity is
            // currently assigned to spaces?
            // For now, we allow deletion. In a real app, check for relationships in
            // space_amenities table.
            try {
                amenityRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Could not delete amenity with id " + id + ". It might be in use.", e);
            }
        } else {
            return false;
        }
    }

}
