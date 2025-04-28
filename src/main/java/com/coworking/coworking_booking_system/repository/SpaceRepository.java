package com.coworking.coworking_booking_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.coworking.coworking_booking_system.entity.Space;
import com.coworking.coworking_booking_system.enums.SpaceType;

public interface SpaceRepository extends JpaRepository<Space, Long>, JpaSpecificationExecutor<Space> {

    List<Space> findByType(SpaceType type);

    List<Space> findByCapacityGreaterThanEqual(Integer minCapacity);

}
