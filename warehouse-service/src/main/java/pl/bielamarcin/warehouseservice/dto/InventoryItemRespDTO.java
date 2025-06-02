package pl.bielamarcin.warehouseservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryItemRespDTO(
        UUID id,
        UUID productId,
        Integer totalQuantity,
        Integer availableQuantity,
        Integer reservedQuantity,
        LocationDTO location,
        LocalDateTime lastUpdated
) {
}