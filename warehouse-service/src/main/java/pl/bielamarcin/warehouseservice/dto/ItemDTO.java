package pl.bielamarcin.warehouseservice.dto;

import java.util.UUID;

public record ItemDTO(UUID productId, int quantity) {
}