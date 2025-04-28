package com.coworking.coworking_booking_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmenityDto {

    private Long id;

    @NotBlank(message = "Amenity name cannot be blank")
    @Size(max = 100)
    private String name;

}
