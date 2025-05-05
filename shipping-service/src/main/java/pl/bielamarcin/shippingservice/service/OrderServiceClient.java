package pl.bielamarcin.shippingservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.bielamarcin.shippingservice.dto.OrderDTO;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceClient.class);
    private final RestTemplate restTemplate;
    private final String orderServiceUrl;

    public OrderServiceClient(RestTemplate restTemplate, @Value("${services.order-service.url}") String orderServiceUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceUrl = orderServiceUrl;
    }

    public Optional<OrderDTO> getOrderById(UUID orderId) {
        try {
            logger.info("Pobieranie zamówienia o ID: {}", orderId);
            OrderDTO order = restTemplate.getForObject(orderServiceUrl + "/api/orders/" + orderId, OrderDTO.class);

            if (order != null) {
                logger.info("Pobrano zamówienie: {}", order.getId());
                return Optional.of(order);
            }
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            logger.error("Błąd podczas pobierania zamówienia {}: {}", orderId, e.getMessage());
            return Optional.empty();
        }
    }
}