package pl.bielamarcin.shippingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bielamarcin.shippingservice.model.Shipment;

import java.util.List;
import java.util.UUID;

public interface ShipmentRespository extends JpaRepository<Shipment, UUID> {
    List<Shipment> findByOrderId(UUID orderId);
}
