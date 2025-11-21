package com.prohands.GMap.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("vehicle_locations")
public record VehicleLocation(
        @Id UUID id,
        String entityId,
        String geom, // GEOGRAPHY(Point,4326) -> Format: "POINT(longitude latitude)"
        Double speed,
        Double bearing,
        Instant updatedAt
) {
    // Helper to get Latitude from WKT String
    public double lat() {
        return parseCoordinate(1);
    }

    // Helper to get Longitude from WKT String
    public double lng() {
        return parseCoordinate(0);
    }

    private double parseCoordinate(int index) {
        if (geom == null || !geom.contains("(") || !geom.contains(")")) {
            return 0.0;
        }
        try {
            String cleanCoords = geom.substring(geom.indexOf('(') + 1, geom.indexOf(')'));
            String[] parts = cleanCoords.trim().split("\\s+");
            return Double.parseDouble(parts[index]);
        } catch (Exception e) {
            return 0.0;
        }
    }
}