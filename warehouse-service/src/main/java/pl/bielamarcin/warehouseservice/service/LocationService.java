package pl.bielamarcin.warehouseservice.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.bielamarcin.warehouseservice.dto.LocationDTO;
import pl.bielamarcin.warehouseservice.dto.LocationReqDTO;
import pl.bielamarcin.warehouseservice.mapper.LocationMapper;
import pl.bielamarcin.warehouseservice.model.InventoryItem;
import pl.bielamarcin.warehouseservice.model.Location;
import pl.bielamarcin.warehouseservice.repository.InventoryItemRepository;
import pl.bielamarcin.warehouseservice.repository.LocationRepository;

import java.util.List;
import java.util.UUID;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository,
                           InventoryItemRepository inventoryItemRepository,
                           LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.locationMapper = locationMapper;
    }

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toDto)
                .toList();
    }

    public LocationDTO getLocationById(UUID id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return locationMapper.toDto(location);
    }

    @Transactional
    public LocationDTO createLocation(LocationReqDTO locationReqDTO) {
        Location location = locationMapper.toEntity(locationReqDTO);

        // Zapewniamy, że ID jest generowane
        if (location.getId() == null) {
            location.setId(UUID.randomUUID());
        }

        Location savedLocation = locationRepository.save(location);
        return locationMapper.toDto(savedLocation);
    }

    @org.springframework.transaction.annotation.Transactional
    public LocationDTO updateLocation(UUID id, LocationReqDTO locationReqDTO) {
        locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono lokalizacji o ID: " + id));

        Location location = locationMapper.toEntity(locationReqDTO);
        location.setId(id);

        Location updatedLocation = locationRepository.save(location);
        return locationMapper.toDto(updatedLocation);
    }

    @Transactional
    public void deleteLocation(UUID id) {
        locationRepository.deleteById(id);
    }

    @Transactional
    public void assignItemToLocation(UUID itemId, UUID locationId) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono przedmiotu o ID: " + itemId));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono lokalizacji o ID: " + locationId));

        item.setLocation(location);
        inventoryItemRepository.save(item);
    }
}
