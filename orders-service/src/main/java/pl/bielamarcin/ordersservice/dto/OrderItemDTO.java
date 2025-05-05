package pl.bielamarcin.ordersservice.dto;

import java.util.UUID;

public record OrderItemDTO(UUID id, UUID productId, Integer quantity) {
}