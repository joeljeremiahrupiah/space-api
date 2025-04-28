package com.coworking.coworking_booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SlotUnavailableException extends RuntimeException {
    
    public SlotUnavailableException(String message) {
        super(message);
    }

}
