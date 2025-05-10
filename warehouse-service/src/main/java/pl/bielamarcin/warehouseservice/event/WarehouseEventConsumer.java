package pl.bielamarcin.warehouseservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.bielamarcin.warehouseservice.dto.ItemDTO;
import pl.bielamarcin.warehouseservice.service.InventoryService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WarehouseEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseEventConsumer.class);

    private final InventoryService inventoryService;

    public WarehouseEventConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-created-topic", groupId = "warehouse-service")
    public void handleOrderCreated(String message) {
        logger.info("Otrzymano zdarzenie order-created: {}", message);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Dodaj obsługę Java Time
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);

            // Teraz możesz użyć obiektu event, np.:
            UUID orderId = event.getId();
            List<ItemDTO> items = event.getOrderItems().stream()
                    .map(item -> new ItemDTO(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());

            // Wywołaj rezerwację produktów
            inventoryService.reserveItems(orderId, items);

        } catch (Exception e) {
            logger.error("Błąd podczas przetwarzania zdarzenia order-created", e);
        }
    }

    @KafkaListener(topics = "product-stock-updated", groupId = "warehouse-service")
    public void handleProductStockUpdated(String message) {
        logger.info("Otrzymano zdarzenie product-stock-updated: {}", message);

//        try {
//            // Deserializacja wiadomości JSON na obiekt ProductStockUpdateEvent
//            ObjectMapper objectMapper = new ObjectMapper();
//            ProductStockUpdateEvent event = objectMapper.readValue(message, ProductStockUpdateEvent.class);
//
//            logger.info("Przetwarzanie aktualizacji stanu dla produktu: {}, ilość: {}, typ operacji: {}",
//                    event.getProductId(), event.getQuantity(), event.getOperationType());
//
//            // Wywołanie serwisu do aktualizacji stanu magazynowego
//            switch (event.getOperationType()) {
//                case ADD:
//                    inventoryService.addStock(event.getProductId(), event.getQuantity());
//                    break;
//                case REMOVE:
//                    inventoryService.removeStock(event.getProductId(), event.getQuantity());
//                    break;
//                case SET:
//                    inventoryService.setStock(event.getProductId(), event.getQuantity());
//                    break;
//                default:
//                    logger.warn("Nieznany typ operacji: {}", event.getOperationType());
//            }
//
//            logger.info("Pomyślnie zaktualizowano stan magazynowy dla produktu: {}", event.getProductId());
//        } catch (JsonProcessingException e) {
//            logger.error("Błąd podczas deserializacji wiadomości: {}", message, e);
//        } catch (Exception e) {
//            logger.error("Błąd podczas przetwarzania aktualizacji stanu magazynowego", e);
//        }
    }
}