package com.prohands.GMap.repository;


import com.prohands.GMap.model.Place;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface PlaceRepository extends ReactiveCrudRepository<Place, UUID> {


    @Query("""
        SELECT id, name, attrs, ST_AsText(geom) as geom, created_at 
        FROM places 
        ORDER BY geom <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography 
        LIMIT :k
    """)
    Flux<Place> findNearest(double lng, double lat, int k);


    @Query("""
        INSERT INTO places (id, name, attrs, geom, created_at) 
        VALUES (:#{#p.id}, :#{#p.name}, :#{#p.attrs}::jsonb, ST_GeographyFromText(:#{#p.geom}), :#{#p.createdAt})
        ON CONFLICT (id) 
        DO UPDATE SET 
            name = EXCLUDED.name, 
            attrs = EXCLUDED.attrs, 
            geom = EXCLUDED.geom, 
            created_at = EXCLUDED.created_at
    """)
    Mono<Void> savePlace(Place p);
}