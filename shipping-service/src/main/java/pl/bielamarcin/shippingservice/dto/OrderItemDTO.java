package pl.bielamarcin.shippingservice.dto;

import java.util.UUID;

public class OrderItemDTO {
    private UUID id;
    private UUID productId;
    private Integer quantity;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

}