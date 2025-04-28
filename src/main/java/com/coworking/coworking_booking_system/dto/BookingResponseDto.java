package com.coworking.coworking_booking_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.coworking.coworking_booking_system.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDto {

    private Long id;

    private Long userId;
    private String username;

    private Long spaceId;
    private String spaceName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

}
