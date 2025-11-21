package com.prohands.GMap.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("places")
public record Place(
    @Id UUID id,
    String name,
    String attrs, // JSONB
    String geom, // GEOGRAPHY(Point,4326)
    Instant createdAt
) {}
