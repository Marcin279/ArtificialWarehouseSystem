package pl.bielamarcin.shippingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bielamarcin.shippingservice.model.Shipment;

import java.util.UUID;

public interface ShipmentRespository extends JpaRepository<Shipment, UUID> {
}
