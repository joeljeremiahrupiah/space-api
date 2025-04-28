package com.coworking.coworking_booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingInitiationResponse {

    private Long bookingId;
    private String pesapalRedirectUrl;

}
