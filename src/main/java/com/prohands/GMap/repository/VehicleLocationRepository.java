package com.prohands.GMap.repository;

import com.prohands.GMap.model.VehicleLocation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VehicleLocationRepository extends ReactiveCrudRepository<VehicleLocation, UUID> {

    @Query("SELECT id, entity_id, ST_AsText(geom) as geom, speed, bearing, updated_at FROM vehicle_locations WHERE entity_id = :entityId ORDER BY updated_at DESC LIMIT 1")
    Mono<VehicleLocation> findLastKnownLocation(String entityId);


    @Query("""
        INSERT INTO vehicle_locations (entity_id, geom, speed, bearing, updated_at) 
        VALUES (:#{#vl.entityId}, ST_GeographyFromText(:#{#vl.geom}), :#{#vl.speed}, :#{#vl.bearing}, :#{#vl.updatedAt})
        ON CONFLICT (entity_id) 
        DO UPDATE SET 
            geom = EXCLUDED.geom, 
            speed = EXCLUDED.speed, 
            bearing = EXCLUDED.bearing, 
            updated_at = EXCLUDED.updated_at
    """)
    Mono<Void> saveLocation(VehicleLocation vl);
}