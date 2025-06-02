package pl.bielamarcin.warehouseservice.dto;

import java.util.UUID;

public record UnavailableItemDTO(UUID productId, int requestedQuantity, int availableQuantity) {
}