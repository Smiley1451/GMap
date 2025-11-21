package com.prohands.GMap.repository;


import com.prohands.GMap.model.Place;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface PlaceRepository extends ReactiveCrudRepository<Place, UUID> {

    @Query("SELECT id, name, ST_X(ST_AsText(geom::geometry)) as lon, ST_Y(ST_AsText(geom::geometry)) as lat, ST_Distance(geom::geography, ST_SetSRID(ST_MakePoint(:lng,:lat),4326)::geography) AS dist_m FROM places ORDER BY geom <-> ST_SetSRID(ST_MakePoint(:lng,:lat),4326)::geography LIMIT :k")
    Flux<Place> findNearest(double lng, double lat, int k);
}
