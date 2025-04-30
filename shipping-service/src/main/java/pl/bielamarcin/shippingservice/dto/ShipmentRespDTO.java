package pl.bielamarcin.shippingservice.dto;

import pl.bielamarcin.shippingservice.model.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentRespDTO(
        UUID id,
        UUID orderId,
        String shippingAddress,
        ShipmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime deliveredAt
) {
}
