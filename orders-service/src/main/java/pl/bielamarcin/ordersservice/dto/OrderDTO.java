package pl.bielamarcin.ordersservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
        UUID id,
        String status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String shippingAddress,
        List<OrderItemDTO> orderItems
) {}

//public class OrderDTO {
//    private UUID id;
//    private String status;
//    private BigDecimal totalPrice;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private String shippingAddress;
//    private List<OrderItemDTO> orderItems;
//
//    // Getters and setters
//    public UUID getId() { return id; }
//    public void setId(UUID id) { this.id = id; }
//
//    public String getStatus() { return status; }
//    public void setStatus(String status) { this.status = status; }
//
//    public BigDecimal getTotalPrice() { return totalPrice; }
//    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
//
//    public LocalDateTime getCreatedAt() { return createdAt; }
//    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//
//    public LocalDateTime getUpdatedAt() { return updatedAt; }
//    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
//
//    public String getShippingAddress() { return shippingAddress; }
//    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
//
//    public List<OrderItemDTO> getOrderItems() { return orderItems; }
//    public void setOrderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; }
//}