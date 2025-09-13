package pl.bielamarcin.productsservice.dto;

import pl.bielamarcin.productsservice.model.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductRespDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        ProductCategory category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
