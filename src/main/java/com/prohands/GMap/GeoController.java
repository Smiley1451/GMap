package com.prohands.GMap;


import com.prohands.GMap.model.LocationEvent;
import com.prohands.GMap.model.Place;
import com.prohands.GMap.service.GeoService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/geo")
public class GeoController {

    private final GeoService geoService;

    public GeoController(GeoService geoService) {
        this.geoService = geoService;
    }

    @PostMapping("/distance")
    public Mono<Double> getDistance(@RequestBody DistanceRequest request) {
        return geoService.getDistance(
                request.from().lat(), request.from().lng(),
                request.to().lat(), request.to().lng()
        );
    }

    @PostMapping("/nearest")
    public Flux<Place> getNearest(@RequestBody LocationEvent from, @RequestParam(defaultValue = "5") int k) {
        return geoService.getNearest(from.lat(), from.lng(), k);
    }

    @PostMapping("/nearest-from-db")
    public Flux<Place> getNearestFromDb(@RequestBody LocationEvent from, @RequestParam(defaultValue = "5") int k) {
        return geoService.getNearestFromDb(from.lat(), from.lng(), k);
    }

    @PostMapping("/location/update")
    public Mono<Void> updateLocation(@RequestBody LocationEvent locationEvent) {
        return geoService.updateLocation(locationEvent);
    }

    @GetMapping("/location/{entity_id}")
    public Mono<LocationEvent> getLocation(@PathVariable("entity_id") String entityId) {
        return geoService.getLocation(entityId);
    }

    @GetMapping("/stream/locations")
    public Flux<LocationEvent> streamLocations() {

        return Flux.empty();
    }
    public record DistanceRequest(LocationEvent from, LocationEvent to) {}
}
