package com.prohands.GMap.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("places")
public record Place(
        @Id UUID id,
        String name,
        String attrs,
        String geom,
        Instant createdAt
) {


    public double lat() {
        return parseCoordinate(1);
    }


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