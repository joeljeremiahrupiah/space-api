package com.coworking.coworking_booking_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.coworking.coworking_booking_system.enums.SpaceType;

import lombok.Data;

@Data
public class SpaceSearchCriteria {

    private String locationKeyword;
    private List<SpaceType> types;
    private Integer minCapacity;
    private BigDecimal maxPricePerHour;
    private List<String> requiredAmenities;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime desiredStartTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime desiredEndTime;

}
