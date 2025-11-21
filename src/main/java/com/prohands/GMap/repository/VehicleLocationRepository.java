package com.prohands.GMap.repository;


import com.prohands.GMap.model.VehicleLocation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VehicleLocationRepository extends ReactiveCrudRepository<VehicleLocation, UUID> {

    @Query("SELECT * FROM vehicle_locations WHERE entity_id = :entityId ORDER BY updated_at DESC LIMIT 1")
    Mono<VehicleLocation> findLastKnownLocation(String entityId);
}
