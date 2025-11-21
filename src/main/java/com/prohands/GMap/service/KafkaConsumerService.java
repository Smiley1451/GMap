package com.prohands.GMap.service;

import com.prohands.GMap.model.*;
import com.prohands.GMap.repository.*;
import org.springframework.data.geo.Point;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import jakarta.annotation.PostConstruct;

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
                    LocationEvent locationEvent = record.value();
                    VehicleLocation vehicleLocation = new VehicleLocation();
                    vehicleLocation.setEntityId(locationEvent.entityId());
                    vehicleLocation.setLat(locationEvent.lat());
                    vehicleLocation.setLng(locationEvent.lng());
                    vehicleLocation.setLocation(new Point(locationEvent.lng(), locationEvent.lat()));
                    return vehicleLocationRepository.save(vehicleLocation)
                            .thenReturn(locationEvent);
                })
                .doOnNext(locationEventSink::tryEmitNext)
                .subscribe();
    }
}
