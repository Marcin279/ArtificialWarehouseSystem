package pl.bielamarcin.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bielamarcin.warehouseservice.model.Location;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}