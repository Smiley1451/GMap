CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE places (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    attrs JSONB,
    geom GEOGRAPHY(Point, 4326) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicle_locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_id TEXT NOT NULL UNIQUE,
    geom GEOGRAPHY(Point, 4326) NOT NULL,
    speed REAL,
    bearing REAL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX places_geom_idx ON places USING GIST (geom);
CREATE INDEX vehicle_locations_geom_idx ON vehicle_locations USING GIST (geom);
