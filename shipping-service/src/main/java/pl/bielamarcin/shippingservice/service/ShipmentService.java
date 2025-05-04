package pl.bielamarcin.shippingservice.service;

import org.springframework.stereotype.Service;
import pl.bielamarcin.shippingservice.dto.ShipmentReqDTO;
import pl.bielamarcin.shippingservice.dto.ShipmentRespDTO;
import pl.bielamarcin.shippingservice.model.Shipment;
import pl.bielamarcin.shippingservice.model.ShipmentStatus;
import pl.bielamarcin.shippingservice.repository.ShipmentRespository;

import java.time.LocalDateTime;

@Service
public class ShipmentService {

    private final ShipmentRespository shipmentRepository;

    public ShipmentService(ShipmentRespository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public ShipmentRespDTO createShipment(ShipmentReqDTO shipmentReqDTO) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(shipmentReqDTO.orderId());
        shipment.setShippingAddress(shipmentReqDTO.shippingAddress());
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setCreatedAt(LocalDateTime.now());

        Shipment savedShipment = shipmentRepository.save(shipment);

        return new ShipmentRespDTO(savedShipment.getId(), savedShipment.getOrderId(), savedShipment.getShippingAddress(), savedShipment.getStatus(), savedShipment.getCreatedAt(), savedShipment.getDeliveredAt());
    }
}