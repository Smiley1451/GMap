package com.prohands.GMap.service;

import com.prohands.GMap.model.LocationEvent;
import com.prohands.GMap.model.VehicleLocation;
import com.prohands.GMap.repository.VehicleLocationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.time.Instant;

@Service
public class KafkaConsumerService {

    private final ReactiveKafkaConsumerTemplate<String, LocationEvent> reactiveKafkaConsumerTemplate;
    private final VehicleLocationRepository vehicleLocationRepository;
    private final Sinks.Many<LocationEvent> locationEventSink;

    public KafkaConsumerService(
            ReactiveKafkaConsumerTemplate<String, LocationEvent> reactiveKafkaConsumerTemplate,
            VehicleLocationRepository vehicleLocationRepository,
            Sinks.Many<LocationEvent> locationEventSink) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
        this.vehicleLocationRepository = vehicleLocationRepository;
        this.locationEventSink = locationEventSink;
    }

    @PostConstruct
    public void consumeLocationUpdates() {
        reactiveKafkaConsumerTemplate
                .receive()
                .flatMap(record -> {
                    LocationEvent event = record.value();

                    // 1. Create PostGIS WKT String: "POINT(lng lat)"
                    String wktGeom = String.format("POINT(%f %f)", event.lng(), event.lat());

                    // 2. Create NEW Record (Records are immutable, no setters)
                    VehicleLocation vehicleLocation = new VehicleLocation(
                            null,               // ID (let DB generate it)
                            event.entityId(),   // Entity ID
                            wktGeom,           // Geom String
                            event.speed(),      // Speed
                            event.bearing(),    // Bearing
                            Instant.now()       // Updated At
                    );

                    return vehicleLocationRepository.save(vehicleLocation)
                            .thenReturn(event);
                })
                .doOnNext(locationEventSink::tryEmitNext)
                .subscribe();
    }
}