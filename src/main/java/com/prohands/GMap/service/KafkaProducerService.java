package com.prohands.GMap.service;

import com.prohands.GMap.model.LocationEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;



@Service
public class KafkaProducerService {

    private final ReactiveKafkaProducerTemplate<String, LocationEvent> reactiveKafkaProducerTemplate;

    public KafkaProducerService(ReactiveKafkaProducerTemplate<String, LocationEvent> reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    public Mono<Void> sendLocationUpdate(LocationEvent locationEvent) {
        return reactiveKafkaProducerTemplate.send("location-updates", locationEvent).then();
    }
}
