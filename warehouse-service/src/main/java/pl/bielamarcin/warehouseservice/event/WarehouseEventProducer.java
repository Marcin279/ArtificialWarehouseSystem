package pl.bielamarcin.warehouseservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WarehouseEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public WarehouseEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductReserved(UUID productId, UUID orderId, int quantity) {
        String message = String.format("{\"productId\":\"%s\",\"orderId\":\"%s\",\"quantity\":%d}",
                productId, orderId, quantity);
        kafkaTemplate.send("product-reserved", message);
        logger.info("Wysłano zdarzenie product-reserved: {}", message);
    }

    public void sendProductReleased(UUID productId, UUID orderId, int quantity) {
        String message = String.format("{\"productId\":\"%s\",\"orderId\":\"%s\",\"quantity\":%d}",
                productId, orderId, quantity);
        kafkaTemplate.send("product-released", message);
        logger.info("Wysłano zdarzenie product-released: {}", message);
    }

    public void sendStockUpdated(UUID productId, int newQuantity) {
        String message = String.format("{\"productId\":\"%s\",\"quantity\":%d}",
                productId, newQuantity);
        kafkaTemplate.send("stock-updated", message);
        logger.info("Wysłano zdarzenie stock-updated: {}", message);
    }
}