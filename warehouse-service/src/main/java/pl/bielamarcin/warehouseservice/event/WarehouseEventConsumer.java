package pl.bielamarcin.warehouseservice.event;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.bielamarcin.warehouseservice.service.InventoryService;

import java.util.UUID;

@Service
public class WarehouseEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseEventConsumer.class);

    private final InventoryService inventoryService;

    public WarehouseEventConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-created", groupId = "warehouse-service")
    public void handleOrderCreated(String message) {
        logger.info("Otrzymano zdarzenie order-created: {}", message);
        // W rzeczywistym systemie deserializujemy JSON do obiektu
        // Tutaj: uproszczona implementacja
        try {
            // Zakładamy, że message zawiera ID zamówienia
            UUID orderId = UUID.fromString(message);
            // Logika rezerwacji na podstawie zamówienia
            // W rzeczywistości pobralibyśmy szczegóły zamówienia przez API
        } catch (Exception e) {
            logger.error("Błąd podczas przetwarzania zdarzenia order-created", e);
        }
    }

    @KafkaListener(topics = "product-stock-updated", groupId = "warehouse-service")
    public void handleProductStockUpdated(String message) {
        logger.info("Otrzymano zdarzenie product-stock-updated: {}", message);
        // Aktualizacja stanu magazynowego dla produktu
    }
}