package pl.bielamarcin.productsservice.dto;

import pl.bielamarcin.productsservice.model.ProductCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRespDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        ProductCategory category
) {
}
