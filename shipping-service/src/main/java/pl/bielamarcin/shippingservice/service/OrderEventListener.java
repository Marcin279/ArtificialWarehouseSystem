package pl.bielamarcin.shippingservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.bielamarcin.shippingservice.dto.OrderDTO;
import pl.bielamarcin.shippingservice.dto.ShipmentReqDTO;

@Component
public class OrderEventListener {

    private final ShipmentService shipmentService;
    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

    public OrderEventListener(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @KafkaListener(topics = "order-created-topic", groupId = "shipping-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleOrderCreatedEvent(OrderDTO orderDTO) {
        try {
            logger.info("Otrzymano zamowienie: {}", orderDTO.id());
            ShipmentReqDTO shipmentReq = new ShipmentReqDTO(orderDTO.id(), orderDTO.shippingAddress());

            shipmentService.createShipment(shipmentReq);
        } catch (Exception e) {
            logger.error("Błąd podczas przetwarzania zamówienia: {}", orderDTO.id(), e);
        }
    }
}