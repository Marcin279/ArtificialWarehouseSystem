package pl.bielamarcin.warehouseservice.dto;

import java.util.UUID;

public record InventoryItemReqDTO(
        UUID productId,
        Integer reservedQuantity
) {
}