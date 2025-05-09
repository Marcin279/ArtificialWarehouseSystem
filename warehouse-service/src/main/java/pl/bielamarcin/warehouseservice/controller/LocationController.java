package pl.bielamarcin.warehouseservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bielamarcin.warehouseservice.dto.LocationDTO;
import pl.bielamarcin.warehouseservice.dto.LocationReqDTO;
import pl.bielamarcin.warehouseservice.service.LocationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouse/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable UUID id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationReqDTO locationReqDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationService.createLocation(locationReqDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(
            @PathVariable UUID id,
            @RequestBody LocationReqDTO locationReqDTO) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationReqDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{locationId}/items/{itemId}")
    public ResponseEntity<Void> assignItemToLocation(
            @PathVariable UUID locationId,
            @PathVariable UUID itemId) {
        locationService.assignItemToLocation(itemId, locationId);
        return ResponseEntity.ok().build();
    }
}