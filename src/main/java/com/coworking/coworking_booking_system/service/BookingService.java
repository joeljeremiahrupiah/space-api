package com.coworking.coworking_booking_system.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coworking.coworking_booking_system.dto.BookingResponseDto;
import com.coworking.coworking_booking_system.dto.CreateBookingRequest;
import com.coworking.coworking_booking_system.entity.Booking;
import com.coworking.coworking_booking_system.entity.Space;
import com.coworking.coworking_booking_system.entity.User;
import com.coworking.coworking_booking_system.enums.BookingStatus;
import com.coworking.coworking_booking_system.exception.ResourceNotFoundException;
import com.coworking.coworking_booking_system.exception.SlotUnavailableException;
import com.coworking.coworking_booking_system.repository.BookingRepository;
import com.coworking.coworking_booking_system.repository.SpaceRepository;
import com.coworking.coworking_booking_system.repository.UserRepository;
import com.coworking.coworking_booking_system.security.UserDetailsImpl;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, SpaceRepository spaceRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.spaceRepository = spaceRepository;
        this.userRepository = userRepository;
    }

    private BookingResponseDto convertToDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .spaceId(booking.getSpace().getId())
                .spaceName(booking.getSpace().getName())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    @Transactional
    public BookingResponseDto createBooking(CreateBookingRequest request) {
        log.info("Attempting to create booking for space ID: {} from {} to {}",
                request.getSpaceId(), request.getStartTime(), request.getEndTime());

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));

        Space space = spaceRepository.findById(request.getSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", request.getSpaceId()));

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        if (space.getOpeningTime() != null && startTime.toLocalTime().isBefore(space.getOpeningTime())) {
            throw new IllegalArgumentException(
                    "Booking start time is before space opening time (" + space.getOpeningTime() + ").");
        }
        if (space.getClosingTime() != null && endTime.toLocalTime().isAfter(space.getClosingTime())) {
            throw new IllegalArgumentException(
                    "Booking end time is after space closing time (" + space.getClosingTime() + ").");
        }

        long overlappingBookings = bookingRepository.countOverlappingBookings(
                space.getId(),
                startTime,
                endTime,
                BookingStatus.CONFIRMED);

        if (overlappingBookings > 0) {
            throw new SlotUnavailableException("The requested time slot for this space is not available.");
        }
        Duration duration = Duration.between(startTime, endTime);
        BigDecimal hours = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2,
                BigDecimal.ROUND_HALF_UP);
        BigDecimal totalPrice = space.getPricePerHour().multiply(hours);

        Booking booking = new Booking(
                currentUser,
                space,
                startTime,
                endTime,
                BookingStatus.CONFIRMED,
                totalPrice);

        Booking savedBooking = bookingRepository.save(booking);

        return convertToDto(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsForCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        List<Booking> bookings = bookingRepository.findByUserId(userDetails.getId());
        return bookings.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingsForAdmin() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public Optional<BookingResponseDto> cancelBooking(Long bookingId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    if (!booking.getUser().getId().equals(userDetails.getId())) {
                        log.warn("User {} attempted to cancel booking {} owned by user {}",
                                userDetails.getId(), bookingId, booking.getUser().getId());
                        throw new org.springframework.security.access.AccessDeniedException(
                                "You are not authorized to cancel this booking.");
                    }
                    if (booking.getStatus() == BookingStatus.CANCELLED
                            || booking.getStatus() == BookingStatus.COMPLETED) {
                        throw new IllegalStateException(
                                "Booking cannot be cancelled as it's already " + booking.getStatus());
                    }
                    if (booking.getStartTime().isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException("Cannot cancel a booking that has already started or passed.");
                    }

                    booking.setStatus(BookingStatus.CANCELLED);
                    Booking cancelledBooking = bookingRepository.save(booking);
                    return convertToDto(cancelledBooking);
                });
    }

}
