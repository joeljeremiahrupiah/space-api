package com.coworking.coworking_booking_system.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.coworking.coworking_booking_system.dto.SpaceSearchCriteria;
import com.coworking.coworking_booking_system.entity.Amenity;
import com.coworking.coworking_booking_system.entity.Space;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SpaceSpecification {

    public static Specification<Space> findByCriteria(SpaceSearchCriteria criteria) {
        return (Root<Space> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> orPredicates = new ArrayList<>();

            Predicate locationFilter = null;
            if (StringUtils.hasText(criteria.getLocationKeyword())) {
                Predicate locationPredicate = cb.like(cb.lower(root.get("locationDescription")),
                        "%" + criteria.getLocationKeyword().toLowerCase() + "%");
                Predicate namePredicate = cb.like(cb.lower(root.get("name")),
                        "%" + criteria.getLocationKeyword().toLowerCase() + "%");
                locationFilter = cb.or(locationPredicate, namePredicate);
            }

            if (!CollectionUtils.isEmpty(criteria.getTypes())) {
                orPredicates.add(root.get("type").in(criteria.getTypes()));
            }

            if (criteria.getMinCapacity() != null && criteria.getMinCapacity() > 0) {
                orPredicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), criteria.getMinCapacity()));
            }

            if (criteria.getMaxPricePerHour() != null) {
                orPredicates.add(cb.lessThanOrEqualTo(root.get("pricePerHour"), criteria.getMaxPricePerHour()));
            }

            if (!CollectionUtils.isEmpty(criteria.getRequiredAmenities())) {
                Join<Space, Amenity> amenityJoin = root.join("amenities", JoinType.INNER);
                orPredicates.add(amenityJoin.get("name").in(criteria.getRequiredAmenities()));
                query.distinct(true);
            }

            Predicate mainOrPredicate = null;
            if (!orPredicates.isEmpty()) {
                mainOrPredicate = cb.or(orPredicates.toArray(new Predicate[0]));
            }

            if (locationFilter != null) {
                if (mainOrPredicate != null) {
                    return cb.and(locationFilter, mainOrPredicate);
                } else {
                    return locationFilter;
                }
            } else {
                if (mainOrPredicate != null) {
                    return mainOrPredicate;
                } else {
                    return cb.conjunction();
                }
            }
        };
    }

}
