package com.prohands.GMap.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prohands.GMap.model.LocationEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class LocationWebSocketHandler implements WebSocketHandler {

    private final Sinks.Many<LocationEvent> locationEventSink;
    private final ObjectMapper objectMapper;

    public LocationWebSocketHandler(Sinks.Many<LocationEvent> locationEventSink, ObjectMapper objectMapper) {
        this.locationEventSink = locationEventSink;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<String> locationEvents = locationEventSink.asFlux()
                .map(this::toJson);

        return session.send(locationEvents.map(session::textMessage));
    }

    private String toJson(LocationEvent locationEvent) {
        try {
            return objectMapper.writeValueAsString(locationEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
