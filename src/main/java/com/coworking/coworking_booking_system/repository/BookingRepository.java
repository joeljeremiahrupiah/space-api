package com.coworking.coworking_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coworking.coworking_booking_system.entity.Booking;
import com.coworking.coworking_booking_system.entity.Space;
import com.coworking.coworking_booking_system.entity.User;
import com.coworking.coworking_booking_system.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findBySpace(Space space);

    List<Booking> findByUserId(Long userId);

    List<Booking> findBySpaceId(Long spaceId);

    // Finds CONFIRMED bookings for a specific space that overlap with a given time
    // range.
    @Query("SELECT b FROM Booking b WHERE b.space.id = :spaceId " +
            "AND b.status = :status " +
            "AND b.startTime < :requestedEndTime " +
            "AND b.endTime > :requestedStartTime")
    List<Booking> findOverlappingBookings(
            @Param("spaceId") Long spaceId,
            @Param("requestedStartTime") LocalDateTime requestedStartTime,
            @Param("requestedEndTime") LocalDateTime requestedEndTime,
            @Param("status") BookingStatus status);

    // Simplified check - just counts overlapping bookings
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.space.id = :spaceId " +
            "AND b.status = :status " +
            "AND b.startTime < :requestedEndTime " +
            "AND b.endTime > :requestedStartTime")
    long countOverlappingBookings(
            @Param("spaceId") Long spaceId,
            @Param("requestedStartTime") LocalDateTime requestedStartTime,
            @Param("requestedEndTime") LocalDateTime requestedEndTime,
            @Param("status") BookingStatus status);

}
