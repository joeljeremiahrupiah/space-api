package com.coworking.coworking_booking_system.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coworking.coworking_booking_system.dto.AmenityDto;
import com.coworking.coworking_booking_system.dto.SpaceDto;
import com.coworking.coworking_booking_system.dto.SpaceSearchCriteria;
import com.coworking.coworking_booking_system.entity.Amenity;
import com.coworking.coworking_booking_system.entity.Space;
import com.coworking.coworking_booking_system.enums.BookingStatus;
import com.coworking.coworking_booking_system.repository.AmenityRepository;
import com.coworking.coworking_booking_system.repository.BookingRepository;
import com.coworking.coworking_booking_system.repository.SpaceRepository;
import com.coworking.coworking_booking_system.repository.specification.SpaceSpecification;

@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final AmenityRepository amenityRepository;
    private BookingRepository bookingRepository;

    @Autowired
    public SpaceService(SpaceRepository spaceRepository, AmenityRepository amenityRepository,
            BookingRepository bookingRepository) {
        this.spaceRepository = spaceRepository;
        this.amenityRepository = amenityRepository;
        this.bookingRepository = bookingRepository;
    }

    private SpaceDto convertToDto(Space space) {
        Set<AmenityDto> amenityDtos = space.getAmenities().stream()
                .map(amenity -> new AmenityDto(amenity.getId(), amenity.getName()))
                .collect(Collectors.toSet());

        SpaceDto dto = SpaceDto.builder()
                .id(space.getId())
                .name(space.getName())
                .type(space.getType())
                .locationDescription(space.getLocationDescription())
                .capacity(space.getCapacity())
                .pricePerHour(space.getPricePerHour())
                .amenities(amenityDtos)
                .description(space.getDescription())
                .imageUrl(space.getImageUrl())
                .build();
        dto.setOpeningTimeFromLocalTime(space.getOpeningTime());
        dto.setClosingTimeFromLocalTime(space.getClosingTime());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<SpaceDto> getAllSpaces() {
        return spaceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<SpaceDto> getSpaceById(Long id) {
        return spaceRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional
    public SpaceDto createSpace(SpaceDto spaceDto) {

        Space space = new Space();
        space.setName(spaceDto.getName());
        space.setType(spaceDto.getType());
        space.setLocationDescription(spaceDto.getLocationDescription());
        space.setCapacity(spaceDto.getCapacity());
        space.setPricePerHour(spaceDto.getPricePerHour());
        space.setDescription(spaceDto.getDescription());
        space.setImageUrl(spaceDto.getImageUrl());
        space.setOpeningTime(spaceDto.getOpeningTimeAsLocalTime());
        space.setClosingTime(spaceDto.getClosingTimeAsLocalTime());

        // Handle Amenities
        if (spaceDto.getAmenityNames() != null && !spaceDto.getAmenityNames().isEmpty()) {
            Set<Amenity> foundAmenities = amenityRepository.findByNameIn(new ArrayList<>(spaceDto.getAmenityNames()));
            if (foundAmenities.size() != spaceDto.getAmenityNames().size()) {
            }
            space.setAmenities(foundAmenities);
        }

        Space savedSpace = spaceRepository.save(space);
        return convertToDto(savedSpace);
    }

    @Transactional
    public Optional<SpaceDto> updateSpace(Long id, SpaceDto spaceDto) {
        return spaceRepository.findById(id)
                .map(existingSpace -> {
                    existingSpace.setName(spaceDto.getName());
                    existingSpace.setType(spaceDto.getType());
                    existingSpace.setLocationDescription(spaceDto.getLocationDescription());
                    existingSpace.setCapacity(spaceDto.getCapacity());
                    existingSpace.setPricePerHour(spaceDto.getPricePerHour());
                    existingSpace.setDescription(spaceDto.getDescription());
                    existingSpace.setImageUrl(spaceDto.getImageUrl());
                    existingSpace.setOpeningTime(spaceDto.getOpeningTimeAsLocalTime());
                    existingSpace.setClosingTime(spaceDto.getClosingTimeAsLocalTime());

                    // Update Amenities
                    Set<Amenity> updatedAmenities = new HashSet<>();
                    if (spaceDto.getAmenityNames() != null && !spaceDto.getAmenityNames().isEmpty()) {
                        updatedAmenities = amenityRepository.findByNameIn(new ArrayList<>(spaceDto.getAmenityNames()));
                        if (updatedAmenities.size() != spaceDto.getAmenityNames().size()) {
                        }
                    } else {
                    }
                    existingSpace.setAmenities(updatedAmenities);

                    Space savedSpace = spaceRepository.save(existingSpace);
                    return convertToDto(savedSpace);
                });
    }

    @Transactional
    public boolean deleteSpace(Long id) {
        if (spaceRepository.existsById(id)) {
            try {
                spaceRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Could not delete space with id " + id + ". It might have related bookings.",
                        e);
            }
        } else {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<SpaceDto> findAvailableSpaces(SpaceSearchCriteria criteria) {
        Specification<Space> spec = SpaceSpecification.findByCriteria(criteria);

        List<Space> potentialSpaces = spaceRepository.findAll(spec);

        // 3. Filter by Availability
        List<Space> availableSpaces;
        if (criteria.getDesiredStartTime() != null && criteria.getDesiredEndTime() != null) {
            if (criteria.getDesiredEndTime().isBefore(criteria.getDesiredStartTime())) {
                throw new IllegalArgumentException("Desired end time cannot be before start time.");
            }

            availableSpaces = potentialSpaces.stream()
                    .filter(space -> {
                        long overlappingCount = bookingRepository.countOverlappingBookings(
                                space.getId(),
                                criteria.getDesiredStartTime(),
                                criteria.getDesiredEndTime(),
                                BookingStatus.CONFIRMED);
                        boolean isAvailable = overlappingCount == 0;
                        if (!isAvailable) {

                        } else {
                        }
                        return isAvailable;
                    })
                    .collect(Collectors.toList());
        } else {
            availableSpaces = potentialSpaces;
        }

        // 4. Convert the final list of available spaces to DTOs
        return availableSpaces.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
