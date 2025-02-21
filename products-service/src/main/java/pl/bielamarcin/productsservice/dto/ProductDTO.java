package pl.bielamarcin.productsservice.dto;

import pl.bielamarcin.productsservice.model.ProductCategory;

import java.math.BigDecimal;

public record ProductDTO(
    String name,
    String description,
    BigDecimal price,
    Integer quantity,
    ProductCategory category
    ) {}

