package com.coworking.coworking_booking_system.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.coworking.coworking_booking_system.enums.SpaceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "spaces")
@Data
@NoArgsConstructor
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Space name cannot be blank")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Space type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SpaceType type;

    @NotBlank(message = "Location description cannot be blank")
    @Size(max = 255)
    @Column(nullable = false)
    private String locationDescription;

    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity;

    @NotNull(message = "Price per hour cannot be null")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "space_amenities", joinColumns = @JoinColumn(name = "space_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private Set<Amenity> amenities = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 255)
    private String imageUrl;

    public Space(String name, SpaceType type, String locationDescription, Integer capacity, BigDecimal pricePerHour,
            LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.type = type;
        this.locationDescription = locationDescription;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

}
