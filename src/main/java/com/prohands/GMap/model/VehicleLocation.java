package com.prohands.GMap.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("vehicle_locations")

public record VehicleLocation(
    @Id UUID id,
    String entityId,
    String geom, // GEOGRAPHY(Point,4326)
    Double speed,
    Double bearing,
    Instant updatedAt
) {}
