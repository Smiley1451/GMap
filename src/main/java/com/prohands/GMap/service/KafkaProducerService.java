package com.prohands.GMap.service;

import com.prohands.GMap.model.LocationEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
