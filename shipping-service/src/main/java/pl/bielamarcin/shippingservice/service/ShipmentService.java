package pl.bielamarcin.shippingservice.service;

import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
import pl.bielamarcin.shippingservice.dto.ShipmentReqDTO;
import pl.bielamarcin.shippingservice.dto.ShipmentRespDTO;
import pl.bielamarcin.shippingservice.model.Shipment;
import pl.bielamarcin.shippingservice.model.ShipmentStatus;
import pl.bielamarcin.shippingservice.repository.ShipmentRespository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShipmentService {

//    private final ShipmentRespository shipmentRepository;
//    private final WebClient webClient;
//
//    public ShipmentService(ShipmentRespository shipmentRepository, WebClient.Builder webClientBuilder) {
//        this.shipmentRepository = shipmentRepository;
//        this.webClient = webClientBuilder.baseUrl("http://order-service/api/orders").build();
//    }
//
//    public ShipmentRespDTO createShipment(ShipmentReqDTO shipmentReqDTO) {
//        // Pobranie szczegółów zamówienia z order-service
//        OrderDetailsDTO orderDetails = webClient.get()
//                .uri("/{orderId}", shipmentReqDTO.orderId())
//                .retrieve()
//                .bodyToMono(OrderDetailsDTO.class)
//                .block();
//
//        if (orderDetails == null) {
//            throw new RuntimeException("Order not found for ID: " + shipmentReqDTO.orderId());
//        }
//
//        // Tworzenie nowego shipmentu
//        Shipment shipment = new Shipment();
//        shipment.setOrderId(shipmentReqDTO.orderId());
//        shipment.set(shipmentReqDTO.shippingAddress());
//        shipment.setStatus(ShipmentStatus.PENDING);
//        shipment.setCreatedAt(LocalDateTime.now());
//
//        // Zapis shipmentu w bazie danych
//        Shipment savedShipment = shipmentRepository.save(shipment);
//
//        // Mapowanie na DTO odpowiedzi
//        return new ShipmentRespDTO(
//                savedShipment.getId(),
//                savedShipment.getOrderId(),
//                savedShipment.getShippingAddress(),
//                savedShipment.getStatus(),
//                savedShipment.getCreatedAt(),
//                savedShipment.getDeliveredAt()
//        );
//    }
}