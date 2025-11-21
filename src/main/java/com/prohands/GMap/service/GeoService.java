package com.prohands.GMap.service;


import com.prohands.GMap.model.LocationEvent;
import com.prohands.GMap.model.Place;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GeoService {

    Mono<Double> getDistance(double fromLat, double fromLng, double toLat, double toLng);

    Flux<Place> getNearest(double lat, double lng, int k);

    Flux<Place> getNearestFromDb(double lat, double lng, int k);

    Mono<Void> updateLocation(LocationEvent locationEvent);

    Mono<LocationEvent> getLocation(String entityId);
}
