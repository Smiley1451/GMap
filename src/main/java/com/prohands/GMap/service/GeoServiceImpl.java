package com.prohands.GMap.service;


import com.prohands.GMap.model.*;
import com.prohands.GMap.repository.PlaceRepository;
import com.prohands.GMap.repository.VehicleLocationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Service
public class GeoServiceImpl implements GeoService {

    private static final double EARTH_RADIUS = 6371e3; // metres

    private final PlaceRepository placeRepository;
    private final VehicleLocationRepository vehicleLocationRepository;
    private final KafkaProducerService kafkaProducerService;
    private final List<Place> places;

    public GeoServiceImpl(
            PlaceRepository placeRepository,
            VehicleLocationRepository vehicleLocationRepository,
            KafkaProducerService kafkaProducerService,
            List<Place> places) {
        this.placeRepository = placeRepository;
        this.vehicleLocationRepository = vehicleLocationRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.places = places;
    }

    @Override
    public Mono<Double> getDistance(double fromLat, double fromLng, double toLat, double toLng) {
        double dLat = Math.toRadians(toLat - fromLat);
        double dLng = Math.toRadians(toLng - fromLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(fromLat)) * Math.cos(Math.toRadians(toLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Mono.just(EARTH_RADIUS * c);
    }

    @Override
    public Flux<Place> getNearest(double lat, double lng, int k) {
        return Flux.fromIterable(places)
                .flatMap(place -> getDistance(lat, lng, place.lat(), place.lng())
                        .map(distance -> new PlaceWithDistance(place, distance)))
                .sort(Comparator.comparing(PlaceWithDistance::distance))
                .take(k)
                .map(PlaceWithDistance::place);
    }

    @Override
    public Flux<Place> getNearestFromDb(double lat, double lng, int k) {
        return placeRepository.findNearest(lng, lat, k);
    }

    @Override
    public Mono<Void> updateLocation(LocationEvent locationEvent) {
        return kafkaProducerService.sendLocationUpdate(locationEvent);
    }

    @Override
    public Mono<LocationEvent> getLocation(String entityId) {
        return vehicleLocationRepository.findLastKnownLocation(entityId)
                .map(vehicleLocation -> new LocationEvent(vehicleLocation.getEntityId(), vehicleLocation.getLat(), vehicleLocation.getLng()));
    }

    private record PlaceWithDistance(Place place, double distance) {}
}
