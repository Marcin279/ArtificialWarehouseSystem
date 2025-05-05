package pl.bielamarcin.shippingservice.service;

import org.springframework.stereotype.Service;
import pl.bielamarcin.shippingservice.dto.ShipmentReqDTO;
import pl.bielamarcin.shippingservice.dto.ShipmentRespDTO;
import pl.bielamarcin.shippingservice.mapper.ShipmentMapper;
import pl.bielamarcin.shippingservice.mapper.ShippingLabelMapper;
import pl.bielamarcin.shippingservice.model.Shipment;
import pl.bielamarcin.shippingservice.model.ShipmentStatus;
import pl.bielamarcin.shippingservice.repository.ShipmentRespository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    private final ShipmentRespository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final ShippingLabelService shippingLabelService;

    public ShipmentService(ShipmentRespository shipmentRepository,
                           ShipmentMapper shipmentMapper,
                           ShippingLabelService shippingLabelService) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
        this.shippingLabelService = shippingLabelService;
    }

    public ShipmentRespDTO createShipment(ShipmentReqDTO shipmentReqDTO) {
        Shipment shipment = shipmentMapper.toEntity(shipmentReqDTO);
        Shipment savedShipment = shipmentRepository.save(shipment);
        shippingLabelService.createShippingLabel(savedShipment.getId());
        return shipmentMapper.toDto(savedShipment);
    }

    public Optional<ShipmentRespDTO> findShipmentById(UUID id) {
        return shipmentRepository.findById(id)
                .map(shipmentMapper::toDto);
    }

    public List<ShipmentRespDTO> getShipmentsByOrderId(UUID orderId) {
        return shipmentRepository.findByOrderId(orderId).stream()
                .map(shipmentMapper::toDto)
                .toList();
    }

    public byte[] generateShippingLabel(UUID id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono przesyłki o ID: " + id));

        try {
            String labelContent = "ETYKIETA WYSYŁKOWA\n" +
                    "------------------------\n" +
                    "ID Przesyłki: " + shipment.getId() + "\n" +
                    "ID Zamówienia: " + shipment.getOrderId() + "\n" +
                    "Status: " + shipment.getStatus() + "\n" +
                    "Adres Dostawy: " + shipment.getShippingAddress() + "\n" +
                    "Data Utworzenia: " + shipment.getCreatedAt() + "\n" +
                    "Przewidywana Dostawa: " + shipment.getDeliveredAt();

            return labelContent.getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania etykiety wysyłkowej", e);
        }
    }

    public ShipmentRespDTO updateShipmentStatus(UUID id, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono przesyłki o ID: " + id));

        shipment.setStatus(status);
        Shipment updatedShipment = shipmentRepository.save(shipment);

        return shipmentMapper.toDto(updatedShipment);
    }
}