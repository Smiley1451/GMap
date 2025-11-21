package com.prohands.GMap.config;

import com.prohands.GMap.model.LocationEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ReceiverOptions<String, LocationEvent> kafkaReceiverOptions(
            KafkaProperties kafkaProperties) {
        ReceiverOptions<String, LocationEvent> basicReceiverOptions =
                ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singleton("location-updates"));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, LocationEvent> reactiveKafkaConsumerTemplate(
            ReceiverOptions<String, LocationEvent> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }

    @Bean
    public Sinks.Many<LocationEvent> locationEventSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
