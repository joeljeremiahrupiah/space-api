package com.coworking.coworking_booking_system.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

import com.coworking.coworking_booking_system.enums.SpaceType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceDto {

    private Long id;

    @NotBlank(message = "Space name cannot be blank")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Space type cannot be null")
    private SpaceType type;

    @NotBlank(message = "Location description cannot be blank")
    @Size(max = 255)
    private String locationDescription;

    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Price per hour cannot be null")
    private BigDecimal pricePerHour;

    private String openingTime;
    private String closingTime;

    private Set<String> amenityNames;
    private Set<AmenityDto> amenities;

    private String description;

    @Size(max = 255)
    private String imageUrl;

    public LocalTime getOpeningTimeAsLocalTime() {
        return openingTime != null ? LocalTime.parse(openingTime) : null;
    }

    public LocalTime getClosingTimeAsLocalTime() {
        return closingTime != null ? LocalTime.parse(closingTime) : null;
    }

    public void setOpeningTimeFromLocalTime(LocalTime time) {
        this.openingTime = (time != null) ? time.toString() : null;
    }

    public void setClosingTimeFromLocalTime(LocalTime time) {
        this.closingTime = (time != null) ? time.toString() : null;
    }

}
