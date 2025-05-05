package pl.bielamarcin.shippingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bielamarcin.shippingservice.model.ShippingLabel;

import java.util.Optional;
import java.util.UUID;

public interface ShippingLabelRepository extends JpaRepository<ShippingLabel, UUID> {
    Optional<ShippingLabel> findByShipmentId(UUID shipmentId);
}