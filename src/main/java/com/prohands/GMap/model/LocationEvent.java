package com.prohands.GMap.model;

import java.time.Instant;

public record LocationEvent(
    String entityId,
    double lat,
    double lng,
    Double speed,
    Double bearing,
    Instant ts
) {}
